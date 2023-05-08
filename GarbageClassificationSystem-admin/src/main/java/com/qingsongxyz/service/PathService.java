package com.qingsongxyz.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qingsongxyz.pojo.Path;
import com.qingsongxyz.vo.PathVO;
import sun.security.tools.PathList;

import java.util.List;

/**
 * <p>
 * 权限表 服务类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-27
 */
public interface PathService extends IService<Path> {

    /**
     * 添加权限信息
     * @param path 权限对象
     * @return 是否添加成功
     */
    int addPath(Path path);

    /**
     * 通过id删除权限信息
     * @param id 权限id
     * @return 是否删除成功
     */
    int deletePath(long id);

    /**
     * 批量删除权限集合
     * @param ids 权限id集合
     * @return 是否删除成功
     */
    int deletePathList(List<Long> ids);

    /**
     * 通过id查询角色权限信息
     * @param id 角色权限id
     * @return 权限VO集合
     */
    PathVO getPathDetailsById(long id);

    /**
     * 查询所有角色权限信息
     * @return 权限VO集合
     */
    List<PathVO> getAllPathDetails();

    /**
     * 查询所有权限信息
     * @return 权限信息集合
     */
    List<PathVO> getAllPathList();

    /**
     * 分页查询权限集合
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 权限集合
     */
    List<PathVO> getPathList(int pageNum, int pageSize);

    /**
     * 通过权限路径模糊查询所有权限集合
     * @param path 权限路径
     * @return 权限集合
     */
    List<PathVO> getAllPathListByPath(String path);

    /**
     * 通过权限路径模糊分页查询权限集合
     * @param path 权限路径
     * @param pageNum 页码
     * @param pageSize 页面容量
     * @return 权限集合
     */
    List<PathVO> getPathListByPath(String path, int pageNum, int pageSize);

    /**
     * 查询权限总数量
     * @return 权限总数量
     */
    int getAllPathCount();

    /**
     * 通过权限路径模糊查询数量
     * @param path 权限路径
     * @return 权限数量
     */
    int getPathCountByPath(String path);

    /**
     * 修改权限信息
     * @param path 权限对象
     * @return 是否修改成功
     */
    int updatePath(Path path);

}
