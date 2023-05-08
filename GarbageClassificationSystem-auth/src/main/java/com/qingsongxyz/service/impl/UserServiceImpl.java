package com.qingsongxyz.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.qingsongxyz.dto.UserDTO;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.UserFeignService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements UserDetailsService {

    private final UserFeignService userFeignService;

    public UserServiceImpl(@Qualifier("userFeignService") UserFeignService userFeignService) {
        this.userFeignService = userFeignService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CommonResult result = userFeignService.getUserByUsername(username);
        UserDTO userDTO = null;
        if (result.getCode() != HttpStatus.HTTP_UNAVAILABLE) {
            Object data = result.getData();
            if (ObjectUtil.isNotNull(data) && data instanceof Map) {
                userDTO = BeanUtil.fillBeanWithMap((Map) data, new UserDTO(), CopyOptions.create().ignoreNullValue().ignoreError());
                if (ObjectUtil.isNull(userDTO)) {
                    throw new UsernameNotFoundException("该用户不存在!!!");
                } else if (userDTO.getAccountLocked() == 1) {
                    throw new LockedException("该账号已被锁定!!!");
                }
            }
        }
        return userDTO;
    }
}
