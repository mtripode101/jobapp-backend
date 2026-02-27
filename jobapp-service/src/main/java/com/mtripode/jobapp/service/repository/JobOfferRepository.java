package com.mtripode.jobapp.service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mtripode.jobapp.service.model.JobOffer;
import com.mtripode.jobapp.service.model.JobOfferStatus;

@Repository
public interface JobOfferRepository extends JpaRepository<JobOffer, Long> {

    List<JobOffer> findByStatus(JobOfferStatus status);

    List<JobOffer> findByOfferedAtAfter(LocalDate date);

    List<JobOffer> findByOfferedAtBefore(LocalDate date);

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

    List<JobOffer> findByExpectedSalaryLessThanAndOfferedSalaryGreaterThan(Double expectedMax, Double offeredMin);

    @Query("SELECT jo FROM JobOffer jo WHERE jo.expectedSalary = jo.offeredSalary")
    List<JobOffer> findByExpectedSalaryEqualsOfferedSalary();
}
