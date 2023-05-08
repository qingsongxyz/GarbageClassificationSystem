package com.qingsongxyz.exception;

/**
 * 用户名已存在异常
 */
public class UsernameExistException extends Exception{

    public UsernameExistException(String message) {
        super(message);
    }
}
