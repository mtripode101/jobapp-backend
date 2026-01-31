package com.mtripode.jobapp.service.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mtripode.jobapp.service.model.Candidate;
import com.mtripode.jobapp.service.model.Company;
import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.Position;
import com.mtripode.jobapp.service.model.Status;
import com.mtripode.jobapp.service.repository.JobApplicationRepository;
import com.mtripode.jobapp.service.service.JobApplicationService;
import com.mtripode.jobapp.service.validators.StatusTransitionValidator;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
@Transactional
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;

    public JobApplicationServiceImpl(JobApplicationRepository jobApplicationRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
    }

    @PostConstruct
    public void init() {
        System.out.println("JobApplicationServiceImpl initialized");
        // e.g., warm caches, validate config
    }

    @PreDestroy
    public void shutdown() {
        System.out.println("JobApplicationServiceImpl shutting down");
        // e.g., close resources
    }

    /**
     * Apply to a job with candidate, company, and position details.
     */
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
                jobId
        );
        return jobApplicationRepository.save(application);
    }

    /**
     * Reject an existing application by ID.
     */
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

    /**
     * Update the status of an application with validation.
     */
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
        return jobApplicationRepository.save(app);
    }

    /**
     * Create a rejected application directly.
     */
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
                jobId
        );
        application.setDateRejected(LocalDate.now());
        return jobApplicationRepository.save(application);
    }

    /**
     * List applications by status.
     */
    @Override
    public List<JobApplication> listByStatus(Status status) {
        return jobApplicationRepository.findByStatus(status);
    }

    /**
     * List all applications.
     */
    @Override
    public List<JobApplication> listAll() {
        return jobApplicationRepository.findAll();
    }

    // simple async wrapper that runs repository call in the configured executor
    @Async("taskExecutor")
    @Override
    public CompletableFuture<List<JobApplication>> listAllAsync() {
        List<JobApplication> result = jobApplicationRepository.findAll();
        return CompletableFuture.completedFuture(result);
    }

    // --- Additional query methods merged from repository ---
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

    /**
     * Find application by ID.
     */
    @Override
    public Optional<JobApplication> findById(Long id) {
        return jobApplicationRepository.findById(id);
    }

    /**
     * Delete application by ID.
     */
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
}
