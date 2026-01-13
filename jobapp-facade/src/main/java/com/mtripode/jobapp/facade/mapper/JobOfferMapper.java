package com.mtripode.jobapp.facade.mapper;

import com.mtripode.jobapp.facade.dto.JobOfferDTO;
import com.mtripode.jobapp.service.model.JobOffer;

public class JobOfferMapper {

    public static JobOfferDTO toDTO(JobOffer offer) {
        JobOfferDTO dto = new JobOfferDTO();
        dto.setId(offer.getId());
        dto.setOfferedAt(offer.getOfferedAt());
        dto.setStatus(offer.getStatus());
        dto.setApplicationId(offer.getApplication().getId());
        return dto;
    }

    public static JobOffer toEntity(JobOfferDTO dto) {
        JobOffer offer = new JobOffer();
        offer.setId(dto.getId());
        offer.setOfferedAt(dto.getOfferedAt());
        offer.setStatus(dto.getStatus());
        // La relación con JobApplication se resuelve en el service
        return offer;
    }
}
