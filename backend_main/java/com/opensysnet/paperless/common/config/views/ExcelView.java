package com.opensysnet.paperless.common.config.views;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opensysnet.paperless.common.model.ExcelModel;
import com.opensysnet.paperless.common.utils.ColorUtils;
import com.opensysnet.paperless.common.utils.ExcelStyle;
import com.opensysnet.paperless.mapper.PaperlessMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharEncoding;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class ExcelView extends AbstractExcelPOIView {

    @Autowired
    private PaperlessMapper paperlessMapper;

    @Override
    protected Workbook createWorkbook() {
        SXSSFWorkbook workbook = new SXSSFWorkbook(100000);
        return workbook;
    }

    public Sheet createSheets(SXSSFWorkbook workbook, String sheetName) {
        Sheet sheet = workbook.createSheet(sheetName);

        return sheet;
    }

    public void createTitle(Sheet sheet, LinkedHashMap<String, String> titleList) {
        Row row = null;
        //set title
        int titleCellIndex = 0;
        row = sheet.createRow(0);
        for(String key : titleList.keySet()) {
            row.createCell(titleCellIndex).setCellValue(titleList.get(key));
            titleCellIndex++;
        }
    }
    @Override
    protected void buildExcelDocument_Install(HttpServletRequest request, HttpServletResponse response, SXSSFWorkbook workbook, Map<String, Object> model) {
        try {
            ExcelModel excelmodel = (ExcelModel) model.get("excelData");

            //file name
            String fileName = StringUtils.isEmpty(excelmodel.getFileName()) ? Long.toString(System.currentTimeMillis()) : excelmodel.getFileName();

            //sheet name
            String sheetName = StringUtils.isEmpty(excelmodel.getSheetName()) ? "sheet" : excelmodel.getSheetName();

            //excel data list
            LinkedList<HashMap<String, String>> excelDataList = new LinkedList<>();
            if(!ObjectUtils.isEmpty(excelmodel.getResultList())) {
                excelDataList = new Gson().fromJson(new Gson().toJson(excelmodel.getResultList()), new TypeToken<LinkedList<HashMap<String, String>>>(){}.getType());
            }

            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, CharEncoding.UTF_8) + ".xlsx");
            response.setHeader("Content-Type", "application/octet-stream");
            response.setHeader("Content-Transfer-Encoding", "binary;");
            response.setHeader("Pragma", "no-cache;");
            response.setHeader("Expires", "-1;");

            int sheetIndex = 1;
            Sheet sheet = this.createSheets(workbook, sheetName + "_" + sheetIndex);
            sheetIndex++;
            Row row = null;

            int rowIndex = 1;

        } catch(Exception e) {
            e.printStackTrace();
            log.error("buildExcelDocument_Install Error : " + e.getMessage(), e);
        }
    }

    @Override
    protected void buildExcelDocument_Service(HttpServletRequest request, HttpServletResponse response, SXSSFWorkbook workbook, Map<String, Object> model) {
        try {
            ExcelModel excelmodel = (ExcelModel) model.get("excelData");

            //file name
            String fileName = StringUtils.isEmpty(excelmodel.getFileName()) ? Long.toString(System.currentTimeMillis()) : excelmodel.getFileName();

            //sheet name
            String sheetName = StringUtils.isEmpty(excelmodel.getSheetName()) ? "sheet" : excelmodel.getSheetName();

            //excel data list
            LinkedList<HashMap<String, String>> excelDataList = new LinkedList<>();
            if (!ObjectUtils.isEmpty(excelmodel.getResultList())) {
                excelDataList = new Gson().fromJson(new Gson().toJson(excelmodel.getResultList()), new TypeToken<LinkedList<HashMap<String, String>>>() {
                }.getType());
            }

            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, CharEncoding.UTF_8) + ".xlsx");
            response.setHeader("Content-Type", "application/octet-stream");
            response.setHeader("Content-Transfer-Encoding", "binary;");
            response.setHeader("Pragma", "no-cache;");
            response.setHeader("Expires", "-1;");

            int sheetIndex = 1;
            Sheet sheet = this.createSheets(workbook, sheetName + "_" + sheetIndex);
            sheetIndex++;
            Row row = null;

            int rowIndex = 1;

        } catch (Exception e) {
            e.printStackTrace();
            log.error("buildExcelDocument_Install Error : " + e.getMessage(), e);
        }
    }
}
