package com.qingsongxyz.constraints.constraintValidator;

import com.qingsongxyz.constraints.DoubleMin;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DoubleMinConstraintValidator implements ConstraintValidator<DoubleMin, Double> {

    private double min;

    @Override
    public void initialize(DoubleMin doubleMin) {
        min = doubleMin.value();
    }

    @Override
    public boolean isValid(Double value, ConstraintValidatorContext context) {
        return value > min;
    }
}
