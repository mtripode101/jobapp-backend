package com.mtripode.jobapp.service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.mtripode.jobapp.service.model.Interview;
import com.mtripode.jobapp.service.model.InterviewType;

public interface InterviewService {

    // Save or update an interview
    Interview saveInterview(Interview interview);

    // Find interview by ID
    Optional<Interview> findById(Long id);

    // Find all interviews
    List<Interview> findAll();

    // Delete interview by ID
    void deleteById(Long id);

    // Find interviews by type
    List<Interview> findByType(InterviewType type);

    // Find interviews scheduled after a specific date/time
    List<Interview> findByScheduledAtAfter(LocalDateTime dateTime);

    // Find interviews scheduled before a specific date/time
    List<Interview> findByScheduledAtBefore(LocalDateTime dateTime);

    // Find interviews where feedback contains a keyword (case-insensitive)
    List<Interview> findByFeedbackContainingIgnoreCase(String keyword);

    // Find interviews linked to a specific job application
    List<Interview> findByApplicationId(Long applicationId);
}
