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

import com.mtripode.jobapp.facade.facade.CandidateFacade;
import com.mtripode.jobapp.facade.facade.impl.CandidateFacadeImpl;
import com.mtripode.jobapp.facade.dto.CandidateDto;


/**
 * REST controller for managing candidates. Uses CandidateFacade to handle DTO ↔
 * Entity conversion.
 */
@RestController
@RequestMapping("/candidates")
@CrossOrigin(origins = "http://localhost:3000") // enable CORS for frontend
public class CandidateController {

    private final CandidateFacade candidateFacade;

    public CandidateController(CandidateFacadeImpl candidateFacade) {
        this.candidateFacade = candidateFacade;
    }

    /**
     * Get all candidates.
     */
    @GetMapping
    public List<CandidateDto> getAllCandidates() {
        return candidateFacade.getAllCandidates();
    }

    /**
     * Get candidate by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CandidateDto> getCandidateById(@PathVariable Long id) {
        return candidateFacade.getCandidateById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new candidate.
     */
    @PostMapping
    public CandidateDto createCandidate(@RequestBody CandidateDto candidateDto) {
        return candidateFacade.createCandidate(candidateDto);
    }

    /**
     * Update an existing candidate.
     */
    @PutMapping("/{id}")
    public CandidateDto updateCandidate(@PathVariable Long id, @RequestBody CandidateDto candidateDto) {
        return candidateFacade.updateCandidate(id, candidateDto);
    }

    /**
     * Delete candidate by ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        candidateFacade.deleteCandidate(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Find candidates by full name.
     */
    @GetMapping("/search/fullname")
    public List<CandidateDto> findByFullName(@RequestParam String fullName) {
        return candidateFacade.findByFullName(fullName);
    }

    /**
     * Find candidate by email.
     */
    @GetMapping("/search/email")
    public ResponseEntity<CandidateDto> findByEmail(@RequestParam String email) {
        return candidateFacade.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Find candidates by phone number.
     */
    @GetMapping("/search/phone")
    public List<CandidateDto> findByPhone(@RequestParam String phone) {
        return candidateFacade.findByPhone(phone);
    }

    /**
     * Find candidates whose name contains a keyword (case-insensitive).
     */
    @GetMapping("/search/keyword")
    public List<CandidateDto> findByFullNameContaining(@RequestParam String keyword) {
        return candidateFacade.findByFullNameContaining(keyword);
    }

    /**
     * Find candidates who have applied to jobs.
     */
    @GetMapping("/with-applications")
    public List<CandidateDto> findWithApplications() {
        return candidateFacade.findWithApplications();
    }

    /**
     * Find candidates with more than X applications.
     */
    @GetMapping("/with-more-than")
    public List<CandidateDto> findCandidatesWithMoreThan(@RequestParam int minApps) {
        return candidateFacade.findCandidatesWithMoreThan(minApps);
    }
}
