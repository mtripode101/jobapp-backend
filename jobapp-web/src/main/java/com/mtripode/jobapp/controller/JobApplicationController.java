package com.mtripode.jobapp.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.mtripode.jobapp.facade.facade.NoteFacade;
import com.mtripode.jobapp.facade.facade.impl.JobApplicationFacadeImpl;
import com.mtripode.jobapp.service.service.note.dto.Comment;
import com.mtripode.jobapp.service.service.note.dto.NoteDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/applications")
@CrossOrigin(origins = "http://localhost:3000")
public class JobApplicationController {

    private static final Logger log = LoggerFactory.getLogger(JobApplicationController.class);

    private final JobApplicationFacade jobApplicationFacade;

    private final NoteFacade noteFacade;

    public JobApplicationController(JobApplicationFacadeImpl jobApplicationFacade, NoteFacade noteFacade) {
        this.jobApplicationFacade = jobApplicationFacade;
        this.noteFacade = noteFacade;
    }

    @PostMapping
    public ResponseEntity<Object> createApplication(@Valid @RequestBody JobApplicationDto dto) {
        if (jobApplicationFacade.findByJobId(dto.getJobId()) != null) {
            ErrorResponse error = new ErrorResponse("Application already exists", HttpStatus.CONFLICT.value());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        JobApplicationDto created = jobApplicationFacade.applyToJob(dto);

        if (Objects.nonNull(created)) {
            String title = "JobApplication created for id " + created.getId();
            String content = "This is the content for the title " + title;
            List<Comment> comments = new ArrayList<>();
            NoteDTO noteDto = this.noteFacade.createNoteForApplication(created.getId(), title, content, comments);
            if (Objects.nonNull(noteDto)) {
                log.info("Created note for application: {}", noteDto.toString());
                created.setNote(noteDto);
            }
        }

        return ResponseEntity
                .created(URI.create("/applications/" + created.getId()))
                .body(created);
    }

    @PostMapping("/rejected")
    public ResponseEntity<JobApplicationDto> createRejectedApplication(@RequestBody JobApplicationDto dto) {
        JobApplicationDto applyRejected = jobApplicationFacade.applyRejected(dto);
        if (Objects.nonNull(applyRejected)) {
            String title = "JobApplication rejected for id " + applyRejected.getId();
            String content = "This is the content for the title " + title;
            List<Comment> comments = new ArrayList<>();
            Comment comment = new Comment();
            comment.setAuthor(applyRejected.getCandidate().getFullName());
            comment.setMessage("Jobapplication Rejected");
            comments.add(comment);
            NoteDTO noteDto = this.noteFacade.createNoteForApplication(applyRejected.getId(), title, content, comments);
            if (Objects.nonNull(dto)) {
                log.info("Created note for rejected application: {}", noteDto.toString());
                applyRejected.setNote(noteDto);
            }
        }
        return ResponseEntity.ok(applyRejected);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody JobApplicationDto dto) {
        JobApplicationDto update = this.jobApplicationFacade.update(id, dto);
        if (Objects.nonNull(update) && Objects.isNull(update.getError())) {
            String title = "JobApplication updated for id " + update.getId();
            String content = "This is the content for the title " + title;
            List<Comment> comments = new ArrayList<>();
            Comment comment = new Comment();
            comment.setAuthor(update.getCandidate().getFullName());
            comment.setMessage("Jobapplication Udpated");
            comments.add(comment);
            NoteDTO noteDto = this.noteFacade.createNoteForApplication(update.getId(), title, content, comments);
            if (Objects.nonNull(noteDto)) {
                log.info("Created note for update application: {}", noteDto.toString());
                update.setNote(noteDto);
            }
        } else {
            StringBuilder errorMessage = new StringBuilder();
            if (Objects.nonNull(update) && Objects.nonNull(update.getError())) {
                errorMessage.append(update.getError().getMessage());

            } else {
                errorMessage.append("JobApplication with id " + id + " not found or could not be updated.");

            }

            ErrorResponse error = new ErrorResponse(errorMessage.toString(), HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(update);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationDto> getApplicationById(@PathVariable Long id) {
        Optional<JobApplicationDto> application = jobApplicationFacade.findById(id);
        if (application.isPresent()) {
            List<NoteDTO> notesDto = this.noteFacade.getNotesForApplication(application.get().getId());
            notesDto.stream().forEach(note -> System.err.println("note " + note.toString()));
        }
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
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id) {
        Optional<JobApplicationDto> applicationOptional = jobApplicationFacade.findById(id);

        if (applicationOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Long applicationId = applicationOptional.get().getId();

        try {
            boolean notesDeleted = this.noteFacade.deleteNotesForApplication(applicationId);
            if (!notesDeleted) {
                log.info("No notes deleted for application {}", applicationId);
            }
        } catch (Exception e) {
            log.warn("Failed to delete notes for application {}: {}", applicationId, e.getMessage());
        }

        jobApplicationFacade.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam String newStatus
    ) {
        JobApplicationDto updated = jobApplicationFacade.updateStatus(id, newStatus);
        if (Objects.isNull(updated)) {
            String errorMessage = "Invalid status transition for application with id " + id + " to status " + newStatus;
            log.error(errorMessage);
            ErrorResponse error = new ErrorResponse(errorMessage, HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
        return ResponseEntity.ok(updated);
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
