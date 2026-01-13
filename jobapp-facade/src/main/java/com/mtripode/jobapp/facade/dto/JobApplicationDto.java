package com.mtripode.jobapp.facade.dto;

import java.time.LocalDateTime;

public class JobApplicationDto extends BaseDto {

    private String sourceLink;
    private String websiteSource;
    private String description;
    private CandidateDto candidate;
    private CompanyDto company;
    private PositionDto position;
    private String status; // APPLIED, REJECTED, INTERVIEW, OFFERED

    public JobApplicationDto() {
    }

    public JobApplicationDto(Long id, LocalDateTime createdAt, LocalDateTime updatedAt,
            String sourceLink, String websiteSource, String description,
            CandidateDto candidate, CompanyDto company, PositionDto position,
            String status) {
        this.setId(id);
        this.setCreatedAt(createdAt);
        this.setUpdatedAt(updatedAt);
        this.sourceLink = sourceLink;
        this.websiteSource = websiteSource;
        this.description = description;
        this.candidate = candidate;
        this.company = company;
        this.position = position;
        this.status = status;
    }

    // Getters y setters
    public String getSourceLink() {
        return sourceLink;
    }

    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }

    public String getWebsiteSource() {
        return websiteSource;
    }

    public void setWebsiteSource(String websiteSource) {
        this.websiteSource = websiteSource;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CandidateDto getCandidate() {
        return candidate;
    }

    public void setCandidate(CandidateDto candidate) {
        this.candidate = candidate;
    }

    public CompanyDto getCompany() {
        return company;
    }

    public void setCompany(CompanyDto company) {
        this.company = company;
    }

    public PositionDto getPosition() {
        return position;
    }

    public void setPosition(PositionDto position) {
        this.position = position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
