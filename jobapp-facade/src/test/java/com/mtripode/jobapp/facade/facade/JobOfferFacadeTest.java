package com.mtripode.jobapp.facade.facade;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mtripode.jobapp.facade.dto.JobOfferDTO;
import com.mtripode.jobapp.facade.facade.impl.JobOfferFacadeImpl;
import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.JobOffer;
import com.mtripode.jobapp.service.model.JobOfferStatus;
import com.mtripode.jobapp.service.service.JobOfferService;

@ExtendWith(MockitoExtension.class)
class JobOfferFacadeTest {

    @Mock
    private JobOfferService jobOfferService;

    private JobOfferFacadeImpl jobOfferFacade;

    @BeforeEach
    void setUp() {
        jobOfferFacade = new JobOfferFacadeImpl(jobOfferService);
    }

    private JobApplication buildJobApplication() {
        JobApplication app = new JobApplication();
        app.setId(100L);
        app.setDescription("Backend Developer role");
        return app;
    }

    private JobOffer buildJobOffer() {
        JobOffer offer = new JobOffer();
        offer.setId(1L);
        offer.setApplication(buildJobApplication()); // attach application
        offer.setOfferedAt(LocalDate.of(2026, 1, 10));
        offer.setStatus(JobOfferStatus.PENDING);
        return offer;
    }

    private JobOffer buildJobOffer(Double expected, Double offered, JobOfferStatus status) {
        JobOffer offer = new JobOffer();
        offer.setId(1L);
        offer.setApplication(buildJobApplication());
        offer.setOfferedAt(LocalDate.of(2026, 1, 10));
        offer.setStatus(status);
        offer.setExpectedSalary(expected);
        offer.setOfferedSalary(offered);
        return offer;
    }

    private JobOfferDTO buildJobOfferDto() {
        JobOfferDTO dto = new JobOfferDTO();
        dto.setId(1L);
        dto.setApplicationId(100L); // matches JobApplication id
        dto.setOfferedAt(LocalDate.of(2026, 1, 10));
        dto.setStatus(JobOfferStatus.PENDING);
        return dto;
    }

    @Test
    @DisplayName("Get all offers should return list of DTOs")
    void testGetAllOffers() {
        JobOffer offer = buildJobOffer();
        when(jobOfferService.findAll()).thenReturn(List.of(offer));

        List<JobOfferDTO> results = jobOfferFacade.getAllOffers();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo(JobOfferStatus.PENDING);
        assertThat(results.get(0).getApplicationId()).isEqualTo(100L);
        verify(jobOfferService, times(1)).findAll();
    }

