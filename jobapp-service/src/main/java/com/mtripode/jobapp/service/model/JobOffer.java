package com.mtripode.jobapp.service.model;

import java.time.LocalDate;
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
@Table(name = "job_offer")
public class JobOffer extends BaseEntity {

    @Column(nullable = false)
    @NotNull
    private LocalDate offeredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobOfferStatus status; // PENDING, ACCEPTED, REJECTED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private JobApplication application;

    private Double expectedSalary;

    private Double offeredSalary;

    // Constructors
    public JobOffer() {
    }

    public JobOffer(LocalDate offeredAt, JobOfferStatus status, JobApplication application) {
        this.offeredAt = offeredAt;
        this.status = status;
        this.application = application;
        this.expectedSalary = 0.0;
        this.offeredSalary = 0.0;
    }

    // Getters and setters

    public Double getExpectedSalary() {
        return expectedSalary;
    }

    public void setExpectedSalary(Double excpectedSalary) {
        this.expectedSalary = excpectedSalary;
    }

    public Double getOfferedSalary() {
        return offeredSalary;
    }

    public void setOfferedSalary(Double offeredSalary) {
        this.offeredSalary = offeredSalary;
    }
    
    public LocalDate getOfferedAt() {
        return offeredAt;
    }

    public void setOfferedAt(LocalDate offeredAt) {
        this.offeredAt = offeredAt;
    }

    public JobOfferStatus getStatus() {
        return status;
    }

    public void setStatus(JobOfferStatus status) {
        this.status = status;
    }

    public JobApplication getApplication() {
        return application;
    }

    public void setApplication(JobApplication application) {
        this.application = application;
    }

    @Override
    public String toString() {
        return "JobOffer{id=" + getId() + ", status=" + status + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JobOffer)) {
            return false;
        }
        JobOffer jobOffer = (JobOffer) o;
        return Objects.equals(getId(), jobOffer.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
