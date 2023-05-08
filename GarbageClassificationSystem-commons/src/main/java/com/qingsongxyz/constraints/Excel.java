package com.qingsongxyz.constraints;

import com.qingsongxyz.constraints.constraintValidator.ExcelConstraintValidator;
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
@Constraint(validatedBy = {ExcelConstraintValidator.class})
public @interface Excel {

    String message() default "上传文件格式必须为.xlx或者xlsx";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    //excel文件最小的大小
    int min() default 0;

    //excel文件最大的大小
    int max() default 20;
}
