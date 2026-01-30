package com.mtripode.jobapp.service.service;

public interface ExcelImportService {

    default void warningMessage() {
        System.out.println("This is a warning message from ExcelImportService.");
    }
    void setFilePath(String filePath);

    boolean processFile();
}
