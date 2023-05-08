package com.qingsongxyz.constraints.constraintValidator;

import com.qingsongxyz.constraints.MessageTypeAnnotation;
import com.qingsongxyz.pojo.Message;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 检验消息类型是否合法
 */
public class MessageTypeConstraintValidator implements ConstraintValidator<MessageTypeAnnotation, String> {

    @Override
    public boolean isValid(String messageType, ConstraintValidatorContext context) {
        Message.MessageType[] messageTypeList = Message.MessageType.values();
        for (Message.MessageType type : messageTypeList) {
            String name = type.toString();
            if(name.equals(messageType)){
                return true;
            }
        }
        return false;
    }
}
