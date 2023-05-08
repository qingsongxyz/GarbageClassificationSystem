package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.constant.RedisConstant;
import com.qingsongxyz.exception.ScoreNotEnoughException;
import com.qingsongxyz.exception.StorageNotEnoughException;
import com.qingsongxyz.mapper.OrderMapper;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.pojo.Order;
import com.qingsongxyz.pojo.OrderDetails;
import com.qingsongxyz.service.OrderDetailsService;
import com.qingsongxyz.service.OrderService;
import com.qingsongxyz.service.feignService.StorageFeignService;
import com.qingsongxyz.service.UserService;
import com.qingsongxyz.vo.OrderVO;
import com.qingsongxyz.vo.UserVO;
import io.seata.spring.annotation.GlobalTransactional;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-01-18
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;

    private final OrderDetailsService orderDetailsService;

    private final StorageFeignService storageFeignService;

    private final UserService userService;

    private final StringRedisTemplate stringRedisTemplate;

    private final RedissonClient client;

    private RLock lock;

    public OrderServiceImpl(OrderMapper orderMapper, OrderDetailsService orderDetailsService, @Qualifier("storageFeignService") StorageFeignService storageFeignService, UserService userService, StringRedisTemplate stringRedisTemplate, RedissonClient client) {
        this.orderMapper = orderMapper;
        this.orderDetailsService = orderDetailsService;
        this.storageFeignService = storageFeignService;
        this.userService = userService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.client = client;
    }

    @PostConstruct
    public void init() {
        lock = client.getLock("payOrder");
    }

    @Override
    public int addOrder(Order order) {
        int result = orderMapper.insert(order);
        if (result == 1) {
            List<OrderDetails> orderDetailsList = order.getOrderDetailsList();
            orderDetailsList.forEach(od -> {
                od.setOrderId(order.getId());
            });
            return orderDetailsService.addOrderDetailsList(orderDetailsList);
        }
        return result;
    }

    @GlobalTransactional(name = "gcs-pay-order", rollbackFor = Exception.class)
    @Override
    public void pay(Order order) throws Exception {
        List<OrderDetails> orderDetailsList = order.getOrderDetailsList();

        try {
            lock.lock();

            //1.判断各商品库存是否充足
            if (!checkStorage(orderDetailsList)) {
                throw new StorageNotEnoughException("商品库存不足!!!");
            }
            Long userId = order.getUserId();
            Integer sum = order.getSum();
            UserVO user = userService.getUserById(userId);

            //2.判断用户积分是否足够
            if (sum > user.getScore()) {
                throw new ScoreNotEnoughException("账户积分不足!!!");
            }

            //3.创建订单
            addOrder(order);



            //4.扣减库存
            for (OrderDetails od : orderDetailsList) {
                CommonResult result = storageFeignService.decreaseStorage(od.getGoodId(), od.getCount());
                if(result.getCode() == HttpStatus.HTTP_UNAVAILABLE){
                    throw new DegradeException(result.getMessage());
                }
            }

            //5.扣减用户积分
            userService.decreaseUserScore(userId, sum);
        } finally {
            lock.unlock();
        }
    }


    /**
     * 判断订单中商品库存是否充足
     *
     * @param orderDetailsList 订单详情信息集合
     * @return 商品库存是否充足
     */
    private boolean checkStorage(List<OrderDetails> orderDetailsList) {
        if (ObjectUtil.isNotEmpty(orderDetailsList)) {
            for (OrderDetails od : orderDetailsList) {
                Long goodId = od.getGoodId();
                int count = od.getCount();
                int storage = 0;
                String result = stringRedisTemplate.opsForValue().get(RedisConstant.STORAGE_LIST_KEY + ":" + goodId.toString());
                if (ObjectUtil.isNotNull(result)) {
                    storage = Integer.parseInt(result);
                }

                if (ObjectUtil.isNull(result) || count > storage) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<OrderVO> getOrderList(long userId, int pageNum, int pageSize) {
        List<OrderVO> orderVOList = new ArrayList<>();
        Page<Order> page = new Page<>(pageNum, pageSize);
        Page<Order> selectPage = orderMapper.selectPage(page, new QueryWrapper<Order>().eq("user_id", userId).eq("deleted", 0));
        List<Order> orderList = selectPage.getRecords();

        orderList.forEach(o -> {
            OrderVO orderVO = BeanUtil.copyProperties(o, OrderVO.class);
            orderVOList.add(orderVO);
        });
        return orderVOList;
    }

    @Override
    public int getAllOrderCount(long userId) {
        return Math.toIntExact(count(new QueryWrapper<Order>().eq("user_id", userId).eq("deleted", 0)));
    }
}
