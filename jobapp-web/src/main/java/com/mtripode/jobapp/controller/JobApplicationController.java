package com.mtripode.jobapp.controller;

import java.util.List;
import java.util.Optional;

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

import com.mtripode.jobapp.facade.dto.JobApplicationDto;
import com.mtripode.jobapp.facade.facade.JobApplicationFacade;
import com.mtripode.jobapp.facade.facade.impl.JobApplicationFacadeImpl;

@RestController
@RequestMapping("/applications")
@CrossOrigin(origins = "http://localhost:3000")
public class JobApplicationController {

    private final JobApplicationFacade jobApplicationFacade;

    public JobApplicationController(JobApplicationFacadeImpl jobApplicationFacade) {
        this.jobApplicationFacade = jobApplicationFacade;
    }

    @PostMapping
    public ResponseEntity<JobApplicationDto> createApplication(@RequestBody JobApplicationDto dto) {
        return ResponseEntity.ok(jobApplicationFacade.applyToJob(dto));
    }

    @PostMapping("/rejected")
    public ResponseEntity<JobApplicationDto> createRejectedApplication(@RequestBody JobApplicationDto dto) {
        return ResponseEntity.ok(jobApplicationFacade.applyRejected(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationDto> getApplicationById(@PathVariable Long id) {
        Optional<JobApplicationDto> application = jobApplicationFacade.findById(id);
        return application.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<JobApplicationDto>> getAllApplications() {
        return ResponseEntity.ok(jobApplicationFacade.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        jobApplicationFacade.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<JobApplicationDto> updateStatus(@PathVariable Long id, @RequestParam String newStatus) {
        return ResponseEntity.ok(jobApplicationFacade.updateStatus(id, newStatus));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobApplicationDto>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(jobApplicationFacade.findByStatus(status));
    }

    @GetMapping("/company")
    public ResponseEntity<List<JobApplicationDto>> getByCompanyName(@RequestParam String name) {
        return ResponseEntity.ok(jobApplicationFacade.findByCompanyName(name));
    }

    @GetMapping("/candidate")
    public ResponseEntity<List<JobApplicationDto>> getByCandidateFullName(@RequestParam String fullName) {
        return ResponseEntity.ok(jobApplicationFacade.findByCandidateFullName(fullName));
    }

    @GetMapping("/position")
    public ResponseEntity<List<JobApplicationDto>> getByPositionTitle(@RequestParam String title) {
        return ResponseEntity.ok(jobApplicationFacade.findByPositionTitle(title));
    }
}
