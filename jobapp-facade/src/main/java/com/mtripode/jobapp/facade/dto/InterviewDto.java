package com.mtripode.jobapp.facade.dto;

import java.time.LocalDateTime;

import com.mtripode.jobapp.service.model.InterviewType;

/**
 * DTO for creating, updating, and returning Interview objects.
 */
public class InterviewDto extends BaseDto {

    private LocalDateTime scheduledAt;
    private InterviewType type;
    private String feedback;
    private Long applicationId;

    public InterviewDto() {
        this.scheduledAt = null;
        this.type = null;
    }

    public InterviewDto(LocalDateTime scheduledAt, InterviewType type, String feedback) {
        this.scheduledAt = scheduledAt;
        this.type = type;
        this.feedback = feedback;
    }
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
    @Override
    public String toString() {
        return "InterviewDto [scheduledAt=" + scheduledAt + ", type=" + type + ", feedback=" + feedback + "]";
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
    
    
}
