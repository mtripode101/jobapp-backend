package com.mtripode.jobapp.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
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

import com.mtripode.jobapp.facade.dto.ErrorResponse;
import com.mtripode.jobapp.facade.dto.JobApplicationDto;
import com.mtripode.jobapp.facade.facade.JobApplicationFacade;
import com.mtripode.jobapp.facade.facade.impl.JobApplicationFacadeImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/applications")
@CrossOrigin(origins = "http://localhost:3000")
public class JobApplicationController {

    private final JobApplicationFacade jobApplicationFacade;

    public JobApplicationController(JobApplicationFacadeImpl jobApplicationFacade) {
        this.jobApplicationFacade = jobApplicationFacade;
    }

    @PostMapping
    public ResponseEntity<Object> createApplication(@Valid @RequestBody JobApplicationDto dto) {
        if (jobApplicationFacade.findByJobId(dto.getJobId()) != null) {
            ErrorResponse error = new ErrorResponse("Application already exists", HttpStatus.CONFLICT.value());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        JobApplicationDto created = jobApplicationFacade.applyToJob(dto);
        return ResponseEntity
                .created(URI.create("/applications/" + created.getId()))
                .body(created);
    }

    @PostMapping("/rejected")
    public ResponseEntity<JobApplicationDto> createRejectedApplication(@RequestBody JobApplicationDto dto) {
        return ResponseEntity.ok(jobApplicationFacade.applyRejected(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationDto> update(@PathVariable Long id, @RequestBody JobApplicationDto dto) {
        return ResponseEntity.ok(this.jobApplicationFacade.update(id, dto));
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

    @GetMapping("/paged")
    public ResponseEntity<Page<JobApplicationDto>> getAllApplicationsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(jobApplicationFacade.findAll(pageable));
    }

    @GetMapping("/findAll/async")
    public CompletableFuture<ResponseEntity<List<JobApplicationDto>>> getAllApplicationsAsync() {
        // If you implemented facade.findAllAsync()
        return jobApplicationFacade.findAllAsync()
                .thenApply(ResponseEntity::ok)
                .exceptionally(ex -> {
                    // log if needed
                    return ResponseEntity.status(500).build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id
    ) {
        jobApplicationFacade.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<JobApplicationDto> updateStatus(@PathVariable Long id, @RequestParam String newStatus
    ) {
        return ResponseEntity.ok(jobApplicationFacade.updateStatus(id, newStatus));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobApplicationDto>> getByStatus(@PathVariable String status
    ) {
        return ResponseEntity.ok(jobApplicationFacade.findByStatus(status));
    }

    @GetMapping("/company")
    public ResponseEntity<List<JobApplicationDto>> getByCompanyName(@RequestParam String name
    ) {
        return ResponseEntity.ok(jobApplicationFacade.findByCompanyName(name));
    }

    @GetMapping("/candidate")
    public ResponseEntity<List<JobApplicationDto>> getByCandidateFullName(@RequestParam String fullName
    ) {
        return ResponseEntity.ok(jobApplicationFacade.findByCandidateFullName(fullName));
    }

    @GetMapping("/position")
    public ResponseEntity<List<JobApplicationDto>> getByPositionTitle(@RequestParam String title
    ) {
        return ResponseEntity.ok(jobApplicationFacade.findByPositionTitle(title));
    }

    @GetMapping("/jobId/{jobId}")
    public ResponseEntity<JobApplicationDto> getByJobId(@PathVariable String jobId
    ) {
        JobApplicationDto dto = jobApplicationFacade.findByJobId(jobId);
        if (dto != null) {
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
