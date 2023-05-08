package com.qingsongxyz.config;

import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import org.apache.poi.common.usermodel.fonts.FontCharset;
import org.apache.poi.ss.usermodel.*;

/**
 * Excel 头部、内容样式配置类
 */
public class ExcelConfig {

    /**
     * excel头部样式配置
     * @return 头部样式配置
     */
    public static WriteCellStyle headConfig() {
        // 表头样式策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 是否换行
        headWriteCellStyle.setWrapped(false);
        // 水平对齐方式
        headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        // 垂直对齐方式
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        // 前景色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        // 背景色
        headWriteCellStyle.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
        // 设置为1时，单元格将被前景色填充
        headWriteCellStyle.setFillPatternType(FillPatternType.NO_FILL);
        // 控制单元格是否应自动调整大小以适应文本过长时的大小
        headWriteCellStyle.setShrinkToFit(false);
        // 单元格边框类型
        headWriteCellStyle.setBorderBottom(BorderStyle.NONE);
        headWriteCellStyle.setBorderLeft(BorderStyle.NONE);
        headWriteCellStyle.setBorderRight(BorderStyle.NONE);
        headWriteCellStyle.setBorderTop(BorderStyle.NONE);
        // 单元格边框颜色
        headWriteCellStyle.setLeftBorderColor(IndexedColors.BLACK.index);
        headWriteCellStyle.setRightBorderColor(IndexedColors.BLACK.index);
        headWriteCellStyle.setTopBorderColor(IndexedColors.BLACK.index);
        headWriteCellStyle.setBottomBorderColor(IndexedColors.BLACK.index);
        // 字体策略
        WriteFont writeFont = new WriteFont();
        // 是否加粗/黑体
        writeFont.setBold(false);
        // 字体颜色
        writeFont.setColor(Font.COLOR_RED);
        // 字体名称
        writeFont.setFontName("黑体");
        // 字体大小
        writeFont.setFontHeightInPoints((short) 12);
        // 是否使用斜体
        writeFont.setItalic(false);
        // 是否在文本中使用横线删除
        writeFont.setStrikeout(false);
        // 设置要使用的文本下划线的类型
        writeFont.setUnderline(Font.U_NONE);
        // 设置要使用的字符集
        writeFont.setCharset(FontCharset.DEFAULT.getNativeId());
        headWriteCellStyle.setWriteFont(writeFont);

        return headWriteCellStyle;
    }

    /**
     * excel内容格配置
     * @return 内容格配置
     */
    public static WriteCellStyle contentConfig() {
        // 内容样式策略策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 前景色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        // 水平对齐方式
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.GENERAL);
        // 单元格边框类型
        contentWriteCellStyle.setBorderBottom(BorderStyle.NONE);
        contentWriteCellStyle.setBorderLeft(BorderStyle.NONE);
        contentWriteCellStyle.setBorderRight(BorderStyle.NONE);
        contentWriteCellStyle.setBorderTop(BorderStyle.NONE);
        // 设置为1时，单元格将被前景色填充
        contentWriteCellStyle.setFillPatternType(FillPatternType.NO_FILL);
        // 是否换行
        contentWriteCellStyle.setWrapped(false);
        // 字体策略
        WriteFont writeFont = new WriteFont();
        // 是否加粗/黑体
        writeFont.setBold(false);
        // 字体颜色
        writeFont.setColor(Font.COLOR_NORMAL);
        // 字体名称
        writeFont.setFontName("宋体");
        // 字体大小
        writeFont.setFontHeightInPoints((short) 11);
        contentWriteCellStyle.setWriteFont(writeFont);
        return contentWriteCellStyle;
    }
}
