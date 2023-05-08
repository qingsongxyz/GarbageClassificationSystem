package com.qingsongxyz.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.qingsongxyz.vo.RoleVO;
import java.util.List;

/**
 * 自定义转换器 将List<Role>类型导出到excel
 */
public class RoleListStringConverter implements Converter<List<RoleVO>> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return List.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }


    @Override
    public WriteCellData<?> convertToExcelData(List<RoleVO> roleVOList, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < roleVOList.size(); i++) {
            RoleVO roleVO = roleVOList.get(i);
            builder.append(roleVO.getRole());
            if(i < roleVOList.size() - 1){
                builder.append("\n");
            }
        }
        return new WriteCellData<>(builder.toString());
    }
}
