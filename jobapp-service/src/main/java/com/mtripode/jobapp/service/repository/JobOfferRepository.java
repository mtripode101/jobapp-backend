package com.mtripode.jobapp.service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mtripode.jobapp.service.model.JobOffer;
import com.mtripode.jobapp.service.model.JobOfferStatus;


@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {

    // Find job offers by status (PENDING, ACCEPTED, REJECTED)
    List<JobOffer> findByStatus(JobOfferStatus status);

    // Find job offers made after a specific date
    List<JobOffer> findByOfferedAtAfter(LocalDate date);

    // Find job offers made before a specific date
    List<JobOffer> findByOfferedAtBefore(LocalDate date);

    // Find job offers linked to a specific job application
    List<JobOffer> findByApplication_Id(Long applicationId);

    List<JobOffer> findByExpectedSalaryGreaterThan(Double salary);

    List<JobOffer> findByOfferedSalaryLessThan(Double salary);

    List<JobOffer> findByExpectedSalaryBetween(Double minSalary, Double maxSalary);

    List<JobOffer> findByOfferedSalaryBetween(Double minSalary, Double maxSalary);

    List<JobOffer> findByExpectedSalaryIsNull();

    List<JobOffer> findByOfferedSalaryIsNull();

    List<JobOffer> findByExpectedSalaryIsNotNull();

    List<JobOffer> findByOfferedSalaryIsNotNull();

    List<JobOffer> findByExpectedSalaryGreaterThanAndOfferedSalaryLessThan(Double expectedMin, Double offeredMax);
}
