package com.mtripode.jobapp.service.model;

public enum Status {
    APPLIED("Applied"),
    INTERVIEW_SCHEDULED("Interview Scheduled"),
    INTERVIEWED("Interviewed"),
    OFFERED("Offered"),
    HIRED("Hired"),
    REJECTED("Rejected");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}