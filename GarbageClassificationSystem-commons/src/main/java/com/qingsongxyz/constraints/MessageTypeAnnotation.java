package com.qingsongxyz.constraints;

import com.qingsongxyz.constraints.constraintValidator.MessageTypeConstraintValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {MessageTypeConstraintValidator.class})
public @interface MessageTypeAnnotation {

    String message() default "请输入合法的消息类型";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