    @Test
    @DisplayName("Get offer by ID should return Optional DTO")
    void testGetOfferById() {
        JobOffer offer = buildJobOffer();
        when(jobOfferService.findById(1L)).thenReturn(Optional.of(offer));

        Optional<JobOfferDTO> result = jobOfferFacade.getOfferById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getApplicationId()).isEqualTo(100L);
        verify(jobOfferService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Create offer should persist and return DTO")
    void testCreateOffer() {
        JobOffer offer = buildJobOffer();
        when(jobOfferService.createOffer(100L, offer.getOfferedAt(), JobOfferStatus.PENDING))
                .thenReturn(offer);

        JobOfferDTO saved = jobOfferFacade.createOffer(100L, offer.getOfferedAt(), JobOfferStatus.PENDING);

        assertThat(saved).isNotNull();
        assertThat(saved.getStatus()).isEqualTo(JobOfferStatus.PENDING);
        assertThat(saved.getApplicationId()).isEqualTo(100L);
        verify(jobOfferService, times(1))
                .createOffer(100L, offer.getOfferedAt(), JobOfferStatus.PENDING);
    }

    @Test
    @DisplayName("Update offer should persist and return DTO")
    void testUpdateOffer() {
        JobOffer offer = buildJobOffer();
        JobOfferDTO dto = buildJobOfferDto();

        when(jobOfferService.saveJobOffer(any(JobOffer.class))).thenReturn(offer);

        JobOfferDTO updated = jobOfferFacade.updateOffer(dto);

        assertThat(updated.getStatus()).isEqualTo(JobOfferStatus.PENDING);
        assertThat(updated.getApplicationId()).isEqualTo(100L);
        verify(jobOfferService, times(1)).saveJobOffer(any(JobOffer.class));
    }

    @Test
    @DisplayName("Delete offer should call service delete")
    void testDeleteOffer() {
        jobOfferFacade.deleteOffer(1L);
        verify(jobOfferService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Get offers by status should return list of DTOs")
    void testGetOffersByStatus() {
        JobOffer offer = buildJobOffer();
        when(jobOfferService.findByStatus(JobOfferStatus.PENDING)).thenReturn(List.of(offer));

        List<JobOfferDTO> results = jobOfferFacade.getOffersByStatus(JobOfferStatus.PENDING);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo(JobOfferStatus.PENDING);
        assertThat(results.get(0).getApplicationId()).isEqualTo(100L);
        verify(jobOfferService, times(1)).findByStatus(JobOfferStatus.PENDING);
    }

    @Test
    @DisplayName("Accept offer should update status and return DTO")
    void testAcceptOffer() {
        JobOffer offer = buildJobOffer();
        offer.setStatus(JobOfferStatus.ACCEPTED);

        when(jobOfferService.acceptOffer(1L)).thenReturn(offer);

        JobOfferDTO result = jobOfferFacade.acceptOffer(1L);

        assertThat(result.getStatus()).isEqualTo(JobOfferStatus.ACCEPTED);
        assertThat(result.getApplicationId()).isEqualTo(100L);
        verify(jobOfferService, times(1)).acceptOffer(1L);
    }

    @Test
    @DisplayName("Reject offer should update status and return DTO")
    void testRejectOffer() {
        JobOffer offer = buildJobOffer();
        offer.setStatus(JobOfferStatus.REJECTED);

        when(jobOfferService.rejectOffer(1L)).thenReturn(offer);

        JobOfferDTO result = jobOfferFacade.rejectOffer(1L);

        assertThat(result.getStatus()).isEqualTo(JobOfferStatus.REJECTED);
        assertThat(result.getApplicationId()).isEqualTo(100L);
        verify(jobOfferService, times(1)).rejectOffer(1L);
    }

    @Test
    @DisplayName("Find offers with expected salary between range")
    void testFindByExpectedSalaryBetween() {
        when(jobOfferService.findByExpectedSalaryBetween(4000.0, 6000.0))
                .thenReturn(List.of(buildJobOffer(5000.0, 3500.0, JobOfferStatus.PENDING)));

        List<JobOfferDTO> results = jobOfferFacade.findByExpectedSalaryBetween(4000.0, 6000.0);

        assertThat(results.get(0).getExpectedSalary()).isBetween(4000.0, 6000.0);
        verify(jobOfferService).findByExpectedSalaryBetween(4000.0, 6000.0);
    }

    @Test
    @DisplayName("Find offers with offered salary between range")
    void testFindByOfferedSalaryBetween() {
        when(jobOfferService.findByOfferedSalaryBetween(2000.0, 4000.0))
                .thenReturn(List.of(buildJobOffer(4500.0, 3000.0, JobOfferStatus.PENDING)));

        List<JobOfferDTO> results = jobOfferFacade.findByOfferedSalaryBetween(2000.0, 4000.0);

        assertThat(results.get(0).getOfferedSalary()).isBetween(2000.0, 4000.0);
        verify(jobOfferService).findByOfferedSalaryBetween(2000.0, 4000.0);
    }

    @Test
    @DisplayName("Find offers with expected salary null")
    void testFindByExpectedSalaryIsNull() {
        when(jobOfferService.findByExpectedSalaryIsNull())
                .thenReturn(List.of(buildJobOffer(null, 3000.0, JobOfferStatus.PENDING)));

        List<JobOfferDTO> results = jobOfferFacade.findByExpectedSalaryIsNull();

        assertThat(results.get(0).getExpectedSalary()).isNull();
        verify(jobOfferService).findByExpectedSalaryIsNull();
    }

    @Test
    @DisplayName("Find offers with offered salary null")
    void testFindByOfferedSalaryIsNull() {
        when(jobOfferService.findByOfferedSalaryIsNull())
                .thenReturn(List.of(buildJobOffer(5000.0, null, JobOfferStatus.PENDING)));

        List<JobOfferDTO> results = jobOfferFacade.findByOfferedSalaryIsNull();

        assertThat(results.get(0).getOfferedSalary()).isNull();
        verify(jobOfferService).findByOfferedSalaryIsNull();
    }

    @Test
    @DisplayName("Find offers with expected salary not null")
    void testFindByExpectedSalaryIsNotNull() {
        when(jobOfferService.findByExpectedSalaryIsNotNull())
                .thenReturn(List.of(buildJobOffer(5000.0, 3000.0, JobOfferStatus.PENDING)));

        List<JobOfferDTO> results = jobOfferFacade.findByExpectedSalaryIsNotNull();

        assertThat(results.get(0).getExpectedSalary()).isNotNull();
        verify(jobOfferService).findByExpectedSalaryIsNotNull();
    }

    @Test
    @DisplayName("Find offers with offered salary not null")
    void testFindByOfferedSalaryIsNotNull() {
        when(jobOfferService.findByOfferedSalaryIsNotNull())
                .thenReturn(List.of(buildJobOffer(5000.0, 3000.0, JobOfferStatus.PENDING)));

        List<JobOfferDTO> results = jobOfferFacade.findByOfferedSalaryIsNotNull();

        assertThat(results.get(0).getOfferedSalary()).isNotNull();
        verify(jobOfferService).findByOfferedSalaryIsNotNull();
    }

    @Test
    @DisplayName("Find offers with expected salary greater than and offered salary less than")
    void testFindByExpectedSalaryGreaterThanAndOfferedSalaryLessThan() {
        when(jobOfferService.findByExpectedSalaryGreaterThanAndOfferedSalaryLessThan(4000.0, 3500.0))
                .thenReturn(List.of(buildJobOffer(5000.0, 3000.0, JobOfferStatus.PENDING)));

        List<JobOfferDTO> results = jobOfferFacade.findByExpectedSalaryGreaterThanAndOfferedSalaryLessThan(4000.0, 3500.0);

        assertThat(results.get(0).getExpectedSalary()).isGreaterThan(4000.0);
        assertThat(results.get(0).getOfferedSalary()).isLessThan(3500.0);
        verify(jobOfferService).findByExpectedSalaryGreaterThanAndOfferedSalaryLessThan(4000.0, 3500.0);
    }

    @Test
    @DisplayName("Find offers with expected salary less than and offered salary greater than")
    void testFindByExpectedSalaryLessThanAndOfferedSalaryGreaterThan() {
        when(jobOfferService.findByExpectedSalaryLessThanAndOfferedSalaryGreaterThan(6000.0, 2000.0))
                .thenReturn(List.of(buildJobOffer(5000.0, 3000.0, JobOfferStatus.PENDING)));

        List<JobOfferDTO> results = jobOfferFacade.findByExpectedSalaryLessThanAndOfferedSalaryGreaterThan(6000.0, 2000.0);

        assertThat(results.get(0).getExpectedSalary()).isLessThan(6000.0);
        assertThat(results.get(0).getOfferedSalary()).isGreaterThan(2000.0);
        verify(jobOfferService).findByExpectedSalaryLessThanAndOfferedSalaryGreaterThan(6000.0, 2000.0);
    }

    @Test
    @DisplayName("Find offers with expected salary equals offered salary")
    void testFindByExpectedSalaryEqualsOfferedSalary() {
        when(jobOfferService.findByExpectedSalaryEqualsOfferedSalary())
                .thenReturn(List.of(buildJobOffer(4000.0, 4000.0, JobOfferStatus.PENDING)));

        List<JobOfferDTO> results = jobOfferFacade.findByExpectedSalaryEqualsOfferedSalary();

        assertThat(results.get(0).getExpectedSalary()).isEqualTo(results.get(0).getOfferedSalary());
        verify(jobOfferService).findByExpectedSalaryEqualsOfferedSalary();
    }    
}