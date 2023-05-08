package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.mapper.BroadcastMapper;
import com.qingsongxyz.pojo.Broadcast;
import com.qingsongxyz.service.BroadcastService;
import com.qingsongxyz.vo.BroadcastVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 消息表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-02-17
 */
@Service
public class BroadcastServiceImpl extends ServiceImpl<BroadcastMapper, Broadcast> implements BroadcastService {

    private final BroadcastMapper broadcastMapper;

    public BroadcastServiceImpl(BroadcastMapper broadcastMapper) {
        this.broadcastMapper = broadcastMapper;
    }

    @Override
    public int addBroadcast(Broadcast broadcast) {
        return broadcastMapper.insert(broadcast);
    }

    @Override
    public int deleteBroadcast(long id) {
        return broadcastMapper.deleteById(id);
    }

    @Override
    public int deleteBroadcastList(List<Long> ids) {
        return broadcastMapper.deleteBatchIds(ids);
    }

    @Override
    public BroadcastVO getBroadcastById(long id) {
        Broadcast broadcast = broadcastMapper.getBroadcastById(id);
        BroadcastVO broadcastVO = BeanUtil.copyProperties(broadcast, BroadcastVO.class);
        return broadcastVO;
    }

    @Override
    public List<BroadcastVO> getBroadcastList(int pageNum, int pageSize) {

        List<Broadcast> broadcastList = broadcastMapper.getBroadcastList(null, (pageNum - 1) * pageSize, pageSize);
        return transfer(broadcastList);
    }

    @Override
    public List<BroadcastVO> getBroadcastListByTitle(String title, int pageNum, int pageSize) {
        List<Broadcast> broadcastList = broadcastMapper.getBroadcastList(title, (pageNum - 1) * pageSize, pageSize);
        return transfer(broadcastList);
    }

    @Override
    public List<BroadcastVO> getAllBroadcastListByTitle(String title) {
        List<Broadcast> broadcastList = broadcastMapper.getBroadcastList(title, null, null);
        return transfer(broadcastList);
    }

    @Override
    public int getBroadcastListCount(String title) {
        return broadcastMapper.getBroadcastListCount(title);
    }

    @Override
    public int updateBroadcast(Broadcast broadcast) {
        return broadcastMapper.updateById(broadcast);
    }

    /**
     * 将消息集合转换为消息VO集合
     *
     * @param broadcastList 消息集合
     * @return 消息VO集合
     */
    private List<BroadcastVO> transfer(List<Broadcast> broadcastList) {
        ArrayList<BroadcastVO> broadcastVOList = new ArrayList<>();
        broadcastList.forEach(b -> {
            BroadcastVO broadcastVO = BeanUtil.copyProperties(b, BroadcastVO.class);
            broadcastVOList.add(broadcastVO);
        });
        return broadcastVOList;
    }
}
