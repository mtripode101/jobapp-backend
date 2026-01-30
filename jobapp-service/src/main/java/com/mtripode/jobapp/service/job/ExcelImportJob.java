package com.mtripode.jobapp.service.job;


import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.mtripode.jobapp.service.model.Candidate;
import com.mtripode.jobapp.service.model.Company;
import com.mtripode.jobapp.service.model.ContactInfo;
import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.JobOffer;
import com.mtripode.jobapp.service.model.JobOfferStatus;
import com.mtripode.jobapp.service.model.Position;
import com.mtripode.jobapp.service.model.Status;
import com.mtripode.jobapp.service.repository.CandidateRepository;
import com.mtripode.jobapp.service.repository.CompanyRepository;
import com.mtripode.jobapp.service.repository.JobApplicationRepository;
import com.mtripode.jobapp.service.repository.PositionRepository;

import io.micrometer.common.util.StringUtils;

@Component
public class ExcelImportJob {

    private static final Logger logger = LoggerFactory.getLogger(ExcelImportJob.class);

    private final CompanyRepository companyRepository;
    private final CandidateRepository candidateRepository;
    private final PositionRepository positionRepository;
    private final JobApplicationRepository jobApplicationRepository;

    public ExcelImportJob(CompanyRepository companyRepository,
            CandidateRepository candidateRepository,
            PositionRepository positionRepository,
            JobApplicationRepository jobApplicationRepository) {
        this.companyRepository = companyRepository;
        this.candidateRepository = candidateRepository;
        this.positionRepository = positionRepository;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    /**
     * Run every day at 00:00 (midnight). Cron format: second minute hour day
     * month weekday
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void runImportJob() {
        processFile("c://temp//jobapp.xlsx");
    }

    public boolean processFile( String excelPath) {
        File file = new File(excelPath);

        Boolean fileProcessed = false;
        if (!file.exists()) {
            logger.warn("Excel file not found at: {}. Skipping import job.", excelPath);
            return fileProcessed;
        }

        try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                try {
                    String source = safeGetString(row, 0);
                    String link = safeGetString(row, 1);
                    LocalDate dateApplied = parseDate(row.getCell(2));
                    String description = safeGetString(row, 3);
                    String companyName = safeGetString(row, 4);
                    String statusStr = safeGetString(row, 5);

                    Status status = StringUtils.isEmpty(statusStr)
                            ? Status.APPLIED
                            : Status.valueOf(statusStr.toUpperCase());

                    LocalDate dateRejected = parseDate(row.getCell(6));
                    String jobID = safeGetString(row, 7); 

                    // Ensure company exists
                    Company company = companyRepository.findByName(
                            StringUtils.isEmpty(companyName) ? "Default" : companyName
                    ).orElseGet(() -> companyRepository.save(
                            new Company(StringUtils.isEmpty(companyName) ? "Default" : companyName, null, "")
                    ));

                    // Candidate find-or-create
// Candidate find-or-create
                    Candidate candidate = candidateRepository.findByContactInfoEmail("mtripode@yahoo.com.ar")
                            .orElseGet(() -> {
                                ContactInfo contactInfo = new ContactInfo(
                                        "mtripode@yahoo.com.ar", // email
                                        "000-0000", // phone
                                        "https://linkedin.com/in/carlosmartintripode", // linkedIn opcional
                                        "https://github.com/cmartintripode" // github opcional
                                );
                                Candidate newCandidate = new Candidate("Carlos Martin Tripode", contactInfo);
                                return candidateRepository.save(newCandidate);
                            });

                    Position position = positionRepository.save(
                            new Position("Imported Position", description, "Remote", company)
                    );

                    JobApplication application = new JobApplication(
                            source,
                            link,
                            dateApplied,
                            description,
                            candidate,
                            company,
                            position,
                            status,
                            jobID
                    );
                    if (status == Status.REJECTED) {
                        application.setDateRejected(dateRejected != null ? dateRejected : LocalDate.now());
                    }

                    JobOffer offer = new JobOffer(dateApplied, JobOfferStatus.PENDING, application);
                    application.addOffer(offer); // maintain bidirectional relationship

                    jobApplicationRepository.save(application);

                } catch (Exception rowEx) {
                    logger.error("Error processing row {}: {}", i, rowEx.getMessage());
                }
            }

            logger.info("✅ Excel import completed successfully.");
            fileProcessed = true;

        } catch (Exception e) {
            logger.error("❌ Error importing Excel file: {}", e.getMessage(), e);
            return fileProcessed;
        }

        return fileProcessed;
    }

    private String safeGetString(Row row, int index) {
        try {
            Cell cell = row.getCell(index);
            if (cell == null) {
                return "";
            }
            if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue();
                return (value == null || value.trim().isEmpty()) ? "" : value.trim();
            } else {
                DataFormatter formatter = new DataFormatter();
                String value = formatter.formatCellValue(cell);
                return (value == null || value.trim().isEmpty()) ? "" : value.trim();
            }
        } catch (Exception e) {
            logger.error("Error reading cell at index {}", index, e);
            return "";
        }
    }

    private LocalDate parseDate(Cell cell) {
        if (cell == null) {
            return null;
        }
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            } else if (cell.getCellType() == CellType.STRING) {
                String value = cell.getStringCellValue().trim();
                if (value.isEmpty()) {
                    return null;
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
                return LocalDate.parse(value, formatter);
            }
        } catch (DateTimeParseException e) {
            logger.error("Error parsing date cell: {}", cell, e);
        } catch (Exception e) {
            logger.error("Unexpected error parsing date cell", e);
        }
        return null;
    }
}

