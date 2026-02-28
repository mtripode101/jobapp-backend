package com.mtripode.jobapp.service.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mtripode.jobapp.service.cache.CacheUtilService;
import com.mtripode.jobapp.service.event.EventType;
import com.mtripode.jobapp.service.event.JobAppEvent;
import com.mtripode.jobapp.service.event.KafkaEventPublisher;
import com.mtripode.jobapp.service.model.Candidate;
import com.mtripode.jobapp.service.model.Company;
import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.JobOffer;
import com.mtripode.jobapp.service.model.JobOfferStatus;
import com.mtripode.jobapp.service.model.Position;
import com.mtripode.jobapp.service.model.Status;
import com.mtripode.jobapp.service.repository.JobApplicationRepository;
import com.mtripode.jobapp.service.service.JobApplicationService;
import com.mtripode.jobapp.service.service.JobOfferService;
import com.mtripode.jobapp.service.validators.StatusTransitionValidator;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
@Transactional
public class JobApplicationServiceImpl implements JobApplicationService {

    private static final Logger log = LoggerFactory.getLogger(JobApplicationServiceImpl.class);

    private final JobApplicationRepository jobApplicationRepository;
    private final JobOfferService jobOfferService;
    private final CacheUtilService cacheUtilService;
    private final KafkaEventPublisher kafkaEventPublisher;

