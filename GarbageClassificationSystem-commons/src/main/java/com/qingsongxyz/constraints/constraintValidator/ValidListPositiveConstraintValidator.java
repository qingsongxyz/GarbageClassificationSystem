package com.qingsongxyz.constraints.constraintValidator;

import com.qingsongxyz.constraints.ValidListPositive;
import com.qingsongxyz.validation.ValidList;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 校验id集合
 */
public class ValidListPositiveConstraintValidator implements ConstraintValidator<ValidListPositive, ValidList<Long>> {

    @Override
    public boolean isValid(ValidList<Long> ids, ConstraintValidatorContext context) {
        //id集合大小为0 校验不通过
        if(ids.size() == 0){
            return false;
        }

        //集合中有一个id小于等于0 校验不通过
        for (Long id : ids) {
            if(id <= 0){
                return false;
            }
        }

        return true;
    }
}
