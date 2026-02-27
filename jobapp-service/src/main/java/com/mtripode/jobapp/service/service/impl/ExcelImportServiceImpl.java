package com.mtripode.jobapp.service.service.impl;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mtripode.jobapp.service.job.ExcelImportJob;
import com.mtripode.jobapp.service.service.ExcelImportService;

@Service
public class ExcelImportServiceImpl implements ExcelImportService {

    private final ExcelImportJob excelImportJob;

    @Value("${jobapp.excel.import.path:./tmp/jobapp.xlsx}")
    private String filePath;

    public ExcelImportServiceImpl(ExcelImportJob excelImportJob) {
        this.excelImportJob = excelImportJob;
    }

    @Override
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean processFile() {
        return excelImportJob.processFile(this.getFilePath());
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public boolean processFile(InputStream inputStream) {
        return this.excelImportJob.processFile(inputStream);
    }
}
