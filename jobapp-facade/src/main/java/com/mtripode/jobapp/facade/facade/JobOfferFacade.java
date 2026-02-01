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

    List<JobOfferDTO> findByExpectedSalaryGreaterThan(Double salary);

    List<JobOfferDTO> findByOfferedSalaryLessThan(Double salary);

    List<JobOfferDTO> findByExpectedSalaryBetween(Double minSalary, Double maxSalary);

    List<JobOfferDTO> findByOfferedSalaryBetween(Double minSalary, Double maxSalary);

    List<JobOfferDTO> findByExpectedSalaryIsNull();

    List<JobOfferDTO> findByOfferedSalaryIsNull();

    List<JobOfferDTO> findByExpectedSalaryIsNotNull();

    List<JobOfferDTO> findByOfferedSalaryIsNotNull();

    List<JobOfferDTO> findByExpectedSalaryGreaterThanAndOfferedSalaryLessThan(Double expectedMin, Double offeredMax);

    List<JobOfferDTO> findByExpectedSalaryLessThanAndOfferedSalaryGreaterThan(Double expectedMax, Double offeredMin);

    List<JobOfferDTO> findByExpectedSalaryEqualsOfferedSalary();


}
