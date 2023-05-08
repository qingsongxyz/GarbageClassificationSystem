package com.qingsongxyz.exception;

/**
 * 验证码异常
 */
public class CaptchaNotFoundException extends Exception{

    public CaptchaNotFoundException(String message) {
        super(message);
    }
}
