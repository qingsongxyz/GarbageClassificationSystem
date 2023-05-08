package com.qingsongxyz.exception;

/**
 * Excel导入单元格数据异常, 包括单元格数据为空、长度过长、格式不合法等
 */
public class ExcelCellDataException extends Exception {

    public ExcelCellDataException(String message) {
        super(message);
    }
}
