package com.mtripode.jobapp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mtripode.jobapp.facade.dto.CandidateDto;
import com.mtripode.jobapp.facade.facade.CandidateFacade;
import com.mtripode.jobapp.metrics.CandidateMetrics;

import io.micrometer.core.annotation.Timed;

/**
 * REST controller for managing candidates. Uses CandidateFacade to handle DTO ↔
 * Entity conversion and CandidateMetrics to record business counters.
 */
@RestController
@RequestMapping("/candidates")
@CrossOrigin(origins = "http://localhost:3000")
public class CandidateController {

    private final CandidateFacade candidateFacade;
    private final CandidateMetrics candidateMetrics;

    public CandidateController(CandidateFacade candidateFacade, CandidateMetrics candidateMetrics) {
        this.candidateFacade = candidateFacade;
        this.candidateMetrics = candidateMetrics;
    }

    /**
     * Get all candidates.
     */
    @GetMapping
    @Timed(value = "jobapp.candidate.getAll.time", description = "Time to get all candidates")
    public List<CandidateDto> getAllCandidates() {
        candidateMetrics.incrementSearch();
        return candidateFacade.getAllCandidates();
    }

    /**
     * Get candidate by ID.
     */
    @GetMapping("/{id}")
    @Timed(value = "jobapp.candidate.getById.time", description = "Time to get candidate by id")
    public ResponseEntity<CandidateDto> getCandidateById(@PathVariable Long id) {
        candidateMetrics.incrementSearch();
        return candidateFacade.getCandidateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new candidate.
     */
    @PostMapping
    @Timed(value = "jobapp.candidate.create.time", description = "Time to create candidate")
    public CandidateDto createCandidate(@RequestBody CandidateDto candidateDto) {
        candidateMetrics.incrementCreate();
        return candidateFacade.createCandidate(candidateDto);
    }

    /**
     * Update an existing candidate.
     */
    @PutMapping("/{id}")
    @Timed(value = "jobapp.candidate.update.time", description = "Time to update candidate")
    public CandidateDto updateCandidate(@PathVariable Long id, @RequestBody CandidateDto candidateDto) {
        candidateMetrics.incrementUpdate();
        return candidateFacade.updateCandidate(id, candidateDto);
    }

    /**
     * Delete candidate by ID.
     */
    @DeleteMapping("/{id}")
    @Timed(value = "jobapp.candidate.delete.time", description = "Time to delete candidate")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        candidateMetrics.incrementDelete();
        candidateFacade.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Find candidates by full name.
     */
    @GetMapping("/search/fullname")
    @Timed(value = "jobapp.candidate.search.fullname.time", description = "Time to search by full name")
    public List<CandidateDto> findByFullName(@RequestParam String fullName) {
        candidateMetrics.incrementSearch();
        return candidateFacade.findByFullName(fullName);
    }

    /**
     * Find candidate by email.
     */
    @GetMapping("/search/email")
    @Timed(value = "jobapp.candidate.search.email.time", description = "Time to search by email")
    public ResponseEntity<CandidateDto> findByEmail(@RequestParam String email) {
        candidateMetrics.incrementSearch();
        return candidateFacade.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Find candidates by phone number.
     */
    @GetMapping("/search/phone")
    @Timed(value = "jobapp.candidate.search.phone.time", description = "Time to search by phone")
    public List<CandidateDto> findByPhone(@RequestParam String phone) {
        candidateMetrics.incrementSearch();
        return candidateFacade.findByPhone(phone);
    }

    /**
     * Find candidates whose name contains a keyword (case-insensitive).
     */
    @GetMapping("/search/keyword")
    @Timed(value = "jobapp.candidate.search.keyword.time", description = "Time to search by keyword")
    public List<CandidateDto> findByFullNameContaining(@RequestParam String keyword) {
        candidateMetrics.incrementSearch();
        return candidateFacade.findByFullNameContaining(keyword);
    }

    /**
     * Find candidates who have applied to jobs.
     */
    @GetMapping("/with-applications")
    @Timed(value = "jobapp.candidate.withApplications.time", description = "Time to get candidates with applications")
    public List<CandidateDto> findWithApplications() {
        candidateMetrics.incrementSearch();
        return candidateFacade.findWithApplications();
    }

    /**
     * Find candidates with more than X applications.
     */
    @GetMapping("/with-more-than")
    @Timed(value = "jobapp.candidate.withMoreThan.time", description = "Time to get candidates with more than X applications")
    public List<CandidateDto> findCandidatesWithMoreThan(@RequestParam int minApps) {
        candidateMetrics.incrementSearch();
        return candidateFacade.findCandidatesWithMoreThan(minApps);
    }
}
