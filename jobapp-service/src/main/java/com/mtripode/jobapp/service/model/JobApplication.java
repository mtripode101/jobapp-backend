package com.mtripode.jobapp.service.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "job_application")
public class JobApplication extends BaseEntity {

    // Link to the job posting or source
    @Column(nullable = false)
    @NotBlank
    private String sourceLink;

    // Website where the job was found (optional, unique)
    @Column(unique = true, length = 1500)
    private String websiteSource;

    // Date when the application was submitted
    private LocalDate dateApplied;

    // Additional description or notes about the application
    private String description;

    // Candidate who submitted this application
    @ManyToOne(optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    // Company associated with this application
    @ManyToOne(optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    // Position applied for
    @ManyToOne(optional = false)
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    // Current status of the application
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    // Date when the application was rejected (if applicable)
    private LocalDate dateRejected;

    // Interviews linked to this application
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Interview> interviews = new ArrayList<>();

    // Job offers linked to this application
    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobOffer> offers = new ArrayList<>();

    // Constructors
    public JobApplication() {
    }

    public JobApplication(String sourceLink, String websiteSource, LocalDate dateApplied,
            String description, Candidate candidate, Company company,
            Position position, Status status) {
        this.sourceLink = sourceLink;
        this.websiteSource = websiteSource;
        this.dateApplied = dateApplied;
        this.description = description;
        this.candidate = candidate;
        this.company = company;
        this.position = position;
        this.status = status;
    }

    // Getters and setters
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

    public LocalDate getDateApplied() {
        return dateApplied;
    }

    public void setDateApplied(LocalDate dateApplied) {
        this.dateApplied = dateApplied;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getDateRejected() {
        return dateRejected;
    }

    public void setDateRejected(LocalDate dateRejected) {
        this.dateRejected = dateRejected;
    }

    public List<Interview> getInterviews() {
        return interviews;
    }

    public void setInterviews(List<Interview> interviews) {
        this.interviews = interviews;
    }

    public List<JobOffer> getOffers() {
        return offers;
    }

    public void setOffers(List<JobOffer> offers) {
        this.offers = offers;
    }

    public void addInterview(Interview interview) {
        interviews.add(interview);
        interview.setApplication(this);
    }

    public void removeInterview(Interview interview) {
        interviews.remove(interview);
        interview.setApplication(null);
    }

    public void addOffer(JobOffer offer) {
        offers.add(offer);
        offer.setApplication(this);
    }

    public void removeOffer(JobOffer offer) {
        offers.remove(offer);
        offer.setApplication(null);
    }

    // Business logic: mark application as rejected
    public void reject() {
        this.status = Status.REJECTED;
        this.dateRejected = LocalDate.now();
    }

    @Override
    public String toString() {
        return "JobApplication{id=" + getId()
                + ", sourceLink='" + sourceLink + '\''
                + ", websiteSource='" + websiteSource + '\''
                + ", dateApplied=" + dateApplied
                + ", candidate=" + (candidate != null ? candidate.getFullName() : "null")
                + ", company=" + (company != null ? company.getName() : "null")
                + ", position=" + (position != null ? position.getTitle() : "null")
                + ", status=" + status
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JobApplication)) {
            return false;
        }
        JobApplication that = (JobApplication) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
