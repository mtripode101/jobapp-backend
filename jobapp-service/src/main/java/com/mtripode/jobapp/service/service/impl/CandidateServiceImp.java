package com.mtripode.jobapp.service.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mtripode.jobapp.service.model.Candidate;
import com.mtripode.jobapp.service.repository.CandidateRepository;
import com.mtripode.jobapp.service.service.CandidateService;

@Service
@Transactional
public class CandidateServiceImp implements CandidateService{

    private final CandidateRepository candidateRepository;

    public CandidateServiceImp(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    // Save or update a candidate
    @Override
    public Candidate saveCandidate(Candidate candidate) {
        return candidateRepository.save(candidate);
    }

    // Find candidate by ID
     @Override
    public Optional<Candidate> findById(Long id) {
        return candidateRepository.findById(id);
    }

    // Find all candidates
     @Override
    public List<Candidate> findAll() {
        return candidateRepository.findAll();
    }

    // Delete candidate by ID
     @Override
    public void deleteById(Long id) {
        candidateRepository.deleteById(id);
    }

    // Find candidates by full name
     @Override
    public List<Candidate> findByFullName(String fullName) {
        return candidateRepository.findByFullName(fullName);
    }

    // Find candidate by email (ahora usando ContactInfo)
     @Override
    public Optional<Candidate> findByEmail(String email) {
        return candidateRepository.findByContactInfoEmail(email);
    }

    // Find candidates by phone number (ahora usando ContactInfo)
     @Override
    public List<Candidate> findByPhone(String phone) {
        return candidateRepository.findByContactInfoPhone(phone);
    }

    // Find candidates whose name contains a keyword (case-insensitive)
     @Override
    public List<Candidate> findByFullNameContainingIgnoreCase(String keyword) {
        return candidateRepository.findByFullNameContainingIgnoreCase(keyword);
    }

    // Find candidates who have applied to jobs (non-empty applications list)
     @Override
    public List<Candidate> findWithApplications() {
        return candidateRepository.findByApplicationsIsNotEmpty();
    }

    // Find candidates with more than X applications
     @Override
    public List<Candidate> findCandidatesWithMoreThan(int minApps) {
        return candidateRepository.findCandidatesWithMoreThan(minApps);
    }
}
