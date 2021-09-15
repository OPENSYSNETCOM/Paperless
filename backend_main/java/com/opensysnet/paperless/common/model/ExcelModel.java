package com.opensysnet.paperless.common.model;

import lombok.Data;

@Data
public class ExcelModel {

    public ExcelModel(String fileName, String sheetName, Object resultList) {
        this.fileName = fileName;
        this.sheetName = sheetName;
        this.resultList = resultList;
    }

    private String fileName;

    private String sheetName;

    private Object resultList;

}
