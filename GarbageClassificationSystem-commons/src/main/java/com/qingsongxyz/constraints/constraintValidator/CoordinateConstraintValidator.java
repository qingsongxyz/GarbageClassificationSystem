package com.qingsongxyz.constraints.constraintValidator;

import com.qingsongxyz.constraints.Coordinate;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 经纬度校验
 */
public class CoordinateConstraintValidator implements ConstraintValidator<Coordinate, double[]> {

    //最小经度
    private static final int MIN_LONGITUDE = -180;

    //最大经度
    private static final int MAX_LONGITUDE = 180;

    //最小纬度
    private static final int MIN_LATITUDE = -90;

    //最大纬度
    private static final int MAX_LATITUDE = 90;

    @Override
    public boolean isValid(double[] value, ConstraintValidatorContext context) {
        if (value.length != 2) return false;
        double longitude = value[0];
        double latitude = value[1];
        if (longitude >= MIN_LONGITUDE && longitude <= MAX_LONGITUDE
                && latitude >= MIN_LATITUDE && latitude <= MAX_LATITUDE) {
            return true;
        }
        return false;
    }
}
