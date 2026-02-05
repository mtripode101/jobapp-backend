package com.mtripode.jobapp.service.service;

import java.io.InputStream;

public interface ExcelImportService {

    default void warningMessage() {
        System.out.println("This is a warning message from ExcelImportService.");
    }
    void setFilePath(String filePath);

    boolean processFile();

    boolean processFile(InputStream inputStream);
    
}
