package com.mtripode.jobapp.facade.facade;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.mtripode.jobapp.facade.dto.JobOfferDTO;
import com.mtripode.jobapp.service.model.JobOfferStatus;

public interface JobOfferFacade {

    // Obtener todas las ofertas
    List<JobOfferDTO> getAllOffers();

    // Obtener oferta por ID
    Optional<JobOfferDTO> getOfferById(Long id);

    // Crear nueva oferta
    JobOfferDTO createOffer(Long applicationId, LocalDate offeredAt, JobOfferStatus status);

    // Actualizar oferta existente
    JobOfferDTO updateOffer(JobOfferDTO dto);

    // Eliminar oferta por ID
    void deleteOffer(Long id);

    // Obtener ofertas por estado
    List<JobOfferDTO> getOffersByStatus(JobOfferStatus status);

    // Aceptar oferta
    JobOfferDTO acceptOffer(Long id);

    // Rechazar oferta
    JobOfferDTO rejectOffer(Long id);
}
