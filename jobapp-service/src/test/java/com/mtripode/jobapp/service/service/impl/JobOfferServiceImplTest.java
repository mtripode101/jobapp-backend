package com.mtripode.jobapp.service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mtripode.jobapp.service.model.JobOffer;
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
