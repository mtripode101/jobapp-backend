package com.mtripode.jobapp.facade.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.mtripode.jobapp.service.model.JobOfferStatus;


public class JobOfferDTO extends BaseDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private LocalDate offeredAt;
    private JobOfferStatus status;
    private Long applicationId;

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

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
}
