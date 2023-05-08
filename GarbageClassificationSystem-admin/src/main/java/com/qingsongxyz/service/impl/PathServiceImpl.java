package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qingsongxyz.mapper.PathMapper;
import com.qingsongxyz.pojo.Path;
import com.qingsongxyz.pojo.RolePath;
import com.qingsongxyz.service.PathService;
import com.qingsongxyz.service.RolePathService;
import com.qingsongxyz.vo.PathVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 权限表 服务实现类
 * </p>
 *
 * @author qingsongxyz
 * @since 2022-11-27
 */
@Service
public class PathServiceImpl extends ServiceImpl<PathMapper, Path> implements PathService {

    private final PathMapper pathMapper;

    private final RolePathService rolePathService;

    public PathServiceImpl(PathMapper pathMapper, RolePathService rolePathService) {
        this.pathMapper = pathMapper;
        this.rolePathService = rolePathService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addPath(Path path) {
        int result = pathMapper.insert(path);
        if(result > 0) {
            //添加角色权限信息 未分配角色
            RolePath rolePath = new RolePath();
            rolePath.setPathId(path.getId());
            int result2 = rolePathService.addRolePath(rolePath);
            if (result2 > 0) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int deletePath(long id) {
        return pathMapper.deleteById(id);
    }

    @Override
    public int deletePathList(List<Long> ids) {
        return pathMapper.deleteBatchIds(ids);
    }

    @Override
    public PathVO getPathDetailsById(long id) {
        Path path = pathMapper.getPathDetailsById(id);
        PathVO pathVO = BeanUtil.copyProperties(path, PathVO.class);
        return pathVO;
    }

    @Override
    public List<PathVO> getAllPathDetails() {
        List<Path> pathList = pathMapper.getAllPathDetails();
        return transfer(pathList);
    }

    @Override
    public List<PathVO> getAllPathList() {
        List<Path> pathList = pathMapper.getPathList(null, null, null);
        return transfer(pathList);
    }

    @Override
    public List<PathVO> getPathList(int pageNum, int pageSize) {
        List<Path> pathList = pathMapper.getPathList(null, (pageNum - 1) * pageSize, pageSize);
        return transfer(pathList);
    }

    @Override
    public List<PathVO> getAllPathListByPath(String path) {
        List<Path> pathList = list(new QueryWrapper<Path>().like("path", path));
        return transfer(pathList);
    }

    @Override
    public List<PathVO> getPathListByPath(String path, int pageNum, int pageSize) {
        List<Path> pathList = pathMapper.getPathList(path, (pageNum - 1) * pageSize, pageSize);
        return transfer(pathList);
    }

    @Override
    public int getAllPathCount() {
        return pathMapper.getPathCount(null);
    }

    @Override
    public int getPathCountByPath(String path) {
        return pathMapper.getPathCount(path);
    }

    @Override
    public int updatePath(Path path) {
        return pathMapper.updateById(path);
    }

    /**
     * 将权限集合转换为VO集合
     * @param pathList 权限集合
     * @return VO集合
     */
    private List<PathVO> transfer(List<Path> pathList) {
        List<PathVO> pathVOList = new ArrayList<>();
        pathList.forEach(p -> {
            PathVO pathVO = BeanUtil.copyProperties(p, PathVO.class);
            pathVOList.add(pathVO);
        });
        return pathVOList;
    }
}

