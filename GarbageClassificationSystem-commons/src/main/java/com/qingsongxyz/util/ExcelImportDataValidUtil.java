package com.qingsongxyz.util;

import com.qingsongxyz.exception.ExcelCellDataException;
import com.qingsongxyz.validation.ExcelGroup;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

/**
 * excel导入数据校验工具类
 */
public class ExcelImportDataValidUtil {

    /**
     * 校验导入excel数据行
     *
     * @param o   导入数据对象
     * @param row 数据所在行
     */
    public static void valid(Object o, int row) throws ExcelCellDataException {
        //创建Hibernate校验配置
        HibernateValidatorConfiguration configure = Validation.byProvider(HibernateValidator.class).configure();
        //创建校验工厂 failFast 校验失败仍然继续校验
        ValidatorFactory validatorFactory = configure.failFast(false).buildValidatorFactory();
        //获取校验器
        Validator validator = validatorFactory.getValidator();

        //使用分组 根据属性标注注解校验对象
        Set<ConstraintViolation<Object>> result = validator.validate(o, ExcelGroup.class);

        StringBuilder builder = new StringBuilder();
        result.forEach(r -> {
            String message = r.getMessage();
            builder.append(message).append(",");
        });
        if (builder.length() != 0) {
            //去掉最后一个逗号
            builder.deleteCharAt(builder.length() - 1);
            throw new ExcelCellDataException("第" + row + "行数据有误: " + builder + ", excel导入失败!!!");
        }
    }
}
