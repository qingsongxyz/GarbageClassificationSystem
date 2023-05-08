package com.qingsongxyz.util;

import cn.hutool.core.util.RandomUtil;

/**
 * 随机验证码工具类
 */
public class CaptchaUtil {

    /**
     * 生成6位纯数字随机验证码
     * @return 6位纯数字随机验证码
     */
    public static String create(){
        String code = RandomUtil.randomNumbers(6);
        return code;
    }
}
