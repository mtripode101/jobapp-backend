package com.mtripode.jobapp.facade.facade;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.mtripode.jobapp.facade.dto.JobApplicationDto;

public interface JobApplicationFacade {

    // Aplicar a un trabajo
    JobApplicationDto applyToJob(JobApplicationDto dto);

    // Aplicar a un trabajo rechazado directamente
    JobApplicationDto applyRejected(JobApplicationDto dto);

    // Buscar aplicación por ID
    Optional<JobApplicationDto> findById(Long id);

    // Listar todas las aplicaciones
    List<JobApplicationDto> findAll();

    CompletableFuture<List<JobApplicationDto>> findAllAsync();

    // Eliminar aplicación por ID
    void deleteById(Long id);

    // Actualizar estado de una aplicación
    JobApplicationDto updateStatus(Long id, String newStatus);

    // Buscar aplicaciones por estado
    List<JobApplicationDto> findByStatus(String status);

    // Buscar aplicaciones por nombre de compañía
    List<JobApplicationDto> findByCompanyName(String companyName);

    // Buscar aplicaciones por nombre completo del candidato
    List<JobApplicationDto> findByCandidateFullName(String fullName);

    // Buscar aplicaciones por título de posición
    List<JobApplicationDto> findByPositionTitle(String title);

    // Buscar aplicación por Job ID
    JobApplicationDto findByJobId(String jobId);
}
