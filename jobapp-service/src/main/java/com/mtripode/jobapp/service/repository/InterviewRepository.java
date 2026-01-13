package com.mtripode.jobapp.service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mtripode.jobapp.service.model.Interview;

import com.mtripode.jobapp.service.model.InterviewType;


@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {

    // Find interviews by type (ONLINE, ONSITE, PHONE)
    List<Interview> findByType(InterviewType type);

    // Find interviews scheduled after a specific date/time
    List<Interview> findByScheduledAtAfter(LocalDateTime dateTime);

    // Find interviews scheduled before a specific date/time
    List<Interview> findByScheduledAtBefore(LocalDateTime dateTime);

    // Find interviews where feedback contains a keyword (case-insensitive)
    List<Interview> findByFeedbackContainingIgnoreCase(String keyword);

    // Find interviews linked to a specific job application
    List<Interview> findByApplication_Id(Long applicationId);
}
