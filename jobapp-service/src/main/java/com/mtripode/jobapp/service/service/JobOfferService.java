package com.mtripode.jobapp.service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.mtripode.jobapp.service.model.JobOffer;
import com.mtripode.jobapp.service.model.JobOfferStatus;

public interface JobOfferService {

    // Save or update a job offer
    JobOffer saveJobOffer(JobOffer jobOffer);

    // Create and attach a new offer to an application
    JobOffer createOffer(Long applicationId, LocalDate offeredAt, JobOfferStatus status);

    // Find job offer by ID
    Optional<JobOffer> findById(Long id);

    // Find all job offers
    List<JobOffer> findAll();

    // Delete job offer by ID
    void deleteById(Long id);

    // Find job offers by status
    List<JobOffer> findByStatus(JobOfferStatus status);

    // Find job offers made after a specific date
    List<JobOffer> findByOfferedAtAfter(LocalDate date);

    // Find job offers made before a specific date
    List<JobOffer> findByOfferedAtBefore(LocalDate date);

    // Find job offers linked to a specific job application
    List<JobOffer> findByApplicationId(Long applicationId);

    // Accept an offer
    JobOffer acceptOffer(Long offerId);

    // Reject an offer
    JobOffer rejectOffer(Long offerId);
}
