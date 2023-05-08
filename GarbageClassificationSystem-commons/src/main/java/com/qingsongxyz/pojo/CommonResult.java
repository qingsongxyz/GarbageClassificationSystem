package com.qingsongxyz.pojo;

import cn.hutool.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.ObjectError;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResult {

    private Integer code;

    private Object data;

    private String message;

    public static CommonResult ok(){
        return new CommonResult(HttpStatus.HTTP_OK, null, null);
    }

    public static CommonResult ok(String message){
        return new CommonResult(HttpStatus.HTTP_OK, null, message);
    }

    public static CommonResult ok(Object data, String message){
        return new CommonResult(HttpStatus.HTTP_OK, data, message);
    }

    public static CommonResult failure(Integer code, String message){
        return new CommonResult(code, null, message);
    }

    public static CommonResult failure(Integer code, List<ObjectError> error){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("参数校验失败");
        for (ObjectError objectError : error) {
            stringBuilder.append(",");
            stringBuilder.append(objectError.getDefaultMessage());
        }
        return new CommonResult(code, null, stringBuilder.toString());
    }

    public static CommonResult failure(Integer code, Object data, String message){
        return new CommonResult(code, data, message);
    }
}
