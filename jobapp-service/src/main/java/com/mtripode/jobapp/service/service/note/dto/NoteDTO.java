package com.mtripode.jobapp.service.service.note.dto;

import java.util.List;
import java.util.stream.Collectors;

public class NoteDTO {

    private String id;
    private String title;
    private String content;
    private List<Comment> comments;
    private Long applicationId;

    public NoteDTO() {
    }

    public NoteDTO(String id, String title, String content, List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.comments = comments;
    }

    // Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    public String toString() {
        List<String> commentstStrings = this.comments.stream().map(Comment::toString).collect(Collectors.toList());
        return "Note id " + this.id + " title " + this.title + " content " + this.content + " applicationID " + this.applicationId+" comments "+commentstStrings;
    }

}
