package com.qingsongxyz.exception;

/**
 * 商品库存不足异常
 */
public class StorageNotEnoughException extends Exception{

    public StorageNotEnoughException(String message) {
        super(message);
    }
}
