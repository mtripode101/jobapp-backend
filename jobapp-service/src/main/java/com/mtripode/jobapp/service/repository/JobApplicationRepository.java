package com.mtripode.jobapp.service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.Status;
import com.mtripode.jobapp.service.model.JobOfferStatus;


@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    // Find all applications by status (e.g., APPLIED, REJECTED, OFFERED)
    List<JobApplication> findByStatus(Status status);

    // Find all applications by company name
    List<JobApplication> findByCompany_Name(String companyName);

    // Find all applications by the website source
    List<JobApplication> findByWebsiteSource(String websiteSource);

    // Find all applications by the source link
    List<JobApplication> findBySourceLink(String sourceLink);

    // Find all applications by status and rejection date
    List<JobApplication> findByStatusAndDateRejected(Status status, LocalDate dateRejected);

    // --- Extra queries to practice with new models ---
    // Find all applications by candidate full name
    List<JobApplication> findByCandidate_FullName(String fullName);

    // Find all applications by position title
    List<JobApplication> findByPosition_Title(String title);

    // Find all applications submitted after a given date
    List<JobApplication> findByDateAppliedAfter(LocalDate date);

    // Find all applications submitted before a given date
    List<JobApplication> findByDateAppliedBefore(LocalDate date);

    // Find all applications by company and status
    List<JobApplication> findByCompany_NameAndStatus(String companyName, Status status);

    // --- NEW: Queries involving offers ---
    // Find applications that have at least one offer
    List<JobApplication> findByOffersIsNotEmpty();

    // Find applications that have no offers
    List<JobApplication> findByOffersIsEmpty();

    // Find applications by offer status (e.g., PENDING, ACCEPTED, REJECTED)
    List<JobApplication> findByOffers_Status(JobOfferStatus status);

    // Find applications by offer date
    List<JobApplication> findByOffers_OfferedAt(LocalDate offeredAt);

    // Find applications with offers made after a given date
    List<JobApplication> findByOffers_OfferedAtAfter(LocalDate date);

    // Find applications with offers made before a given date
    List<JobApplication> findByOffers_OfferedAtBefore(LocalDate date);
}
