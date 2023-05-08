package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.mapper.GarbageDeliveryMapper;
import com.qingsongxyz.mapper.UserMapper;
import com.qingsongxyz.pojo.GarbageDelivery;
import com.qingsongxyz.pojo.GarbageDeliveryDetails;
import com.qingsongxyz.service.GarbageDeliveryDetailsService;
import com.qingsongxyz.service.GarbageDeliveryService;
import com.qingsongxyz.vo.GarbageDeliveryVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 垃圾投递表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-12-26
 */
@Service
public class GarbageDeliveryServiceImpl extends ServiceImpl<GarbageDeliveryMapper, GarbageDelivery> implements GarbageDeliveryService {

    private final GarbageDeliveryMapper garbageDeliveryMapper;

    private final GarbageDeliveryDetailsService garbageDeliveryDetailsService;

    private final UserMapper userMapper;

    public GarbageDeliveryServiceImpl(GarbageDeliveryMapper garbageDeliveryMapper, GarbageDeliveryDetailsService garbageDeliveryDetailsService, UserMapper userMapper) {
        this.garbageDeliveryMapper = garbageDeliveryMapper;
        this.garbageDeliveryDetailsService = garbageDeliveryDetailsService;
        this.userMapper = userMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addGarbageDelivery(GarbageDelivery garbageDelivery) {
        //垃圾投放获取的总积分
        int sum = 0;
        //添加垃圾投递信息
        int result = garbageDeliveryMapper.insert(garbageDelivery);
        if (result == 0) return result;
        List<GarbageDeliveryDetails> garbageDeliveryDetailsList = garbageDelivery.getGarbageDeliveryDetailsList();
        //设置deliveryId
        for (GarbageDeliveryDetails d : garbageDeliveryDetailsList) {
            //用户是否投放到正确的分类
            int sign = d.getFlag() == 0 ? 1 : -1;
            sum += sign * d.getSum();
            d.setDeliveryId(garbageDelivery.getId());
        }
        //添加垃圾投递详情信息
        result = garbageDeliveryDetailsService.addGarbageDeliveryDetailsList(garbageDeliveryDetailsList);
        //更新用户积分
        userMapper.decreaseUserScore(garbageDelivery.getUserId(), (-1) * sum);
        return result;
    }

    @Override
    public int deleteGarbageDelivery(long id) {
        return garbageDeliveryMapper.deleteById(id);
    }

    @Override
    public int deleteGarbageDeliveryList(List<Long> ids) {
        return garbageDeliveryMapper.deleteBatchIds(ids);
    }

    @Override
    public List<GarbageDeliveryVO> getGarbageDeliveryListByUserId(long userId, int pageNum, int pageSize) {
        List<GarbageDelivery> garbageDeliveryList = garbageDeliveryMapper.getGarbageDeliveryListByUserIdAndStatue(userId, null, (pageNum - 1) * pageSize, pageSize);
        return transfer(garbageDeliveryList);
    }

    @Override
    public List<GarbageDeliveryVO> getGarbageDeliveryListByUserIdAndStatus(long userId, int status, int pageNum, int pageSize) {
        List<GarbageDelivery> garbageDeliveryList = garbageDeliveryMapper.getGarbageDeliveryListByUserIdAndStatue(userId, status, (pageNum - 1) * pageSize, pageSize);
        return transfer(garbageDeliveryList);
    }

    @Override
    public int getAllGarbageDeliveryCountByUserId(long userId) {
        return garbageDeliveryMapper.getGarbageDeliveryCountByUserIdAndStatus(userId, null);
    }

    @Override
    public int getAllGarbageDeliveryCountByUserIdAndStatus(long userId, int status) {
        return garbageDeliveryMapper.getGarbageDeliveryCountByUserIdAndStatus(userId, status);
    }

    @Override
    public int getGarbageDeliveryCount() {
        Long count = garbageDeliveryMapper.selectCount(null);
        return Math.toIntExact(count);
    }

    @Override
    public List<Map<String, Object>> getGarbageDeliveryCountGroupByWeek(long userId) {
        String[] weekList = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        //初始化分组
        List<Map<String, Object>> result = new ArrayList<>(weekList.length);
        for (int i = 0; i < weekList.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", weekList[i]);
            map.put("value", 0);
            result.add(map);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 0, 0, 0);

        QueryWrapper<GarbageDelivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("create_time as name, count(*) as value")
                .eq("user_id", userId)
                .groupBy("create_time")
                .having("create_time BETWEEN '" + start.toString().replaceAll("T", " ") + "' AND '" + now.toString().replaceAll("T", " ") + "'");
        List<Map<String, Object>> maps = listMaps(queryWrapper);
        for (int i = 0; i < maps.size(); i++) {
            Map<String, Object> map = maps.get(i);
            map.put("name", weekList[i]);
            result.set(i, map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getPersonalGarbageDeliveryCountGroupByStatus(long userId) {
        String[] statusList = {"正确(获得积分)", "异常(违规)", "错误(扣减积分)"};
        //初始化分组
        List<Map<String, Object>> result = new ArrayList<>(statusList.length);
        for (int i = 0; i < statusList.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", statusList[i]);
            map.put("value", 0);
            result.add(map);
        }
        QueryWrapper<GarbageDelivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("status as name, count(*) as value")
                .eq("user_id", userId)
                .groupBy("status")
                .having("status > 0");
        List<Map<String, Object>> maps = listMaps(queryWrapper);
        for (int i = 0; i < maps.size(); i++) {
            Map<String, Object> map = maps.get(i);
            map.put("name", statusList[i]);
            result.set(i, map);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getGarbageDeliveryCountGroupByStatus() {
        String[] statusList = {"正确(获得积分)", "异常(违规)", "错误(扣减积分)"};
        //初始化分组
        List<Map<String, Object>> result = new ArrayList<>(statusList.length);
        for (int i = 0; i < statusList.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", statusList[i]);
            map.put("value", 0);
            result.add(map);
        }
        QueryWrapper<GarbageDelivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("status as name, count(*) as value")
                .groupBy("status")
                .having("status > 0");
        List<Map<String, Object>> maps = listMaps(queryWrapper);
        for (int i = 0; i < maps.size(); i++) {
            Map<String, Object> map = maps.get(i);
            map.put("name", statusList[i]);
            result.set(i, map);
        }
        return result;
    }

    /**
     * 将垃圾投递信息集合转换VO集合
     *
     * @param garbageDeliveryList 垃圾投递信息集合
     * @return 垃圾投递信息VO集合
     */
    private List<GarbageDeliveryVO> transfer(List<GarbageDelivery> garbageDeliveryList) {
        ArrayList<GarbageDeliveryVO> garbageDeliveryVOList = new ArrayList<>();
        garbageDeliveryList.forEach(d -> {
            GarbageDeliveryVO garbageDeliveryVO = BeanUtil.copyProperties(d, GarbageDeliveryVO.class);
            garbageDeliveryVOList.add(garbageDeliveryVO);
        });
        return garbageDeliveryVOList;
    }

}
