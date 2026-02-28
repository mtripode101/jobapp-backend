package com.mtripode.jobapp.service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.JobOffer;
import com.mtripode.jobapp.service.model.JobOfferStatus;
import com.mtripode.jobapp.service.repository.JobApplicationRepository;
import com.mtripode.jobapp.service.repository.JobOfferRepository;

@ExtendWith(MockitoExtension.class)
class JobOfferServiceImplTest {

    @Mock
    private JobOfferRepository jobOfferRepository;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    private JobOfferServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new JobOfferServiceImpl(jobOfferRepository, jobApplicationRepository);
    }

    @Test
    void saveJobOfferShouldDelegateToRepository() {
        JobOffer offer = new JobOffer();
        when(jobOfferRepository.save(offer)).thenReturn(offer);

        JobOffer result = service.saveJobOffer(offer);

        assertThat(result).isSameAs(offer);
        verify(jobOfferRepository).save(offer);
    }

    @Test
    void createOfferShouldCreateOfferAndPersistApplication() {
        Long applicationId = 1L;
        LocalDate offeredAt = LocalDate.now();
        JobApplication application = new JobApplication();

        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.of(application));

        JobOffer result = service.createOffer(applicationId, offeredAt, JobOfferStatus.PENDING);

        assertThat(result.getOfferedAt()).isEqualTo(offeredAt);
        assertThat(result.getStatus()).isEqualTo(JobOfferStatus.PENDING);
        assertThat(result.getApplication()).isSameAs(application);
        assertThat(application.getOffers()).contains(result);
        verify(jobApplicationRepository).save(application);
    }

    @Test
    void createOfferShouldThrowWhenApplicationDoesNotExist() {
        Long applicationId = 99L;
        when(jobApplicationRepository.findById(applicationId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createOffer(applicationId, LocalDate.now(), JobOfferStatus.PENDING))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Application not found with id " + applicationId);
    }

    @Test
    void findByIdShouldDelegateToRepository() {
        JobOffer offer = new JobOffer();
        when(jobOfferRepository.findById(10L)).thenReturn(Optional.of(offer));

        Optional<JobOffer> result = service.findById(10L);

        assertThat(result).contains(offer);
        verify(jobOfferRepository).findById(10L);
    }

    @Test
    void findAllShouldDelegateToRepository() {
        List<JobOffer> expected = List.of(new JobOffer());
        when(jobOfferRepository.findAll()).thenReturn(expected);

        List<JobOffer> result = service.findAll();

        assertThat(result).isSameAs(expected);
        verify(jobOfferRepository).findAll();
    }

    @Test
    void deleteByIdShouldDelegateToRepository() {
        service.deleteById(5L);
        verify(jobOfferRepository).deleteById(5L);
    }

    @Test
    void findByStatusShouldDelegateToRepository() {
        List<JobOffer> expected = List.of(new JobOffer());
        when(jobOfferRepository.findByStatus(JobOfferStatus.PENDING)).thenReturn(expected);

        List<JobOffer> result = service.findByStatus(JobOfferStatus.PENDING);

        assertThat(result).isSameAs(expected);
        verify(jobOfferRepository).findByStatus(JobOfferStatus.PENDING);
    }

    @Test
    void findByOfferedDateQueriesShouldDelegateToRepository() {
        LocalDate date = LocalDate.now();
        List<JobOffer> expected = List.of(new JobOffer());
        when(jobOfferRepository.findByOfferedAtAfter(date)).thenReturn(expected);
        when(jobOfferRepository.findByOfferedAtBefore(date)).thenReturn(expected);

        assertThat(service.findByOfferedAtAfter(date)).isSameAs(expected);
        assertThat(service.findByOfferedAtBefore(date)).isSameAs(expected);

        verify(jobOfferRepository).findByOfferedAtAfter(date);
        verify(jobOfferRepository).findByOfferedAtBefore(date);
    }

    @Test
    void findByApplicationIdShouldDelegateToRepository() {
        List<JobOffer> expected = List.of(new JobOffer());
        when(jobOfferRepository.findByApplication_Id(33L)).thenReturn(expected);

        List<JobOffer> result = service.findByApplicationId(33L);

        assertThat(result).isSameAs(expected);
        verify(jobOfferRepository).findByApplication_Id(33L);
    }

    @Test
    void acceptOfferShouldUpdateStatusAndSave() {
        JobOffer offer = new JobOffer();
        offer.setStatus(JobOfferStatus.PENDING);
        when(jobOfferRepository.findById(12L)).thenReturn(Optional.of(offer));
        when(jobOfferRepository.save(offer)).thenReturn(offer);

        JobOffer result = service.acceptOffer(12L);

        assertThat(result.getStatus()).isEqualTo(JobOfferStatus.ACCEPTED);
        verify(jobOfferRepository).findById(12L);
        verify(jobOfferRepository).save(offer);
    }

    @Test
    void acceptOfferShouldThrowWhenOfferDoesNotExist() {
        when(jobOfferRepository.findById(12L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.acceptOffer(12L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Offer not found with id 12");
    }

    @Test
    void rejectOfferShouldUpdateStatusAndSave() {
        JobOffer offer = new JobOffer();
        offer.setStatus(JobOfferStatus.PENDING);
        when(jobOfferRepository.findById(13L)).thenReturn(Optional.of(offer));
        when(jobOfferRepository.save(offer)).thenReturn(offer);

        JobOffer result = service.rejectOffer(13L);

        assertThat(result.getStatus()).isEqualTo(JobOfferStatus.REJECTED);
        verify(jobOfferRepository).findById(13L);
        verify(jobOfferRepository).save(offer);
    }

    @Test
    void rejectOfferShouldThrowWhenOfferDoesNotExist() {
        when(jobOfferRepository.findById(13L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.rejectOffer(13L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Offer not found with id 13");
    }

    @Test
    void salaryQueriesShouldDelegateToRepository() {
        List<JobOffer> expected = List.of(new JobOffer());
        when(jobOfferRepository.findByExpectedSalaryGreaterThan(1000.0)).thenReturn(expected);
        when(jobOfferRepository.findByOfferedSalaryLessThan(5000.0)).thenReturn(expected);
        when(jobOfferRepository.findByExpectedSalaryBetween(1000.0, 2000.0)).thenReturn(expected);
        when(jobOfferRepository.findByOfferedSalaryBetween(2000.0, 3000.0)).thenReturn(expected);
        when(jobOfferRepository.findByExpectedSalaryIsNull()).thenReturn(expected);
        when(jobOfferRepository.findByOfferedSalaryIsNull()).thenReturn(expected);
        when(jobOfferRepository.findByExpectedSalaryIsNotNull()).thenReturn(expected);
        when(jobOfferRepository.findByOfferedSalaryIsNotNull()).thenReturn(expected);
        when(jobOfferRepository.findByExpectedSalaryGreaterThanAndOfferedSalaryLessThan(1000.0, 3000.0))
                .thenReturn(expected);
        when(jobOfferRepository.findByExpectedSalaryLessThanAndOfferedSalaryGreaterThan(5000.0, 2000.0))
                .thenReturn(expected);
        when(jobOfferRepository.findByExpectedSalaryEqualsOfferedSalary()).thenReturn(expected);

        assertThat(service.findByExpectedSalaryGreaterThan(1000.0)).isSameAs(expected);
        assertThat(service.findByOfferedSalaryLessThan(5000.0)).isSameAs(expected);
        assertThat(service.findByExpectedSalaryBetween(1000.0, 2000.0)).isSameAs(expected);
        assertThat(service.findByOfferedSalaryBetween(2000.0, 3000.0)).isSameAs(expected);
        assertThat(service.findByExpectedSalaryIsNull()).isSameAs(expected);
        assertThat(service.findByOfferedSalaryIsNull()).isSameAs(expected);
        assertThat(service.findByExpectedSalaryIsNotNull()).isSameAs(expected);
        assertThat(service.findByOfferedSalaryIsNotNull()).isSameAs(expected);
        assertThat(service.findByExpectedSalaryGreaterThanAndOfferedSalaryLessThan(1000.0, 3000.0)).isSameAs(expected);
        assertThat(service.findByExpectedSalaryLessThanAndOfferedSalaryGreaterThan(5000.0, 2000.0)).isSameAs(expected);
        assertThat(service.findByExpectedSalaryEqualsOfferedSalary()).isSameAs(expected);

        verify(jobOfferRepository).findByExpectedSalaryGreaterThan(1000.0);
        verify(jobOfferRepository).findByOfferedSalaryLessThan(5000.0);
        verify(jobOfferRepository).findByExpectedSalaryBetween(1000.0, 2000.0);
        verify(jobOfferRepository).findByOfferedSalaryBetween(2000.0, 3000.0);
        verify(jobOfferRepository).findByExpectedSalaryIsNull();
        verify(jobOfferRepository).findByOfferedSalaryIsNull();
        verify(jobOfferRepository).findByExpectedSalaryIsNotNull();
        verify(jobOfferRepository).findByOfferedSalaryIsNotNull();
        verify(jobOfferRepository).findByExpectedSalaryGreaterThanAndOfferedSalaryLessThan(1000.0, 3000.0);
        verify(jobOfferRepository).findByExpectedSalaryLessThanAndOfferedSalaryGreaterThan(5000.0, 2000.0);
        verify(jobOfferRepository).findByExpectedSalaryEqualsOfferedSalary();
    }

    @Test
    void shouldUseCorrectRepositoryMethodForExpectedLessThanAndOfferedGreaterThan() {
        Double expectedMax = 5000.0;
        Double offeredMin = 4000.0;
        List<JobOffer> expected = List.of(new JobOffer());

        when(jobOfferRepository.findByExpectedSalaryLessThanAndOfferedSalaryGreaterThan(expectedMax, offeredMin))
                .thenReturn(expected);

        List<JobOffer> result = service.findByExpectedSalaryLessThanAndOfferedSalaryGreaterThan(expectedMax, offeredMin);

        assertThat(result).isSameAs(expected);
        verify(jobOfferRepository).findByExpectedSalaryLessThanAndOfferedSalaryGreaterThan(expectedMax, offeredMin);
    }

    @Test
    void shouldDelegateEqualsSalaryQueryToRepository() {
        List<JobOffer> expected = List.of(new JobOffer());
        when(jobOfferRepository.findByExpectedSalaryEqualsOfferedSalary()).thenReturn(expected);

        List<JobOffer> result = service.findByExpectedSalaryEqualsOfferedSalary();

        assertThat(result).isSameAs(expected);
        verify(jobOfferRepository).findByExpectedSalaryEqualsOfferedSalary();
    }
}
