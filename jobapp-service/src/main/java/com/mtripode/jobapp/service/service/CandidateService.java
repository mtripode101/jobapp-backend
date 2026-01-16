package com.mtripode.jobapp.service.service;

import java.util.List;
import java.util.Optional;

import com.mtripode.jobapp.service.model.Candidate;


public interface CandidateService {

    Candidate saveCandidate(Candidate candidate);

    Optional<Candidate> findById(Long id);

    List<Candidate> findAll();

    void deleteById(Long id);

    List<Candidate> findByFullName(String fullName);

    Optional<Candidate> findByEmail(String email);

    List<Candidate> findByPhone(String phone);

    List<Candidate> findByFullNameContainingIgnoreCase(String keyword);

    List<Candidate> findWithApplications();

    List<Candidate> findCandidatesWithMoreThan(int minApps);
    
}
