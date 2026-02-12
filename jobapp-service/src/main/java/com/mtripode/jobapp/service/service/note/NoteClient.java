package com.mtripode.jobapp.service.service.note;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.mtripode.jobapp.service.service.note.dto.Comment;
import com.mtripode.jobapp.service.service.note.dto.NoteDTO;

import reactor.core.publisher.Mono;

@Component
public class NoteClient {

    private final WebClient webClient;

    public NoteClient(WebClient.Builder builder,
            @Value("${note.service.base-url:http://localhost/notes:8082}") String baseUrl) {
        this.webClient = builder.baseUrl(baseUrl).build();
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

    public boolean deleteNotesForApplication(Long applicationId) {
        return webClient.delete()
                .uri("/application/{id}", applicationId)
                .exchangeToMono(response -> Mono.just(response.statusCode().is2xxSuccessful()))
                .block();
    }

}
