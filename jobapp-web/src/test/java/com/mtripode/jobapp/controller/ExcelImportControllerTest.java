package com.mtripode.jobapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.mtripode.jobapp.facade.facade.ExcelImportFacade;

@ExtendWith(MockitoExtension.class)
class ExcelImportControllerTest {

    @Mock
    private ExcelImportFacade facade;

    @Mock
    private MultipartFile file;

    private ExcelImportController controller;

    @BeforeEach
    void setUp() {
        controller = new ExcelImportController(facade);
    }

    @Test
    void processExcelFileShouldUseDefaultPathWhenBlankOrDefault() {
        when(facade.processFile("c://temp//jobapp.xlsx")).thenReturn(true);

        ResponseEntity<Boolean> blankResponse = controller.processExcelFile(" ");
        ResponseEntity<Boolean> defaultResponse = controller.processExcelFile("default");

        assertThat(blankResponse.getBody()).isTrue();
        assertThat(defaultResponse.getBody()).isTrue();
    }

    @Test
    void uploadAndProcessShouldReturnBadRequestForEmptyFile() {
        when(file.isEmpty()).thenReturn(true);

        ResponseEntity<?> response = controller.uploadAndProcess(file);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().toString()).contains("Empty file");
    }

    @Test
    void uploadAndProcessShouldReturnSuccessWhenFacadeProcessesInputStream() throws Exception {
        InputStream input = new ByteArrayInputStream(new byte[] {1, 2, 3});
        when(file.isEmpty()).thenReturn(false);
        when(file.getInputStream()).thenReturn(input);
        when(facade.processFile(input)).thenReturn(true);

        ResponseEntity<?> response = controller.uploadAndProcess(file);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().toString()).contains("success=true");
    }

    @Test
    void uploadAndProcessShouldReturnInternalServerErrorOnIOException() throws Exception {
        when(file.isEmpty()).thenReturn(false);
        when(file.getInputStream()).thenThrow(new IOException("io error"));

        ResponseEntity<?> response = controller.uploadAndProcess(file);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().toString()).contains("File processing error");
    }
}
