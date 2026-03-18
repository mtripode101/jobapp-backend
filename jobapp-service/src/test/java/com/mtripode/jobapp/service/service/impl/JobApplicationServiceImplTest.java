package com.mtripode.jobapp.service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.mtripode.jobapp.service.cache.CacheUtilService;
import com.mtripode.jobapp.service.event.KafkaEventPublisher;
import com.mtripode.jobapp.service.model.Candidate;
import com.mtripode.jobapp.service.model.Company;
import com.mtripode.jobapp.service.model.Interview;
import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.JobOffer;
import com.mtripode.jobapp.service.model.JobOfferStatus;
import com.mtripode.jobapp.service.model.Position;
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

    @Mock
    private KafkaEventPublisher kafkaEventPublisher;

    private JobApplicationServiceImpl service;
    private CacheUtilService cacheUtilService;

    @BeforeEach
    void setUp() {
        cacheUtilService = new CacheUtilService(cacheManager);
        service = new JobApplicationServiceImpl(
                jobApplicationRepository,
                jobOfferService,
                cacheUtilService,
                kafkaEventPublisher);
        lenient().when(jobApplicationRepository.save(any(JobApplication.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void applyToJobShouldCreateApplicationWithAppliedStatus() {
        Candidate candidate = new Candidate();
        Company company = new Company();
        Position position = new Position();

        JobApplication result = service.applyToJob(
                "source-link",
                "website",
                "desc",
                candidate,
                company,
                position,
                "JOB-1");

        assertThat(result.getStatus()).isEqualTo(Status.APPLIED);
        assertThat(result.getDateApplied()).isEqualTo(LocalDate.now());
        assertThat(result.getCandidate()).isSameAs(candidate);
        assertThat(result.getCompany()).isSameAs(company);
        assertThat(result.getPosition()).isSameAs(position);
        assertThat(result.getJobId()).isEqualTo("JOB-1");
        verify(jobApplicationRepository).save(any(JobApplication.class));
    }

    @Test
    void rejectApplicationShouldSetRejectedStatusAndDate() {
        JobApplication application = new JobApplication();
        application.setStatus(Status.APPLIED);
        when(jobApplicationRepository.findById(7L)).thenReturn(Optional.of(application));

        JobApplication result = service.rejectApplication(7L);

        assertThat(result.getStatus()).isEqualTo(Status.REJECTED);
        assertThat(result.getDateRejected()).isEqualTo(LocalDate.now());
        verify(jobApplicationRepository).save(application);
    }

    @Test
    void rejectApplicationShouldThrowWhenNotFound() {
        when(jobApplicationRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.rejectApplication(7L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Application not found with id 7");
    }

    @Test
    void updateStatusShouldPersistValidTransition() {
        JobApplication application = new JobApplication();
        application.setStatus(Status.APPLIED);
        when(jobApplicationRepository.findById(8L)).thenReturn(Optional.of(application));

        JobApplication result = service.updateStatus(8L, Status.INTERVIEW_SCHEDULED);

        assertThat(result.getStatus()).isEqualTo(Status.INTERVIEW_SCHEDULED);
        verify(jobApplicationRepository).save(application);
    }

    @Test
    void updateStatusShouldSetRejectedDateWhenNewStatusRejected() {
        JobApplication application = new JobApplication();
        application.setStatus(Status.APPLIED);
        when(jobApplicationRepository.findById(9L)).thenReturn(Optional.of(application));

        JobApplication result = service.updateStatus(9L, Status.REJECTED);

        assertThat(result.getStatus()).isEqualTo(Status.REJECTED);
        assertThat(result.getDateRejected()).isEqualTo(LocalDate.now());
        verify(jobApplicationRepository).save(application);
    }

    @Test
    void updateStatusShouldThrowForInvalidTransition() {
        JobApplication application = new JobApplication();
        application.setStatus(Status.REJECTED);
        when(jobApplicationRepository.findById(10L)).thenReturn(Optional.of(application));

        assertThatThrownBy(() -> service.updateStatus(10L, Status.HIRED))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Invalid transition");
    }

    @Test
    void updateStatusShouldThrowWhenApplicationNotFound() {
        when(jobApplicationRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus(10L, Status.REJECTED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Application not found with id 10");
    }

    @Test
    void applyRejectedShouldCreateApplicationAsRejectedWithDate() {
        JobApplication result = service.applyRejected(
                "source-link",
                "website",
                "desc",
                new Candidate(),
                new Company(),
                new Position(),
                "JOB-2");

        assertThat(result.getStatus()).isEqualTo(Status.REJECTED);
        assertThat(result.getDateRejected()).isEqualTo(LocalDate.now());
        verify(jobApplicationRepository).save(any(JobApplication.class));
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
    void updateShouldNotClearCacheWhenRejectedButNoOffers() {
        Long applicationId = 50L;
        when(jobOfferService.findByApplicationId(applicationId)).thenReturn(List.of());

        JobApplication toUpdate = new JobApplication();
        toUpdate.setStatus(Status.REJECTED);

        service.update(applicationId, toUpdate);

        verify(jobOfferService, never()).saveJobOffer(any(JobOffer.class));
        verify(cacheManager, never()).getCache("job-offers");
    }

    @Test
    void updateShouldSetBackReferencesForInterviewsAndOffers() {
        Long applicationId = 55L;
        Interview interview = new Interview();
        JobOffer offer = new JobOffer();
        when(jobOfferService.findByApplicationId(applicationId)).thenReturn(List.of(offer));

        JobApplication toUpdate = new JobApplication();
        toUpdate.setStatus(Status.APPLIED);
        toUpdate.setInterviews(new ArrayList<>(List.of(interview)));

        JobApplication result = service.update(applicationId, toUpdate);

        assertThat(result.getId()).isEqualTo(applicationId);
        assertThat(interview.getApplication()).isSameAs(result);
        assertThat(offer.getApplication()).isSameAs(result);
        assertThat(result.getOffers()).containsExactly(offer);
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

    @Test
    void listMethodsShouldDelegateToRepository() {
        LocalDate date = LocalDate.now();
        List<JobApplication> expected = List.of(new JobApplication());
        when(jobApplicationRepository.findByStatus(Status.APPLIED)).thenReturn(expected);
        when(jobApplicationRepository.findAll()).thenReturn(expected);
        when(jobApplicationRepository.findByCompany_Name("Acme")).thenReturn(expected);
        when(jobApplicationRepository.findByWebsiteSource("linkedin")).thenReturn(expected);
        when(jobApplicationRepository.findBySourceLink("src")).thenReturn(expected);
        when(jobApplicationRepository.findByStatusAndDateRejected(Status.REJECTED, date)).thenReturn(expected);
        when(jobApplicationRepository.findByCandidate_FullName("John Doe")).thenReturn(expected);
        when(jobApplicationRepository.findByPosition_Title("Engineer")).thenReturn(expected);
        when(jobApplicationRepository.findByDateAppliedAfter(date)).thenReturn(expected);
        when(jobApplicationRepository.findByDateAppliedBefore(date)).thenReturn(expected);
        when(jobApplicationRepository.findByCompany_NameAndStatus("Acme", Status.APPLIED)).thenReturn(expected);

        assertThat(service.listByStatus(Status.APPLIED)).isSameAs(expected);
        assertThat(service.listAll()).isSameAs(expected);
        assertThat(service.listByCompanyName("Acme")).isSameAs(expected);
        assertThat(service.listByWebsiteSource("linkedin")).isSameAs(expected);
        assertThat(service.listBySourceLink("src")).isSameAs(expected);
        assertThat(service.listByStatusAndDateRejected(Status.REJECTED, date)).isSameAs(expected);
        assertThat(service.listByCandidateFullName("John Doe")).isSameAs(expected);
        assertThat(service.listByPositionTitle("Engineer")).isSameAs(expected);
        assertThat(service.listByDateAppliedAfter(date)).isSameAs(expected);
        assertThat(service.listByDateAppliedBefore(date)).isSameAs(expected);
        assertThat(service.listByCompanyNameAndStatus("Acme", Status.APPLIED)).isSameAs(expected);

        verify(jobApplicationRepository).findByStatus(Status.APPLIED);
        verify(jobApplicationRepository).findAll();
        verify(jobApplicationRepository).findByCompany_Name("Acme");
        verify(jobApplicationRepository).findByWebsiteSource("linkedin");
        verify(jobApplicationRepository).findBySourceLink("src");
        verify(jobApplicationRepository).findByStatusAndDateRejected(Status.REJECTED, date);
        verify(jobApplicationRepository).findByCandidate_FullName("John Doe");
        verify(jobApplicationRepository).findByPosition_Title("Engineer");
        verify(jobApplicationRepository).findByDateAppliedAfter(date);
        verify(jobApplicationRepository).findByDateAppliedBefore(date);
        verify(jobApplicationRepository).findByCompany_NameAndStatus("Acme", Status.APPLIED);
    }

    @Test
    void listAllAsyncShouldReturnCompletedFutureWithRepositoryData() {
        List<JobApplication> expected = List.of(new JobApplication());
        when(jobApplicationRepository.findAll()).thenReturn(expected);

        CompletableFuture<List<JobApplication>> future = service.listAllAsync();

        assertThat(future).isCompleted();
        assertThat(future.join()).isSameAs(expected);
    }

    @Test
    void findByIdShouldDelegateToRepository() {
        JobApplication app = new JobApplication();
        when(jobApplicationRepository.findById(20L)).thenReturn(Optional.of(app));

        Optional<JobApplication> result = service.findById(20L);

        assertThat(result).contains(app);
        verify(jobApplicationRepository).findById(20L);
    }

    @Test
    void deleteByIdShouldDeleteWhenExists() {
        when(jobApplicationRepository.existsById(30L)).thenReturn(true);

        service.deleteById(30L);

        verify(jobApplicationRepository).deleteById(30L);
    }

    @Test
    void deleteByIdShouldThrowWhenApplicationDoesNotExist() {
        when(jobApplicationRepository.existsById(31L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteById(31L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Application not found with id 31");
    }

    @Test
    void findByJobIdShouldDelegateToRepository() {
        JobApplication app = new JobApplication();
        when(jobApplicationRepository.findByJobId("JOB-77")).thenReturn(app);

        JobApplication result = service.findByJobId("JOB-77");

        assertThat(result).isSameAs(app);
        verify(jobApplicationRepository).findByJobId("JOB-77");
    }

    @Test
    void listAllPageableShouldDelegateToRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<JobApplication> expected = new PageImpl<>(List.of(new JobApplication()));
        when(jobApplicationRepository.findAll(pageable)).thenReturn(expected);

        Page<JobApplication> result = service.listAll(pageable);

        assertThat(result).isSameAs(expected);
        verify(jobApplicationRepository).findAll(pageable);
    }

    private static JobOffer buildOffer(JobOfferStatus status) {
        JobOffer offer = new JobOffer();
        offer.setStatus(status);
        return offer;
    }
}
