package com.mtripode.jobapp.service.model;

public enum InterviewType {
    ONLINE("Online"),
    ONSITE("Onsite"),
    PHONE("Phone"),
    VIDEO("Video"),
    TECHNICAL("Technical"),
    HR("HR");

    private final String displayName;

    InterviewType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}