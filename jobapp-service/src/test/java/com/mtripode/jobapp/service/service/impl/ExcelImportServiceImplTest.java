package com.mtripode.jobapp.service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mtripode.jobapp.service.job.ExcelImportJob;

class ExcelImportServiceImplTest {

    private TestExcelImportJob excelImportJob;

    private ExcelImportServiceImpl service;

    @BeforeEach
    void setUp() {
        excelImportJob = new TestExcelImportJob();
        service = new ExcelImportServiceImpl(excelImportJob);
    }

    @Test
    void processFileShouldUseConfiguredPath() {
        service.setFilePath("./tmp/custom.xlsx");
        excelImportJob.pathResult = true;

        boolean result = service.processFile();

        assertThat(result).isTrue();
        assertThat(excelImportJob.lastPath).isEqualTo("./tmp/custom.xlsx");
    }

    @Test
    void processFileWithInputStreamShouldDelegateToJob() {
        InputStream stream = new ByteArrayInputStream(new byte[] {1, 2, 3});
        excelImportJob.streamResult = true;

        boolean result = service.processFile(stream);

        assertThat(result).isTrue();
        assertThat(excelImportJob.lastStream).isSameAs(stream);
    }

    @Test
    void setFilePathShouldUpdateGetterValue() {
        service.setFilePath("c:/temp/jobapp.xlsx");

        assertThat(service.getFilePath()).isEqualTo("c:/temp/jobapp.xlsx");
    }

    static class TestExcelImportJob extends ExcelImportJob {

        String lastPath;
        InputStream lastStream;
        boolean pathResult;
        boolean streamResult;

        TestExcelImportJob() {
            super(null, null, null, null);
        }

        @Override
        public boolean processFile(String excelPath) {
            this.lastPath = excelPath;
            return pathResult;
        }

        @Override
        public boolean processFile(InputStream inputStream) {
            this.lastStream = inputStream;
            return streamResult;
        }
    }
}
