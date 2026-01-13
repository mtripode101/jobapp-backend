package com.mtripode.jobapp.service.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.JobOffer;
import com.mtripode.jobapp.service.model.JobOfferStatus;
import com.mtripode.jobapp.service.repository.JobApplicationRepository;
import com.mtripode.jobapp.service.repository.JobOfferRepository;
import com.mtripode.jobapp.service.model.JobOffer;
import com.mtripode.jobapp.service.model.JobOfferStatus;
import com.mtripode.jobapp.service.service.JobOfferService;

@Service
@Transactional
public class JobOfferServiceImpl implements JobOfferService {

    private final JobOfferRepository jobOfferRepository;
    private final JobApplicationRepository jobApplicationRepository;

    public JobOfferServiceImpl(JobOfferRepository jobOfferRepository,
            JobApplicationRepository jobApplicationRepository) {
        this.jobOfferRepository = jobOfferRepository;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    // Save or update a job offer
    @Override
    public JobOffer saveJobOffer(JobOffer jobOffer) {
        return jobOfferRepository.save(jobOffer);
    }

    // Create and attach a new offer to an application
    @Override
    public JobOffer createOffer(Long applicationId, LocalDate offeredAt, JobOfferStatus status) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with id " + applicationId));

        JobOffer offer = new JobOffer(offeredAt, status, application);
        application.addOffer(offer); // maintain bidirectional relationship
        jobApplicationRepository.save(application);

        return jobOfferRepository.save(offer);
    }

    // Find job offer by ID
    @Override
    public Optional<JobOffer> findById(Long id) {
        return jobOfferRepository.findById(id);
    }

    // Find all job offers
    @Override
    public List<JobOffer> findAll() {
        return jobOfferRepository.findAll();
    }

    // Delete job offer by ID
    @Override
    public void deleteById(Long id) {
        jobOfferRepository.deleteById(id);
    }

    // Find job offers by status
    @Override
    public List<JobOffer> findByStatus(JobOfferStatus status) {
        return jobOfferRepository.findByStatus(status);
    }

    // Find job offers made after a specific date
    @Override
    public List<JobOffer> findByOfferedAtAfter(LocalDate date) {
        return jobOfferRepository.findByOfferedAtAfter(date);
    }

    // Find job offers made before a specific date
    @Override
    public List<JobOffer> findByOfferedAtBefore(LocalDate date) {
        return jobOfferRepository.findByOfferedAtBefore(date);
    }

    // Find job offers linked to a specific job application
    @Override
    public List<JobOffer> findByApplicationId(Long applicationId) {
        return jobOfferRepository.findByApplication_Id(applicationId);
    }

    // --- Extra convenience methods ---
    // Accept an offer
    @Override
    public JobOffer acceptOffer(Long offerId) {
        JobOffer offer = jobOfferRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found with id " + offerId));
        offer.setStatus(JobOfferStatus.ACCEPTED);
        return jobOfferRepository.save(offer);
    }

    // Reject an offer
    @Override
    public JobOffer rejectOffer(Long offerId) {
        JobOffer offer = jobOfferRepository.findById(offerId)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found with id " + offerId));
        offer.setStatus(JobOfferStatus.REJECTED);
        return jobOfferRepository.save(offer);
    }
}
