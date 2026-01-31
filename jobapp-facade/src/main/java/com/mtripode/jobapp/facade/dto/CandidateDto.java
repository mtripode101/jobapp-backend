package com.mtripode.jobapp.facade.dto;

import java.io.Serializable;

/**
 * DTO for creating, updating, and returning Candidate objects.
 */
public class CandidateDto extends BaseDto implements Serializable {

    private String fullName;
    private String email;
    private String phone;
    private String linkedIn;
    private String github;
    private static final long serialVersionUID = 1L;

    // --- Constructors ---
    public CandidateDto() {
    }

    public CandidateDto(Long id, String fullName, String email, String phone, String linkedIn, String github) {
        this.setId(id); // inherited from BaseDto
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.linkedIn = linkedIn;
        this.github = github;
    }

    // --- Getters and Setters ---
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLinkedIn() {
        return linkedIn;
    }

    public void setLinkedIn(String linkedIn) {
        this.linkedIn = linkedIn;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    // --- toString ---
    @Override
    public String toString() {
        return "CandidateDto{" +
                "id=" + getId() + // from BaseDto
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", linkedIn='" + linkedIn + '\'' +
                ", github='" + github + '\'' +
                ", createdAt=" + getCreatedAt() + // from BaseDto
                ", updatedAt=" + getUpdatedAt() + // from BaseDto
                '}';
    }
}