package com.mtripode.jobapp.service.service.impl;

import org.springframework.stereotype.Service;

import com.mtripode.jobapp.service.job.ExcelImportJob;
import com.mtripode.jobapp.service.service.ExcelImportService;

@Service
public class ExcelImportServiceImpl implements ExcelImportService {

    private final ExcelImportJob excelImportJob;
    private String filePath;

    public ExcelImportServiceImpl(ExcelImportJob excelImportJob) {
        this.excelImportJob = excelImportJob;
        this.filePath = "c://temp//jobapp.xlsx";
    }

    @Override
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean processFile() {
        // c://temp//jobapp.xlsx
        return excelImportJob.processFile(this.getFilePath());
    }

    public String getFilePath() {
        return filePath;
    }

    

}
