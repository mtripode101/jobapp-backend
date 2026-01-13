package com.mtripode.jobapp.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mtripode.jobapp.service.model.Candidate;


@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    // Find candidates by full name
    List<Candidate> findByFullName(String fullName);

    // Find candidates by email (Optional para evitar null)
    Optional<Candidate> findByContactInfoEmail(String email);

    // Find candidates by phone number
    List<Candidate> findByContactInfoPhone(String phone);

    // Find candidates whose name contains a keyword (case-insensitive)
    List<Candidate> findByFullNameContainingIgnoreCase(String keyword);

    // Find candidates who have applied to jobs (non-empty applications list)
    List<Candidate> findByApplicationsIsNotEmpty();

    // Custom query: candidates with more than X applications
    @Query("SELECT c FROM Candidate c WHERE SIZE(c.applications) > :minApps")
    List<Candidate> findCandidatesWithMoreThan(@Param("minApps") int minApps);
}
