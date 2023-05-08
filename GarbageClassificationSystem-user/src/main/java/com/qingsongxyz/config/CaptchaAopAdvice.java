package com.qingsongxyz.config;

import cn.hutool.http.HttpStatus;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.qingsongxyz.exception.CaptchaNotFoundException;
import com.qingsongxyz.pojo.CaptchaResult;
import com.qingsongxyz.pojo.CommonResult;
import com.qingsongxyz.service.feignService.CaptchaFeignService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CaptchaAopAdvice {

    private final CaptchaFeignService captchaFeignService;

    public CaptchaAopAdvice(@Qualifier("captchaFeignService") CaptchaFeignService captchaFeignService) {
        this.captchaFeignService = captchaFeignService;
    }

    @Pointcut(value = "execution(* com.qingsongxyz.service.impl.UserServiceImpl.register(..)) &&" +
            "execution(* com.qingsongxyz.service.impl.UserServiceImpl.updatePwd(..))")
    public void pointCut() {}

    @Around(value = "pointCut()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object proceed = null;
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof CaptchaResult) {
            CaptchaResult captchaResult = (CaptchaResult) args[0];

            //远程调用captcha二次校验 验证码
            CommonResult result = captchaFeignService.checkTwice(captchaResult);
            if (result.getCode().equals(200)) {
                proceed = joinPoint.proceed();
            }else if(result.getCode() == HttpStatus.HTTP_UNAVAILABLE){
                throw new DegradeException(result.getMessage());
            } else {
                throw new CaptchaNotFoundException("请先进行验证码验证!!!");
            }
        }

        return proceed;
    }
}
