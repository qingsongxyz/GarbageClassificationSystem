package com.qingsongxyz.config;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.qingsongxyz.service.PathService;
import com.qingsongxyz.vo.PathVO;
import com.qingsongxyz.vo.RolePathVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.qingsongxyz.constant.RedisConstant.ROLE_PATH_KEY;

@Component
@Slf4j
public class LoadRolePathCache implements CommandLineRunner {

    private final StringRedisTemplate stringRedisTemplate;

    private final PathService pathService;

    public LoadRolePathCache(StringRedisTemplate stringRedisTemplate, PathService pathService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.pathService = pathService;
    }

    @Override
    public void run(String... args) throws Exception {
        List<PathVO> pathVOList = pathService.getAllPathDetails();
        if(ObjectUtil.isNotEmpty(pathVOList)) {
            HashMap<String, String> map = new HashMap<>();
            pathVOList.forEach(p -> {
                List<RolePathVO> rolePathVOList = p.getRolePathVOList();
                if (ObjectUtil.isNotEmpty(rolePathVOList)) {
                    //权限路径对应的角色集合
                    List<String> list = new ArrayList<>();
                    rolePathVOList.forEach(r -> {
                        String role = r.getRoleVO().getRole();
                        list.add(role);
                    });
                    if (list.size() > 0) {
                        map.put(p.getPath(), JSON.toJSONString(list));
                    }
                }
            });
            log.info("map:{}", map);
            //存入redis
            stringRedisTemplate.opsForHash().putAll(ROLE_PATH_KEY, map);
        }
    }
}
