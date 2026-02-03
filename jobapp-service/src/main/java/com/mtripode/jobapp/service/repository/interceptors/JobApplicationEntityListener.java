package com.mtripode.jobapp.service.repository.interceptors;

import java.time.LocalDate;

import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.Status;
import com.mtripode.jobapp.service.validators.StatusTransitionValidator;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class JobApplicationEntityListener {

    @PrePersist
    public void beforePersist(JobApplication app) {
        // Solo setear dateApplied si no viene seteado (para no romper tests)
        if (app.getDateApplied() == null) {
            app.setDateApplied(LocalDate.now());
        }

        // Si el status es REJECTED y no tiene dateRejected, setearlo
        if (app.getStatus() == Status.REJECTED && app.getDateRejected() == null) {
            app.setDateRejected(LocalDate.now());
        }

    }

    @PreUpdate
    public void beforeUpdate(JobApplication app) {
        Status oldStatus = app.getPreviousStatus();
        Status newStatus = app.getStatus();

        if (!StatusTransitionValidator.canTransition(oldStatus, newStatus)) {
            throw new IllegalStateException("Invalid transition from " + oldStatus + " to " + newStatus);
        }

        if (newStatus == Status.REJECTED && app.getDateRejected() == null) {
            app.setDateRejected(LocalDate.now());
        }
    }
}
