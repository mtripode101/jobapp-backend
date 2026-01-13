package com.mtripode.jobapp.service.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "company")
public class Company extends BaseEntity {

    @Column(nullable = false, unique = true)
    @NotBlank
    private String name;

    @Column(nullable = true) // website opcional
    private String website;

    @Column(nullable = true, length = 1000) // descripción opcional, hasta 1000 caracteres
    private String description;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<JobApplication> applications = new ArrayList<>();

    // Constructores
    public Company() {
    }

    public Company(String name, String website, String description) {
        this.name = name;
        this.website = website;
        this.description = description;
    }

    // Getters y setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<JobApplication> getApplications() {
        return applications;
    }

    public void setApplications(List<JobApplication> applications) {
        this.applications = applications;
    }

    // Helpers para relación bidireccional
    public void addApplication(JobApplication application) {
        applications.add(application);
        application.setCompany(this);
    }

    public void removeApplication(JobApplication application) {
        applications.remove(application);
        application.setCompany(null);
    }

    // equals y hashCode basados en id heredado
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Company)) {
            return false;
        }
        Company company = (Company) o;
        return Objects.equals(getId(), company.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    // toString para depuración
    @Override
    public String toString() {
        return "Company{id=" + getId() + ", name='" + name + "', website='" + website + "', description='" + description + "'}";
    }
}
