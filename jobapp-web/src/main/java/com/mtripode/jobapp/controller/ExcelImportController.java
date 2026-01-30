package com.mtripode.jobapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mtripode.jobapp.facade.facade.ExcelImportFacade;

import io.micrometer.common.util.StringUtils;


@RestController
@RequestMapping("/excelimport")
@CrossOrigin(origins = "http://localhost:3000") // habilita CORS solo para este controlador
public class ExcelImportController {

    private final ExcelImportFacade excelImportFacade;

    public ExcelImportController(ExcelImportFacade excelImportFacade) {
        this.excelImportFacade = excelImportFacade;
    }

    @PostMapping("/process")
    public ResponseEntity<Boolean> processExcelFile(@RequestBody String filePath) {
        StringBuilder realFilePath = new StringBuilder("");
        if (StringUtils.isBlank(filePath)) {
            realFilePath.append("c://temp//jobapp.xlsx");
        }
        else {
            realFilePath.append(filePath);
        }
        boolean result = excelImportFacade.processFile(realFilePath.toString());
        return ResponseEntity.ok(result);
    }
}