    public JobApplicationServiceImpl(JobApplicationRepository jobApplicationRepository, JobOfferService jobOfferService,
            CacheUtilService cacheUtilService, KafkaEventPublisher kafkaEventPublisher) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.jobOfferService = jobOfferService;
        this.cacheUtilService = cacheUtilService;
        this.kafkaEventPublisher = kafkaEventPublisher;
    }

    @PostConstruct
    public void init() {
        System.out.println("JobApplicationServiceImpl initialized");
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("JobApplicationServiceImpl shutting down");
    }

    @Override
    public JobApplication applyToJob(String sourceLink, String websiteSource, String description,
            Candidate candidate, Company company, Position position, String jobId) {
        JobApplication application = new JobApplication(
                sourceLink,
                websiteSource,
                LocalDate.now(),
                description,
                candidate,
                company,
                position,
                Status.APPLIED,
                jobId);
        JobApplication saved = jobApplicationRepository.save(application);
        publishApplicationCreatedEvent(saved, "api");
        return saved;
    }

    @Override
    public JobApplication rejectApplication(Long id) {
        Optional<JobApplication> optional = jobApplicationRepository.findById(id);
        if (optional.isPresent()) {
            JobApplication app = optional.get();
            app.setStatus(Status.REJECTED);
            app.setDateRejected(LocalDate.now());
            return jobApplicationRepository.save(app);
        }
        throw new IllegalArgumentException("Application not found with id " + id);
    }

    @Override
    public JobApplication updateStatus(Long id, Status newStatus) {
        JobApplication app = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with id " + id));

        Status current = app.getStatus();
        if (!StatusTransitionValidator.canTransition(current, newStatus)) {
            throw new IllegalStateException("Invalid transition from " + current + " to " + newStatus);
        }

        app.setStatus(newStatus);
        if (newStatus == Status.REJECTED) {
            app.setDateRejected(LocalDate.now());
        }

        JobApplication saved = jobApplicationRepository.save(app);
        publishApplicationStatusChangedEvent(saved, current, newStatus, "api");
        return saved;
    }

    @Override
    public JobApplication applyRejected(String sourceLink, String websiteSource, String description,
            Candidate candidate, Company company, Position position, String jobId) {
        JobApplication application = new JobApplication(
                sourceLink,
                websiteSource,
                LocalDate.now(),
                description,
                candidate,
                company,
                position,
                Status.REJECTED,
                jobId);
        application.setDateRejected(LocalDate.now());
        return jobApplicationRepository.save(application);
    }

    @Transactional
    @Override
    public JobApplication update(Long id, JobApplication updateJobApplication) {
        Optional<JobApplication> existingApplication = Optional.ofNullable(jobApplicationRepository.findById(id))
                .orElse(Optional.empty());
        Status previousStatus = existingApplication
                .map(JobApplication::getStatus)
                .orElse(null);

        List<JobOffer> applicationOffers = jobOfferService.findByApplicationId(id);

        if (updateJobApplication.getStatus() == Status.REJECTED) {
            for (JobOffer offer : applicationOffers) {
                if (offer.getStatus() != JobOfferStatus.REJECTED) {
                    offer.setStatus(JobOfferStatus.REJECTED);
                    jobOfferService.saveJobOffer(offer);
                }
            }
            if (!applicationOffers.isEmpty()) {
                cacheUtilService.clearCache("job-offers");
            }
        }

        updateJobApplication.setId(id);
        updateJobApplication.setOffers(applicationOffers);

        if (updateJobApplication.getInterviews() != null) {
            updateJobApplication.getInterviews().forEach(interview -> interview.setApplication(updateJobApplication));
        }

        if (updateJobApplication.getOffers() != null) {
            updateJobApplication.getOffers().forEach(offer -> offer.setApplication(updateJobApplication));
        }

        JobApplication saved = jobApplicationRepository.save(updateJobApplication);
        publishApplicationUpdatedEvent(saved, previousStatus, saved.getStatus(), "api");
        return saved;
    }

    @Override
    public List<JobApplication> listByStatus(Status status) {
        return jobApplicationRepository.findByStatus(status);
    }

    @Override
    public List<JobApplication> listAll() {
        return jobApplicationRepository.findAll();
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<List<JobApplication>> listAllAsync() {
        List<JobApplication> result = jobApplicationRepository.findAll();
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public List<JobApplication> listByCompanyName(String companyName) {
        return jobApplicationRepository.findByCompany_Name(companyName);
    }

    @Override
    public List<JobApplication> listByWebsiteSource(String websiteSource) {
        return jobApplicationRepository.findByWebsiteSource(websiteSource);
    }

    @Override
    public List<JobApplication> listBySourceLink(String sourceLink) {
        return jobApplicationRepository.findBySourceLink(sourceLink);
    }

    @Override
    public List<JobApplication> listByStatusAndDateRejected(Status status, LocalDate dateRejected) {
        return jobApplicationRepository.findByStatusAndDateRejected(status, dateRejected);
    }

    @Override
    public List<JobApplication> listByCandidateFullName(String fullName) {
        return jobApplicationRepository.findByCandidate_FullName(fullName);
    }

    @Override
    public List<JobApplication> listByPositionTitle(String title) {
        return jobApplicationRepository.findByPosition_Title(title);
    }

    @Override
    public List<JobApplication> listByDateAppliedAfter(LocalDate date) {
        return jobApplicationRepository.findByDateAppliedAfter(date);
    }

    @Override
    public List<JobApplication> listByDateAppliedBefore(LocalDate date) {
        return jobApplicationRepository.findByDateAppliedBefore(date);
    }

    @Override
    public List<JobApplication> listByCompanyNameAndStatus(String companyName, Status status) {
        return jobApplicationRepository.findByCompany_NameAndStatus(companyName, status);
    }

    @Override
    public Optional<JobApplication> findById(Long id) {
        Optional<JobApplication> jobOptional = jobApplicationRepository.findById(id);
        return jobOptional;
    }

    @Override
    public void deleteById(Long id) {
        if (!jobApplicationRepository.existsById(id)) {
            throw new IllegalArgumentException("Application not found with id " + id);
        }
        jobApplicationRepository.deleteById(id);
    }

    @Override
    public JobApplication findByJobId(String jobId) {
        return jobApplicationRepository.findByJobId(jobId);
    }

    @Override
    public Page<JobApplication> listAll(Pageable pageable) {
        return jobApplicationRepository.findAll(pageable);
    }

    private void publishApplicationCreatedEvent(JobApplication app, String source) {
        JobAppEvent event = JobAppEvent.newEvent(EventType.APPLICATION_CREATED);
        event.setApplicationId(app.getId());
        event.setJobId(app.getJobId());
        event.setCandidateId(app.getCandidate() != null ? app.getCandidate().getId() : null);
        event.setCompanyId(app.getCompany() != null ? app.getCompany().getId() : null);
        event.setNewStatus(app.getStatus() != null ? app.getStatus().name() : null);
        event.setSource(source);
        kafkaEventPublisher.publishApplicationEvent(event);
    }

    private void publishApplicationStatusChangedEvent(JobApplication app, Status previousStatus, Status newStatus, String source) {
        JobAppEvent event = JobAppEvent.newEvent(EventType.APPLICATION_STATUS_CHANGED);
        event.setApplicationId(app.getId());
        event.setJobId(app.getJobId());
        event.setCandidateId(app.getCandidate() != null ? app.getCandidate().getId() : null);
        event.setCompanyId(app.getCompany() != null ? app.getCompany().getId() : null);
        event.setPreviousStatus(previousStatus != null ? previousStatus.name() : null);
        event.setNewStatus(newStatus != null ? newStatus.name() : null);
        event.setSource(source);
        kafkaEventPublisher.publishApplicationEvent(event);
    }

    private void publishApplicationUpdatedEvent(JobApplication app, Status previousStatus, Status newStatus, String source) {
        JobAppEvent event = JobAppEvent.newEvent(EventType.APPLICATION_UPDATED);
        event.setApplicationId(app.getId());
        event.setJobId(app.getJobId());
        event.setCandidateId(app.getCandidate() != null ? app.getCandidate().getId() : null);
        event.setCompanyId(app.getCompany() != null ? app.getCompany().getId() : null);
        event.setPreviousStatus(previousStatus != null ? previousStatus.name() : null);
        event.setNewStatus(newStatus != null ? newStatus.name() : null);
        event.setSource(source);
        kafkaEventPublisher.publishApplicationEvent(event);
    }
}
