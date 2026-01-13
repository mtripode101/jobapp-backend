package com.mtripode.jobapp.service.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mtripode.jobapp.service.model.Interview;
import com.mtripode.jobapp.service.model.InterviewType;
import com.mtripode.jobapp.service.repository.InterviewRepository;
import com.mtripode.jobapp.service.service.InterviewService;

@Service
@Transactional
public class InterviewServiceImpl implements InterviewService{

    private final InterviewRepository interviewRepository;

    public InterviewServiceImpl(InterviewRepository interviewRepository) {
        this.interviewRepository = interviewRepository;
    }

    // Save or update an interview
    @Override
    public Interview saveInterview(Interview interview) {
        return interviewRepository.save(interview);
    }

    // Find interview by ID
    @Override
    public Optional<Interview> findById(Long id) {
        return interviewRepository.findById(id);
    }

    // Find all interviews
    @Override
    public List<Interview> findAll() {
        return interviewRepository.findAll();
    }

    // Delete interview by ID
    @Override
    public void deleteById(Long id) {
        interviewRepository.deleteById(id);
    }

    // Find interviews by type
    @Override
    public List<Interview> findByType(InterviewType type) {
        return interviewRepository.findByType(type);
    }

    // Find interviews scheduled after a specific date/time
    @Override
    public List<Interview> findByScheduledAtAfter(LocalDateTime dateTime) {
        return interviewRepository.findByScheduledAtAfter(dateTime);
    }

    // Find interviews scheduled before a specific date/time
    @Override
    public List<Interview> findByScheduledAtBefore(LocalDateTime dateTime) {
        return interviewRepository.findByScheduledAtBefore(dateTime);
    }

    // Find interviews where feedback contains a keyword (case-insensitive)
    @Override
    public List<Interview> findByFeedbackContainingIgnoreCase(String keyword) {
        return interviewRepository.findByFeedbackContainingIgnoreCase(keyword);
    }

    // Find interviews linked to a specific job application
    @Override
    public List<Interview> findByApplicationId(Long applicationId) {
        return interviewRepository.findByApplication_Id(applicationId);
    }
}
