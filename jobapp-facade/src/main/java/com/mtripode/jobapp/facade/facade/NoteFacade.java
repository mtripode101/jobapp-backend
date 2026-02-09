package com.mtripode.jobapp.facade.facade;

import java.util.List;

import com.mtripode.jobapp.service.service.note.dto.Comment;
import com.mtripode.jobapp.service.service.note.dto.NoteDTO;

public interface NoteFacade {

    NoteDTO createNoteForApplication (Long applicationId, String title, String content, List<Comment> comments);

    List<NoteDTO> getNotesForApplication(Long applicationId);
    
}
