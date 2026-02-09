package com.mtripode.jobapp.facade.facade.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mtripode.jobapp.facade.dto.JobApplicationDto;
import com.mtripode.jobapp.facade.facade.NoteFacade;
import com.mtripode.jobapp.facade.mapper.JobApplicationMapper;
import com.mtripode.jobapp.service.service.JobApplicationService;
import com.mtripode.jobapp.service.service.note.NoteClient;
import com.mtripode.jobapp.service.service.note.dto.Comment;
import com.mtripode.jobapp.service.service.note.dto.NoteDTO;

public class NoteFacadeImpl implements NoteFacade {

    private static final Logger log = LoggerFactory.getLogger(NoteFacadeImpl.class);

    private final NoteClient noteClient;

    private final JobApplicationService jobApplicationService;

    public NoteFacadeImpl(NoteClient noteClient, JobApplicationService jobApplicationService) {
        this.noteClient = noteClient;
        this.jobApplicationService = jobApplicationService;
    }

    @Override
    public NoteDTO createNoteForApplication(Long applicationId, String title, String content, List<Comment> comments) {
        Optional<JobApplicationDto> jobApplicationOptional = jobApplicationService.findById(applicationId).map(JobApplicationMapper::toDto);
        NoteDTO noteDto = null;
        if (jobApplicationOptional.isEmpty()) {
            return new NoteDTO();
        }
        try {
            noteDto = noteClient.createNoteForApplication(jobApplicationOptional.get().getId(),
                    "Application created",
                    "Job application applyToJob for jobId: " + jobApplicationOptional.get().getId(), comments);
        } catch (Exception e) {
            // Log the error but do not break the main flow
            log.warn("Failed to create note for application {}: {}", jobApplicationOptional.get().getId(), e.getMessage());
        }

        return noteDto;
    }

    @Override
    public List<NoteDTO> getNotesForApplication(Long applicationId) {
        Optional<JobApplicationDto> jobApplicationOptional = jobApplicationService.findById(applicationId).map(JobApplicationMapper::toDto);
        List<NoteDTO> notes = new ArrayList<>();
        if (jobApplicationOptional.isEmpty()) {
            return notes;
        }
        try {
            notes = noteClient.getNotesForApplication(jobApplicationOptional.get().getId());
            log.info("Notes for id "+notes.toString());
        } catch (Exception e) {  
            // Log the error but do not break the main flow
            log.warn("Failed to findById note for application {}: {}", jobApplicationOptional.get().getId(), e.getMessage());
        }

        return notes;
    }
}
