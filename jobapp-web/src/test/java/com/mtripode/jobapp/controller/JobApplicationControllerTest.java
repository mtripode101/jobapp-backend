package com.mtripode.jobapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mtripode.jobapp.facade.dto.ErrorResponse;
import com.mtripode.jobapp.facade.dto.JobApplicationDto;
import com.mtripode.jobapp.facade.facade.NoteFacade;
import com.mtripode.jobapp.facade.facade.impl.JobApplicationFacadeImpl;
import com.mtripode.jobapp.service.service.note.dto.Comment;
import com.mtripode.jobapp.service.service.note.dto.NoteDTO;

@ExtendWith(MockitoExtension.class)
class JobApplicationControllerTest {

    @Mock
    private NoteFacade noteFacade;

    private StubJobApplicationFacade facade;
    private JobApplicationController controller;

    @BeforeEach
    void setUp() {
        facade = new StubJobApplicationFacade();
        controller = new JobApplicationController(facade, noteFacade);
    }

    @Test
    void createApplicationShouldReturnConflictWhenJobIdExists() {
        JobApplicationDto input = application(1L, "JOB-1");
        facade.byJobId = input;

        ResponseEntity<Object> response = controller.createApplication(input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
    }

    @Test
    void createApplicationShouldCreateNotesAndReturnCreated() {
        JobApplicationDto input = application(null, "JOB-2");
        JobApplicationDto created = application(2L, "JOB-2");
        facade.created = created;
        NoteDTO createdNote = note("N1");
        when(noteFacade.createNoteForApplication(eq(2L), any(), any(), any())).thenReturn(createdNote);

        ResponseEntity<Object> response = controller.createApplication(input);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        JobApplicationDto body = (JobApplicationDto) response.getBody();
        assertThat(body.getNotes()).hasSize(1);
        assertThat(body.getNotes().get(0).getId()).isEqualTo("N1");
    }

    @Test
    void createRejectedApplicationShouldReturnOkAndAttachNote() {
        JobApplicationDto rejected = application(3L, "JOB-3");
        rejected.setCandidate(new com.mtripode.jobapp.facade.dto.CandidateDto());
        rejected.getCandidate().setFullName("Martin");
        facade.rejected = rejected;
        when(noteFacade.createNoteForApplication(eq(3L), any(), any(), any())).thenReturn(note("N2"));

        ResponseEntity<JobApplicationDto> response = controller.createRejectedApplication(application(null, "JOB-3"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNotes()).hasSize(1);
    }

    @Test
    void updateShouldReturnNotFoundWhenFacadeReturnsError() {
        JobApplicationDto errorDto = new JobApplicationDto();
        errorDto.setError(new ErrorResponse("invalid", 400));
        facade.updated = errorDto;

        ResponseEntity<?> response = controller.update(5L, new JobApplicationDto());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
    }

    @Test
    void updateShouldCreateExtraNotesForIncomingNotesWithoutId() {
        JobApplicationDto updated = application(6L, "JOB-6");
        facade.updated = updated;

        JobApplicationDto incoming = new JobApplicationDto();
        NoteDTO incomingNote = new NoteDTO();
        incomingNote.setTitle("new");
        incomingNote.setContent("content");
        Comment c = new Comment();
        c.setAuthor("a");
        c.setMessage("m");
        incomingNote.setComments(List.of(c));
        incoming.setNotes(List.of(incomingNote));

        when(noteFacade.getNotesForApplication(6L)).thenReturn(List.of());
        when(noteFacade.createNoteForApplication(eq(6L), eq("new"), eq("content"), any())).thenReturn(note("N3"));

        ResponseEntity<?> response = controller.update(6L, incoming);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        JobApplicationDto body = (JobApplicationDto) response.getBody();
        assertThat(body.getNotes()).hasSize(1);
        assertThat(body.getNotes().get(0).getId()).isEqualTo("N3");
    }

    @Test
    void shouldHandleGetByIdAllPagedAsyncAndDelete() {
        JobApplicationDto dto = application(7L, "JOB-7");
        facade.byId = Optional.of(dto);
        facade.all = List.of(dto);
        Page<JobApplicationDto> page = new PageImpl<>(List.of(dto));
        facade.page = page;
        facade.async = CompletableFuture.completedFuture(List.of(dto));
        when(noteFacade.getNotesForApplication(7L)).thenReturn(List.of(note("N4")));
        when(noteFacade.deleteNotesForApplication(7L)).thenReturn(true);

        assertThat(controller.getApplicationById(7L).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(controller.getAllApplications().getBody()).hasSize(1);
        assertThat(controller.getAllApplicationsPaged(0, 5, "id").getBody().getTotalElements()).isEqualTo(1);
        assertThat(controller.getAllApplicationsAsync().join().getBody()).hasSize(1);
        assertThat(controller.deleteApplication(7L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        facade.byId = Optional.empty();
        assertThat(controller.deleteApplication(999L).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        facade.byId = Optional.of(dto);
        doThrow(new RuntimeException("notes error")).when(noteFacade).deleteNotesForApplication(7L);
        assertThat(controller.deleteApplication(7L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldHandleUpdateStatusAndFilterEndpoints() {
        JobApplicationDto dto = application(8L, "JOB-8");
        facade.statusUpdated = dto;
        facade.byStatus = List.of(dto);
        facade.byCompany = List.of(dto);
        facade.byCandidate = List.of(dto);
        facade.byPosition = List.of(dto);
        facade.byJobId = dto;

        assertThat(controller.updateStatus(8L, "APPLIED").getStatusCode()).isEqualTo(HttpStatus.OK);

        facade.statusUpdated = null;
        assertThat(controller.updateStatus(8L, "HIRED").getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        assertThat(controller.getByStatus("APPLIED").getBody()).hasSize(1);
        assertThat(controller.getByCompanyName("Acme").getBody()).hasSize(1);
        assertThat(controller.getByCandidateFullName("John").getBody()).hasSize(1);
        assertThat(controller.getByPositionTitle("Eng").getBody()).hasSize(1);
        assertThat(controller.getByJobId("JOB-8").getStatusCode()).isEqualTo(HttpStatus.OK);

        facade.byJobId = null;
        assertThat(controller.getByJobId("NONE").getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private static JobApplicationDto application(Long id, String jobId) {
        JobApplicationDto dto = new JobApplicationDto();
        dto.setId(id);
        dto.setJobId(jobId);
        return dto;
    }

    private static NoteDTO note(String id) {
        NoteDTO note = new NoteDTO();
        note.setId(id);
        note.setComments(new ArrayList<>());
        return note;
    }

    static class StubJobApplicationFacade extends JobApplicationFacadeImpl {

        JobApplicationDto created;
        JobApplicationDto rejected;
        JobApplicationDto updated;
        Optional<JobApplicationDto> byId = Optional.empty();
        List<JobApplicationDto> all = List.of();
        CompletableFuture<List<JobApplicationDto>> async = CompletableFuture.completedFuture(List.of());
        JobApplicationDto statusUpdated;
        List<JobApplicationDto> byStatus = List.of();
        List<JobApplicationDto> byCompany = List.of();
        List<JobApplicationDto> byCandidate = List.of();
        List<JobApplicationDto> byPosition = List.of();
        JobApplicationDto byJobId;
        Page<JobApplicationDto> page = Page.empty();

        StubJobApplicationFacade() {
            super(null);
        }

        @Override
        public JobApplicationDto applyToJob(JobApplicationDto dto) {
            return created;
        }

        @Override
        public JobApplicationDto applyRejected(JobApplicationDto dto) {
            return rejected;
        }

        @Override
        public JobApplicationDto update(Long id, JobApplicationDto dto) {
            return updated;
        }

        @Override
        public Optional<JobApplicationDto> findById(Long id) {
            return byId;
        }

        @Override
        public List<JobApplicationDto> findAll() {
            return all;
        }

        @Override
        public CompletableFuture<List<JobApplicationDto>> findAllAsync() {
            return async;
        }

        @Override
        public void deleteById(Long id) {
        }

        @Override
        public JobApplicationDto updateStatus(Long id, String newStatus) {
            return statusUpdated;
        }

        @Override
        public List<JobApplicationDto> findByStatus(String status) {
            return byStatus;
        }

        @Override
        public List<JobApplicationDto> findByCompanyName(String companyName) {
            return byCompany;
        }

        @Override
        public List<JobApplicationDto> findByCandidateFullName(String fullName) {
            return byCandidate;
        }

        @Override
        public List<JobApplicationDto> findByPositionTitle(String title) {
            return byPosition;
        }

        @Override
        public JobApplicationDto findByJobId(String jobId) {
            return byJobId;
        }

        @Override
        public Page<JobApplicationDto> findAll(Pageable pageable) {
            return page;
        }
    }
}

