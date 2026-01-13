package com.mtripode.jobapp.service.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(
        name = "candidate",
        indexes = {
            @Index(name = "idx_candidate_email", columnList = "email")
        }
)
public class Candidate extends BaseEntity {

    @NotBlank
    private String fullName;

    @Embedded
    private ContactInfo contactInfo;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobApplication> applications = new ArrayList<>();

    // --- Constructors ---
    public Candidate() {
    }

    public Candidate(String fullName, ContactInfo contactInfo) {
        this.fullName = fullName;
        this.contactInfo = contactInfo;
    }

    // --- Helpers for bidirectional relationship ---
    public void addApplication(JobApplication application) {
        applications.add(application);
        application.setCandidate(this);
    }

    public void removeApplication(JobApplication application) {
        applications.remove(application);
        application.setCandidate(null);
    }

    // --- Getters & Setters ---
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public ContactInfo getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(ContactInfo contactInfo) {
        this.contactInfo = contactInfo;
    }

    public List<JobApplication> getApplications() {
        return applications;
    }

    public void setApplications(List<JobApplication> applications) {
        this.applications = applications;
    }

    @Override
    public String toString() {
        return "Candidate{id=" + getId() + ", fullName='" + fullName
                + "', contactInfo=" + contactInfo + "}";
    }
}
