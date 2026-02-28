package com.mtripode.jobapp.service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.util.ReflectionTestUtils;

import com.mtripode.jobapp.service.cache.CacheUtilService;
import com.mtripode.jobapp.service.config.JobConfigProperties;
import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.JobOffer;
import com.mtripode.jobapp.service.model.JobOfferStatus;
import com.mtripode.jobapp.service.model.Status;
import com.mtripode.jobapp.service.repository.JobApplicationRepository;
import com.mtripode.jobapp.service.repository.JobOfferRepository;
import com.mtripode.jobapp.service.service.JobOfferService;

@ExtendWith(MockitoExtension.class)
class CronJobApplicationServiceImplTest {

    @Mock
    private JobOfferService jobOfferService;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private JobOfferRepository jobOfferRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    private CronJobApplicationServiceImpl service;

    @BeforeEach
    void setUp() {
        JobConfigProperties jobConfigProperties = new JobConfigProperties();
        ReflectionTestUtils.setField(jobConfigProperties, "rejectThresholdDays", 30);
        CacheUtilService cacheUtilService = new CacheUtilService(cacheManager);
        lenient().when(cacheManager.getCache("job-offers")).thenReturn(cache);

        service = new CronJobApplicationServiceImpl(
                jobOfferService,
                jobApplicationRepository,
                jobConfigProperties,
                cacheUtilService,
                jobOfferRepository);
    }

    @Test
    void cleanOldApplicationsAndOffersShouldRejectApplicationsAndOffers() {
        JobApplication app = new JobApplication();
        app.setId(1L);
        app.setJobId("JOB-1");
        app.setStatus(Status.APPLIED);

        JobOffer offer1 = new JobOffer();
        offer1.setStatus(JobOfferStatus.PENDING);
        JobOffer offer2 = new JobOffer();
        offer2.setStatus(JobOfferStatus.ACCEPTED);

        when(jobApplicationRepository.findByStatusAndDateAppliedBefore(any(Status.class), any(LocalDate.class)))
                .thenReturn(List.of(app));
        when(jobOfferService.findByApplicationId(1L)).thenReturn(List.of(offer1, offer2));

        service.cleanOldApplicationsAndOffers();

        assertThat(app.getStatus()).isEqualTo(Status.REJECTED);
        assertThat(app.getDateRejected()).isEqualTo(LocalDate.now());
        assertThat(offer1.getStatus()).isEqualTo(JobOfferStatus.REJECTED);
        assertThat(offer2.getStatus()).isEqualTo(JobOfferStatus.REJECTED);

        verify(jobApplicationRepository).findByStatusAndDateAppliedBefore(any(Status.class), any(LocalDate.class));
        verify(jobOfferService, times(2)).saveJobOffer(any(JobOffer.class));
        verify(cache).clear();
        verify(jobApplicationRepository).save(app);
    }

    @Test
    void cleanOldApplicationsAndOffersShouldDoNothingWhenNoOldApplications() {
        when(jobApplicationRepository.findByStatusAndDateAppliedBefore(any(Status.class), any(LocalDate.class)))
                .thenReturn(List.of());

        service.cleanOldApplicationsAndOffers();

        verify(jobOfferService, never()).saveJobOffer(any(JobOffer.class));
        verify(cache, never()).clear();
        verify(jobApplicationRepository, never()).save(any(JobApplication.class));
    }

    @Test
    void cleanJobOffersWithAppRejectedShouldRejectOnlyMatchingOffers() {
        JobApplication rejectedApp = new JobApplication();
        rejectedApp.setStatus(Status.REJECTED);
        rejectedApp.setJobId("JOB-REJ");

        JobApplication appliedApp = new JobApplication();
        appliedApp.setStatus(Status.APPLIED);
        appliedApp.setJobId("JOB-APP");

        JobOffer shouldReject = new JobOffer();
        shouldReject.setStatus(JobOfferStatus.PENDING);
        shouldReject.setApplication(rejectedApp);

        JobOffer shouldKeep = new JobOffer();
        shouldKeep.setStatus(JobOfferStatus.PENDING);
        shouldKeep.setApplication(appliedApp);

        when(jobOfferRepository.findByStatus(JobOfferStatus.PENDING)).thenReturn(List.of(shouldReject, shouldKeep));

        service.cleanJobOffersWithAppRejected();

        assertThat(shouldReject.getStatus()).isEqualTo(JobOfferStatus.REJECTED);
        assertThat(shouldKeep.getStatus()).isEqualTo(JobOfferStatus.PENDING);
        verify(jobOfferService, times(1)).saveJobOffer(any(JobOffer.class));
    }
}
