package com.mtripode.jobapp.facade.facade;

import com.mtripode.jobapp.facade.dto.CandidateDto;

import java.util.List;
import java.util.Optional;

public interface CandidateFacade {

 
    List<CandidateDto> getAllCandidates();

    Optional<CandidateDto> getCandidateById(Long id);

    CandidateDto createCandidate(CandidateDto candidateDto);

    CandidateDto updateCandidate(Long id, CandidateDto candidateDto);

    void deleteCandidate(Long id);

    List<CandidateDto> findByFullName(String fullName);

    Optional<CandidateDto> findByEmail(String email);

    List<CandidateDto> findByPhone(String phone);

    List<CandidateDto> findByFullNameContaining(String keyword);

    List<CandidateDto> findWithApplications();

    List<CandidateDto> findCandidatesWithMoreThan(int minApps);
    
}
