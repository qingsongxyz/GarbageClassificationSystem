package com.qingsongxyz.config;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.util.Map;

/**
 * excel单元格下拉框生成处理器
 */
public class DropDownCellHandler implements SheetWriteHandler {

    //默认生成下拉框的行数
    private static final int DEFAULT_ROW_SIZE = 0x10000;

    //默认输入非下拉选项时错误标题
    private static final String DEFAULT_ERROR_TITLE = "提示";

    //默认输入非下拉选项时错误内容
    private static final String DEFAULT_ERROR_TEXT = "输入内容错误, 请在下拉框中选择!!!";

    //下拉框数据
    //key是下拉框所在列下标 value为下拉框数据
    private final Map<Integer, String[]> dropDownMap;

    //输入非下拉选项时错误标题
    private String errorTitle = DEFAULT_ERROR_TITLE;

    //输入非下拉选项时错误内容
    private String errorText = DEFAULT_ERROR_TEXT;

    //生成下拉框的行数
    private int rowSize = DEFAULT_ROW_SIZE;

    public void setErrorTitle(String errorTitle) {
        this.errorTitle = errorTitle;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public void setRowSize(int rowSize) {
        this.rowSize = rowSize;
    }

    public DropDownCellHandler(Map<Integer, String[]> dropDownMap) {
        this.dropDownMap = dropDownMap;
    }

    public DropDownCellHandler(Map<Integer, String[]> dropDownMap, int rowSize) {
        this.dropDownMap = dropDownMap;
        if (rowSize > 0) {
            this.rowSize = rowSize;
        }
    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Sheet sheet = writeSheetHolder.getSheet();
        DataValidationHelper helper = sheet.getDataValidationHelper();

        dropDownMap.forEach((columnIndex, value) -> {
            // 下拉框范围设置
            CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(1, rowSize, columnIndex, columnIndex);
            // 内容
            DataValidationConstraint constraint = helper.createExplicitListConstraint(value);
            DataValidation dataValidation = helper.createValidation(constraint, cellRangeAddressList);

            // 阻止输入非下拉选项的值
            dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
            dataValidation.setShowErrorBox(true);
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.createErrorBox(errorTitle, errorText);
            sheet.addValidationData(dataValidation);
        });
    }
}
