package com.qingsongxyz.constraints;

import com.qingsongxyz.constraints.constraintValidator.DoubleMinConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = { DoubleMinConstraintValidator.class })
public @interface DoubleMin {

    String message() default "{com.qingsongxyz.constraints.DoubleMin.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return 最小值
     */
    double value();
}
