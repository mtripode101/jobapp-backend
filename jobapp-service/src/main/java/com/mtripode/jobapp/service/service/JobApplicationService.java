package com.mtripode.jobapp.service.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.Candidate;
import com.mtripode.jobapp.service.model.Company;
import com.mtripode.jobapp.service.model.Position;
import com.mtripode.jobapp.service.model.Status;

public interface JobApplicationService {

    // Apply to a job with candidate, company, and position details
    JobApplication applyToJob(String sourceLink, String websiteSource, String description,
            Candidate candidate, Company company, Position position);

    // Reject an existing application by ID
    JobApplication rejectApplication(Long id);

    // Update the status of an application with validation
    JobApplication updateStatus(Long id, Status newStatus);

    // Create a rejected application directly
    JobApplication applyRejected(String sourceLink, String websiteSource, String description,
            Candidate candidate, Company company, Position position);

    // List applications by status
    List<JobApplication> listByStatus(Status status);

    // List all applications
    List<JobApplication> listAll();

    // Additional query methods
    List<JobApplication> listByCompanyName(String companyName);

    List<JobApplication> listByWebsiteSource(String websiteSource);

    List<JobApplication> listBySourceLink(String sourceLink);

    List<JobApplication> listByStatusAndDateRejected(Status status, LocalDate dateRejected);

    List<JobApplication> listByCandidateFullName(String fullName);

    List<JobApplication> listByPositionTitle(String title);

    List<JobApplication> listByDateAppliedAfter(LocalDate date);

    List<JobApplication> listByDateAppliedBefore(LocalDate date);

    List<JobApplication> listByCompanyNameAndStatus(String companyName, Status status);

    // Find application by ID
    Optional<JobApplication> findById(Long id);

    // Delete application by ID
    void deleteById(Long id);
}
