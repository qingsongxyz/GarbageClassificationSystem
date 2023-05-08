package com.qingsongxyz.constraints;

import com.qingsongxyz.constraints.constraintValidator.CityConstraintValidator;
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
@Constraint(validatedBy = {CityConstraintValidator.class})
public @interface City {

    String message() default "垃圾回收站地址的城市必须合法";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
