package com.qingsongxyz.constraints;

import com.qingsongxyz.constraints.constraintValidator.CoordinateConstraintValidator;
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
@Constraint(validatedBy = {CoordinateConstraintValidator.class})
public @interface Coordinate {

    String message() default "垃圾回收站经纬度必须合法";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
