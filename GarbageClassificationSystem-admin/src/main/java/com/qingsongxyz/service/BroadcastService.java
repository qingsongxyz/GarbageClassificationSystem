package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.pojo.Broadcast;
import com.qingsongxyz.vo.BroadcastVO;

import java.util.List;

/**
 * <p>
 * 消息表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2023-02-17
 */
public interface BroadcastService extends IService<Broadcast> {

    /**
     * 发布消息
     * @param broadcast 消息对象
     * @return 是否发布成功
     */
    int addBroadcast(Broadcast broadcast);

    /**
     * 删除消息
     * @param id 消息id
     * @return 是否删除成功
     */
    int deleteBroadcast(long id);

    /**
     * 批量删除消息集合
     * @param ids 消息id集合
     * @return 是否批量删除成功
     */
    int deleteBroadcastList(List<Long> ids);

    /**
     * 通过id查询消息
     * @param id 消息id
     * @return 消息
     */
    BroadcastVO getBroadcastById(long id);

    /**
     * 分页查询消息集合
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 消息集合
     */
    List<BroadcastVO> getBroadcastList(int pageNum, int pageSize);

    /**
     * 通过消息标题分页查询信息集合
     * @param title 消息标题
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 信息集合
     */
    List<BroadcastVO> getBroadcastListByTitle(String title, int pageNum, int pageSize);

    /**
     * 通过消息标题查询所有消息集合
     * @param title 消息标题
     * @return 消息集合
     */
    List<BroadcastVO> getAllBroadcastListByTitle(String title);

    /**
     * 查询消息数量
     * @param title 消息标题
     * @return String username)
     */
    int getBroadcastListCount(String title);

    /**
     * 修改消息信息
     * @param broadcast 消息对象
     * @return  是否修改成功
     */
    int updateBroadcast(Broadcast broadcast);
}
