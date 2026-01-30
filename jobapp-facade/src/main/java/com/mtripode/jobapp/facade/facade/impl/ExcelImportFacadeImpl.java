package com.mtripode.jobapp.facade.facade.impl;

import org.springframework.stereotype.Component;

import com.mtripode.jobapp.facade.facade.ExcelImportFacade;
import com.mtripode.jobapp.service.service.ExcelImportService;

@Component
public class ExcelImportFacadeImpl implements ExcelImportFacade {

    private final ExcelImportService excelImportService;

    public ExcelImportFacadeImpl(ExcelImportService excelImportService) {
        this.excelImportService = excelImportService;
    }

    @Override
    public boolean processFile(String filePath) {
        excelImportService.setFilePath(filePath);
        return excelImportService.processFile();
    }

}
