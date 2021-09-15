package com.opensysnet.paperless.common.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class ExcelStyle {

    public static CellStyle work_sheet_left(SXSSFWorkbook workbook, boolean bold, short color) {
        Font font = workbook.createFont();
        font.setFontName("맑은 고딕");
        font.setFontHeight((short)(10*20));
        font.setBold(bold);
        if (color != 0) font.setColor(color);
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        return style;
    }

    public static CellStyle work_sheet_right(SXSSFWorkbook workbook, boolean bold, short color) {
        Font font = workbook.createFont();
        font.setFontName("맑은 고딕");
        font.setFontHeight((short)(10*20));
        font.setBold(bold);
        if (color != 0) font.setColor(color);
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        return style;
    }

    public static CellStyle work_sheet_left_with_line(SXSSFWorkbook workbook, boolean bold, short color) {
        Font font = workbook.createFont();
        font.setFontName("맑은 고딕");
        font.setFontHeight((short)(10*20));
        font.setBold(bold);
        if (color != 0) font.setColor(color);
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        return style;
    }

    public static CellStyle work_sheet_center(SXSSFWorkbook workbook, boolean bold, short color) {
        Font font = workbook.createFont();
        font.setFontName("맑은 고딕");
        font.setFontHeight((short)(10*20));
        font.setBold(bold);
        if (color != 0) font.setColor(color);
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        return style;
    }

    public static CellStyle work_sheet_center_with_line(SXSSFWorkbook workbook, boolean bold, short color) {
        Font font = workbook.createFont();
        font.setFontName("맑은 고딕");
        font.setFontHeight((short)(10*20));
        font.setBold(bold);
        if (color > 0) font.setColor(color);
        if (color == -2 ) font.setColor(Font.COLOR_RED);
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        if (color == -1 || color == -2) {
            style.setFillForegroundColor(IndexedColors.TAN.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        return style;
    }

    public static CellStyle work_sheet_line(SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderRight(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        return style;
    }
}
