package com.mtripode.jobapp.service.service.note;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.mtripode.jobapp.service.service.note.dto.Comment;
import com.mtripode.jobapp.service.service.note.dto.NoteDTO;

@Component
public class NoteClient {

    private final WebClient webClient;

    public NoteClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://localhost:8082/notes").build();
    }

    public NoteDTO createNoteForApplication(Long applicationId, String title, String content, List<Comment> comments) {
        NoteDTO dto = new NoteDTO();
        dto.setApplicationId(applicationId);
        dto.setTitle(title);
        dto.setContent(content);
        dto.setComments(comments);
        
        return webClient.post()
                .uri("/{id}/notes", applicationId)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(NoteDTO.class)
                .block();
    }

    public List<NoteDTO> getNotesForApplication(Long applicationId) {
        return webClient.get()
                .uri("/{id}/notes", applicationId)
                .retrieve()
                .bodyToFlux(NoteDTO.class)
                .collectList()
                .block();
    }
    
}