package com.qingsongxyz.util;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.qingsongxyz.service.PathService;
import com.qingsongxyz.vo.PathVO;
import com.qingsongxyz.vo.RolePathVO;
import java.util.ArrayList;
import java.util.List;

public class RoleUtil {

    /**
     * 通过id查询权限详情信息
     * @param pathService 权限Service对象
     * @param pathId id
     * @return 权限详情信息
     */
    public static String getRoleList(PathService pathService, long pathId) {
        PathVO pathVO = pathService.getPathDetailsById(pathId);
        String roleList = "";
        if (ObjectUtil.isNotNull(pathVO)) {
            List<RolePathVO> rolePathVOList = pathVO.getRolePathVOList();
            if (ObjectUtil.isNotNull(rolePathVOList)) {
                //权限路径对应的角色集合
                List<String> list = new ArrayList<>();
                rolePathVOList.forEach(r -> {
                    String role = r.getRoleVO().getRole();
                    list.add(role);
                });
                if (list.size() > 0) {
                    roleList = JSON.toJSONString(list);
                }
            }
        }
        return roleList;
    }
}
