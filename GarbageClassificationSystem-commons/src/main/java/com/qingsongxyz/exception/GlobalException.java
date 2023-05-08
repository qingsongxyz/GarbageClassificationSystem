package com.qingsongxyz.exception;

import cn.hutool.http.HttpStatus;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.qingsongxyz.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalException {

    @ExceptionHandler(value = {NoHandlerFoundException.class})
    public CommonResult handleNoHandlerFoundException(NoHandlerFoundException e){
        return CommonResult.failure(HttpStatus.HTTP_NOT_FOUND, "请求路径不存在!!!");
    }

    @ExceptionHandler(DegradeException.class)
    public CommonResult handlerDegradeException(DegradeException e){
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "服务器错误!!!");
    }

    @ExceptionHandler(value = {UsernameExistException.class})
    public CommonResult handleUsernameExistException(UsernameExistException e){
        return CommonResult.failure(HttpStatus.HTTP_BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(value = {CaptchaNotFoundException.class})
    public CommonResult handleCaptchaNotFoundException(CaptchaNotFoundException e){
        return CommonResult.failure(HttpStatus.HTTP_BAD_REQUEST, "[验证码校验失败]" + e.getMessage());
    }

    @ExceptionHandler(value = {StorageNotEnoughException.class})
    public CommonResult handleStorageNotEnoughException(StorageNotEnoughException e){
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, e.getMessage());
    }

    @ExceptionHandler(value = {ScoreNotEnoughException.class})
    public CommonResult handleScoreNotEnoughException(ScoreNotEnoughException e){
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, e.getMessage());
    }

    @ExceptionHandler(value = {ExcelCellDataException.class})
    public CommonResult handleExcelCellDataException(ExcelCellDataException e){
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, e.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResult handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.info(e.getMessage());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[参数校验失败]");
        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            stringBuilder.append(error.getDefaultMessage()).append(",");
        }
        //去掉最后一个逗号
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return CommonResult.failure(HttpStatus.HTTP_BAD_REQUEST, stringBuilder.toString());
    }

    @ExceptionHandler(value = {BindException.class})
    public CommonResult handleBindException(BindException e){
        log.info(e.getMessage());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[参数校验失败]");
        for (ObjectError error : e.getAllErrors()) {
            stringBuilder.append(error.getDefaultMessage()).append(",");
        }
        //去掉最后一个逗号
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return CommonResult.failure(HttpStatus.HTTP_BAD_REQUEST, stringBuilder.toString());
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public CommonResult handleConstraintViolationException(ConstraintViolationException e){
        log.info(e.getMessage());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[参数校验失败]");
        for (ConstraintViolation<?> error : e.getConstraintViolations()) {
            stringBuilder.append(error.getMessage()).append(",");
        }
        //去掉最后一个逗号
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return CommonResult.failure(HttpStatus.HTTP_BAD_REQUEST, stringBuilder.toString());
    }

    @ExceptionHandler(value = {Exception.class})
    public CommonResult handleException(Exception e) {
        return CommonResult.failure(HttpStatus.HTTP_INTERNAL_ERROR, "服务器错误!!!");
    }
}
