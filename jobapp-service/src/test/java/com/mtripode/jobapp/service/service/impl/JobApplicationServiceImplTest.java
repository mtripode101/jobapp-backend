package com.mtripode.jobapp.service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.mtripode.jobapp.service.cache.CacheUtilService;
import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.JobOffer;
import com.mtripode.jobapp.service.model.JobOfferStatus;
import com.mtripode.jobapp.service.model.Status;
import com.mtripode.jobapp.service.repository.JobApplicationRepository;
import com.mtripode.jobapp.service.service.JobOfferService;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceImplTest {

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private JobOfferService jobOfferService;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    private JobApplicationServiceImpl service;
    private CacheUtilService cacheUtilService;

    @BeforeEach
    void setUp() {
        cacheUtilService = new CacheUtilService(cacheManager);
        service = new JobApplicationServiceImpl(jobApplicationRepository, jobOfferService, cacheUtilService);
        when(jobApplicationRepository.save(any(JobApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void updateShouldRejectOffersAndClearCacheWhenApplicationIsRejected() {
        Long applicationId = 42L;
        JobOffer pending = buildOffer(JobOfferStatus.PENDING);
        JobOffer accepted = buildOffer(JobOfferStatus.ACCEPTED);
        List<JobOffer> offers = new ArrayList<>(List.of(pending, accepted));

        when(jobOfferService.findByApplicationId(applicationId)).thenReturn(offers);
        when(jobOfferService.saveJobOffer(any(JobOffer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(cacheManager.getCache("job-offers")).thenReturn(cache);

        JobApplication toUpdate = new JobApplication(
                "source-link",
                "website",
                LocalDate.now(),
                "desc",
                null,
                null,
                null,
                Status.REJECTED,
                "JOB-42");

        JobApplication result = service.update(applicationId, toUpdate);

        assertThat(result.getId()).isEqualTo(applicationId);
        assertThat(offers).allMatch(o -> o.getStatus() == JobOfferStatus.REJECTED);
        verify(jobOfferService, times(2)).saveJobOffer(any(JobOffer.class));
        verify(cache).clear();
    }

    @Test
    void updateShouldNotTouchOffersOrCacheWhenApplicationIsNotRejected() {
        Long applicationId = 100L;
        JobOffer pending = buildOffer(JobOfferStatus.PENDING);
        JobOffer accepted = buildOffer(JobOfferStatus.ACCEPTED);
        List<JobOffer> offers = new ArrayList<>(List.of(pending, accepted));

        when(jobOfferService.findByApplicationId(applicationId)).thenReturn(offers);

        JobApplication toUpdate = new JobApplication(
                "source-link",
                "website",
                LocalDate.now(),
                "desc",
                null,
                null,
                null,
                Status.APPLIED,
                "JOB-100");

        JobApplication result = service.update(applicationId, toUpdate);

        assertThat(result.getId()).isEqualTo(applicationId);
        assertThat(pending.getStatus()).isEqualTo(JobOfferStatus.PENDING);
        assertThat(accepted.getStatus()).isEqualTo(JobOfferStatus.ACCEPTED);
        verify(jobOfferService, never()).saveJobOffer(any(JobOffer.class));
        verify(cache, never()).clear();
    }

    private static JobOffer buildOffer(JobOfferStatus status) {
        JobOffer offer = new JobOffer();
        offer.setStatus(status);
        return offer;
    }
}
