package com.mtripode.jobapp.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
        } else if (filePath.equalsIgnoreCase("default")) {
            realFilePath.append("c://temp//jobapp.xlsx");
        }
        boolean result = excelImportFacade.processFile(realFilePath.toString());
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAndProcess(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Empty file"));
        }
        try (InputStream in = file.getInputStream()) {
            boolean result = excelImportFacade.processFile(in);
            return ResponseEntity.ok(Map.of("success", result));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "File processing error"));
        }
    }
}
