package com.mtripode.jobapp.service.model;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "interview")
public class Interview extends BaseEntity {

    @NotNull
    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private InterviewType type;

    @Column(length = 1000)
    private String feedback;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private JobApplication application;

    // Constructors
    public Interview() {
    }

    public Interview(LocalDateTime scheduledAt, InterviewType type, String feedback, JobApplication application) {
        this.scheduledAt = scheduledAt;
        this.type = type;
        this.feedback = feedback;
        this.application = application;
    }

    public Interview(@NotNull LocalDateTime scheduledAt, InterviewType type, String feedback) {
        this.scheduledAt = scheduledAt;
        this.type = type;
        this.feedback = feedback;
    }

    // Getters and setters
    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public InterviewType getType() {
        return type;
    }

    public void setType(InterviewType type) {
        this.type = type;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public JobApplication getApplication() {
        return application;
    }

    public void setApplication(JobApplication application) {
        this.application = application;
    }

    @Override
    public String toString() {
        return "Interview{id=" + getId() + ", type=" + type + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Interview)) {
            return false;
        }
        Interview interview = (Interview) o;
        return Objects.equals(getId(), interview.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
