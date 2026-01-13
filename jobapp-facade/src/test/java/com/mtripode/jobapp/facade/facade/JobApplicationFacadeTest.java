package com.mtripode.jobapp.facade.facade;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mtripode.jobapp.facade.dto.JobApplicationDto;
import com.mtripode.jobapp.facade.mapper.JobApplicationMapper;
import com.mtripode.jobapp.facade.facade.impl.JobApplicationFacadeImpl;
import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.Status;
import com.mtripode.jobapp.service.service.JobApplicationService;

@ExtendWith(MockitoExtension.class)
class JobApplicationFacadeTest {

    @Mock
    private JobApplicationService jobApplicationService;

    private JobApplicationMapper jobApplicationMapper;
    private JobApplicationFacadeImpl jobApplicationFacade;

    @BeforeEach
    void setUp() {
        jobApplicationMapper = new JobApplicationMapper(); // real mapper
        jobApplicationFacade = new JobApplicationFacadeImpl(jobApplicationService, jobApplicationMapper);
    }

    private JobApplication buildJobApplication() {
        JobApplication jobApp = new JobApplication();
        jobApp.setId(1L);
        jobApp.setSourceLink("https://example.com/job");
        jobApp.setWebsiteSource("LinkedIn");
        jobApp.setDescription("Backend Developer role");
        jobApp.setStatus(Status.APPLIED);
        return jobApp;
    }

    private JobApplicationDto buildJobApplicationDto() {
        JobApplicationDto dto = new JobApplicationDto();
        dto.setId(1L);
        dto.setSourceLink("https://example.com/job");
        dto.setWebsiteSource("LinkedIn");
        dto.setDescription("Backend Developer role");
        dto.setStatus(Status.APPLIED.toString());
        return dto;
    }

    @Test
    @DisplayName("Apply to job should persist and return DTO")
    void testApplyToJob() {
        JobApplication jobApp = buildJobApplication();
        JobApplicationDto dto = buildJobApplicationDto();

        when(jobApplicationService.applyToJob(
                anyString(), anyString(), anyString(),
                any(), any(), any()
        )).thenReturn(jobApp);

        JobApplicationDto saved = jobApplicationFacade.applyToJob(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getDescription()).isEqualTo("Backend Developer role");
        verify(jobApplicationService, times(1))
                .applyToJob(anyString(), anyString(), anyString(), any(), any(), any());
    }

    @Test
    @DisplayName("Apply rejected should persist and return DTO")
    void testApplyRejected() {
        JobApplication jobApp = buildJobApplication();
        JobApplicationDto dto = buildJobApplicationDto();

        when(jobApplicationService.applyRejected(
                anyString(), anyString(), anyString(),
                any(), any(), any()
        )).thenReturn(jobApp);

        JobApplicationDto saved = jobApplicationFacade.applyRejected(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getStatus()).isEqualTo("APPLIED");
        verify(jobApplicationService, times(1))
                .applyRejected(anyString(), anyString(), anyString(), any(), any(), any());
    }

    @Test
    @DisplayName("Find job application by ID should return Optional DTO")
    void testFindById() {
        JobApplication jobApp = buildJobApplication();
        when(jobApplicationService.findById(1L)).thenReturn(Optional.of(jobApp));

        Optional<JobApplicationDto> result = jobApplicationFacade.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getSourceLink()).isEqualTo("https://example.com/job");
        verify(jobApplicationService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Find all job applications should return list of DTOs")
    void testFindAll() {
        JobApplication jobApp = buildJobApplication();
        when(jobApplicationService.listAll()).thenReturn(List.of(jobApp));

        List<JobApplicationDto> results = jobApplicationFacade.findAll();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getWebsiteSource()).isEqualTo("LinkedIn");
        verify(jobApplicationService, times(1)).listAll();
    }

    @Test
    @DisplayName("Delete job application should call service delete")
    void testDeleteById() {
        jobApplicationFacade.deleteById(1L);
        verify(jobApplicationService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Update status should modify and return DTO")
    void testUpdateStatus() {
        JobApplication jobApp = buildJobApplication();
        jobApp.setStatus(Status.REJECTED);

        when(jobApplicationService.updateStatus(1L, Status.REJECTED)).thenReturn(jobApp);

        JobApplicationDto updated = jobApplicationFacade.updateStatus(1L, "REJECTED");

        assertThat(updated.getStatus()).isEqualTo("REJECTED");
        verify(jobApplicationService, times(1)).updateStatus(1L, Status.REJECTED);
    }

    @Test
    @DisplayName("Find job applications by status should return list of DTOs")
    void testFindByStatus() {
        JobApplication jobApp = buildJobApplication();
        when(jobApplicationService.listByStatus(Status.APPLIED)).thenReturn(List.of(jobApp));

        List<JobApplicationDto> results = jobApplicationFacade.findByStatus("APPLIED");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo("APPLIED");
        verify(jobApplicationService, times(1)).listByStatus(Status.APPLIED);
    }

    @Test
    @DisplayName("Find job applications by company name should return list of DTOs")
    void testFindByCompanyName() {
        JobApplication jobApp = buildJobApplication();
        when(jobApplicationService.listByCompanyName("Tech Corp")).thenReturn(List.of(jobApp));

        List<JobApplicationDto> results = jobApplicationFacade.findByCompanyName("Tech Corp");

        assertThat(results).hasSize(1);
        verify(jobApplicationService, times(1)).listByCompanyName("Tech Corp");
    }

    @Test
    @DisplayName("Find job applications by candidate full name should return list of DTOs")
    void testFindByCandidateFullName() {
        JobApplication jobApp = buildJobApplication();
        when(jobApplicationService.listByCandidateFullName("John Doe")).thenReturn(List.of(jobApp));

        List<JobApplicationDto> results = jobApplicationFacade.findByCandidateFullName("John Doe");

        assertThat(results).hasSize(1);
        verify(jobApplicationService, times(1)).listByCandidateFullName("John Doe");
    }

    @Test
    @DisplayName("Find job applications by position title should return list of DTOs")
    void testFindByPositionTitle() {
        JobApplication jobApp = buildJobApplication();
        when(jobApplicationService.listByPositionTitle("Backend Developer")).thenReturn(List.of(jobApp));

        List<JobApplicationDto> results = jobApplicationFacade.findByPositionTitle("Backend Developer");

        assertThat(results).hasSize(1);
        verify(jobApplicationService, times(1)).listByPositionTitle("Backend Developer");
    }
}