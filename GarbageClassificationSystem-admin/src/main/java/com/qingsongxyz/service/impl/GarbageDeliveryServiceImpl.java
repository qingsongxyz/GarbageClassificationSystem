package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.mapper.GarbageDeliveryMapper;
import com.qingsongxyz.pojo.GarbageDelivery;
import com.qingsongxyz.pojo.User;
import com.qingsongxyz.service.GarbageDeliveryService;
import com.qingsongxyz.service.UserService;
import com.qingsongxyz.validation.ValidList;
import com.qingsongxyz.vo.GarbageDeliveryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 垃圾投递表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-12-26
 */
@Slf4j
@Service
public class GarbageDeliveryServiceImpl extends ServiceImpl<GarbageDeliveryMapper, GarbageDelivery> implements GarbageDeliveryService {

    private final GarbageDeliveryMapper garbageDeliveryMapper;

    private final UserService userService;

    public GarbageDeliveryServiceImpl(GarbageDeliveryMapper garbageDeliveryMapper, UserService userService) {
        this.garbageDeliveryMapper = garbageDeliveryMapper;
        this.userService = userService;
    }

    @Override
    public List<GarbageDeliveryVO> getGarbageDeliveryList(int pageNum, int pageSize) {
        List<GarbageDelivery> garbageDeliveryList = garbageDeliveryMapper.getGarbageDeliveryList((pageNum - 1) * pageSize, pageSize);
        return transfer(garbageDeliveryList);
    }

    @Override
    public List<GarbageDeliveryVO> getGarbageDeliveryListByUsernameOrStatus(String username, int status, int pageNum, int pageSize) {
        List<GarbageDelivery> garbageDeliveryList = garbageDeliveryMapper.getGarbageDeliveryListByUsernameOrStatus(username, status, (pageNum - 1) * pageSize, pageSize);
        return transfer(garbageDeliveryList);
    }

    @Override
    public int getAllGarbageDeliveryCount() {
        return Math.toIntExact(count());
    }

    @Override
    public int getAllGarbageDeliveryCountByUsernameOrStatus(String username, int status) {
        return garbageDeliveryMapper.getGarbageDeliveryCountByUsernameAndStatus(username, status);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int updateGarbageDelivery(ValidList<GarbageDelivery> garbageDeliveryList) {
        garbageDeliveryList.forEach(g -> {
            Integer newStatus = g.getStatus();
            if(ObjectUtil.isNotNull(newStatus)){
                GarbageDelivery garbageDelivery = garbageDeliveryMapper.selectById(g.getId());
                Integer oldStatus = garbageDelivery.getStatus();
                if(!newStatus.equals(oldStatus)){
                    //用户投递垃圾状态变为异常需要扣除相应的积分
                    if(newStatus == 2){
                        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.eq("id", garbageDelivery.getUserId())
                                .setSql("score = score - " + garbageDelivery.getTotal());
                        boolean update = userService.update(updateWrapper);
                        log.info("GarbageDeliveryServiceImpl updateGarbageDelivery 投递异常扣除用户积分:{}", update);
                    }
                    //用户投递垃圾状态变为正常需要添加相应的积分
                    if(oldStatus == 2){
                        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.eq("id", garbageDelivery.getUserId())
                                .setSql("score = score + " + garbageDelivery.getTotal());
                        boolean update = userService.update(updateWrapper);
                        log.info("GarbageDeliveryServiceImpl updateGarbageDelivery 投递正常添加用户积分:{}", update);
                    }
                }

            }
        });
        return updateBatchById(garbageDeliveryList) ? 1 : 0;
    }

    /**
     * 将垃圾投递信息集合转换VO集合
     * @param garbageDeliveryList 垃圾投递信息集合
     * @return 垃圾投递信息VO集合
     */
    private List<GarbageDeliveryVO>  transfer(List<GarbageDelivery> garbageDeliveryList){
        ArrayList<GarbageDeliveryVO> garbageDeliveryVOList = new ArrayList<>();
        garbageDeliveryList.forEach(d -> {
            GarbageDeliveryVO garbageDeliveryVO = BeanUtil.copyProperties(d, GarbageDeliveryVO.class);
            garbageDeliveryVOList.add(garbageDeliveryVO);
        });
        return garbageDeliveryVOList;
    }
}
