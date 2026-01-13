package com.mtripode.jobapp.facade.facade.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.mtripode.jobapp.facade.dto.CandidateDto;
import com.mtripode.jobapp.facade.facade.CandidateFacade;
import com.mtripode.jobapp.facade.mapper.CandidateMapper;
import com.mtripode.jobapp.service.model.Candidate;
import com.mtripode.jobapp.service.service.CandidateService;

/**
 * Facade for Candidate operations. Delegates to CandidateService and uses
 * CandidateMapper for conversion between DTO and Entity.
 */
@Component
public class CandidateFacadeImpl implements CandidateFacade {

    private final CandidateService candidateService;
    private final CandidateMapper candidateMapper;

    public CandidateFacadeImpl(CandidateService candidateService, CandidateMapper candidateMapper) {
        this.candidateService = candidateService;
        this.candidateMapper = candidateMapper;
    }

    @Override
    public List<CandidateDto> getAllCandidates() {
        return candidateService.findAll()
                .stream()
                .map(candidateMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CandidateDto> getCandidateById(Long id) {
        return candidateService.findById(id).map(candidateMapper::toDto);
    }

    @Override
    public CandidateDto createCandidate(CandidateDto candidateDto) {
        Candidate saved = candidateService.saveCandidate(candidateMapper.toEntity(candidateDto));
        return candidateMapper.toDto(saved);
    }

    @Override
    public CandidateDto updateCandidate(Long id, CandidateDto candidateDto) {
        Candidate existing = candidateService.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id " + id));

        // Update fields selectively
        existing.setFullName(candidateDto.getFullName());
        if (existing.getContactInfo() != null) {
            existing.getContactInfo().setEmail(candidateDto.getEmail());
            existing.getContactInfo().setPhone(candidateDto.getPhone());
            existing.getContactInfo().setLinkedIn(candidateDto.getLinkedIn());
            existing.getContactInfo().setGithub(candidateDto.getGithub());
        }

        Candidate updated = candidateService.saveCandidate(existing);
        return candidateMapper.toDto(updated);
    }

    @Override
    public void deleteCandidate(Long id) {
        candidateService.deleteById(id);
    }

    @Override
    public List<CandidateDto> findByFullName(String fullName) {
        return candidateService.findByFullName(fullName)
                .stream()
                .map(candidateMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CandidateDto> findByEmail(String email) {
        return candidateService.findByEmail(email).map(candidateMapper::toDto);
    }

    @Override
    public List<CandidateDto> findByPhone(String phone) {
        return candidateService.findByPhone(phone)
                .stream()
                .map(candidateMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandidateDto> findByFullNameContaining(String keyword) {
        return candidateService.findByFullNameContainingIgnoreCase(keyword)
                .stream()
                .map(candidateMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandidateDto> findWithApplications() {
        return candidateService.findWithApplications()
                .stream()
                .map(candidateMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CandidateDto> findCandidatesWithMoreThan(int minApps) {
        return candidateService.findCandidatesWithMoreThan(minApps)
                .stream()
                .map(candidateMapper::toDto)
                .collect(Collectors.toList());
    }
}
