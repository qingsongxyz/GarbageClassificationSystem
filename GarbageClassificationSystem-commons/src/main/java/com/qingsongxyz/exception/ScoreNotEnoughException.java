package com.qingsongxyz.exception;

/**
 * 用户积分不足异常
 */
public class ScoreNotEnoughException extends Exception {

    public ScoreNotEnoughException(String message) {
        super(message);
    }
}
