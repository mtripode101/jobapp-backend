package com.mtripode.jobapp.facade.facade.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mtripode.jobapp.facade.dto.InterviewDto;
import com.mtripode.jobapp.facade.facade.InterviewFacade;
import com.mtripode.jobapp.facade.mapper.InterviewMapper;
import com.mtripode.jobapp.service.model.Interview;
import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.service.InterviewService;
import com.mtripode.jobapp.service.service.JobApplicationService;

@Component
public class InterviewFacadeImpl implements InterviewFacade {

    private static final Logger logger = LoggerFactory.getLogger(InterviewFacadeImpl.class);

    private final InterviewService interviewService;
    private final InterviewMapper interviewMapper;
    private final JobApplicationService jobApplicationService;

    public InterviewFacadeImpl(InterviewService interviewService,
                               InterviewMapper interviewMapper,
                               JobApplicationService jobApplicationService) {
        this.interviewService = interviewService;
        this.interviewMapper = interviewMapper;
        this.jobApplicationService = jobApplicationService;
    }

    @Override
    public List<InterviewDto> getAllInterviews() {
        logger.debug("getAllInterviews - inicio");
        List<Interview> entities = interviewService.findAll();
        logger.debug("getAllInterviews - {} entrevistas recuperadas desde el servicio", entities.size());
        List<InterviewDto> dtos = entities.stream()
                .map(interviewMapper::toDto)
                .collect(Collectors.toList());
        logger.debug("getAllInterviews - mapeo a DTO completado");
        return dtos;
    }

    @Override
    public Optional<InterviewDto> getInterviewById(Long id) {
        logger.debug("getInterviewById - inicio. id={}", id);
        Optional<InterviewDto> result = interviewService.findById(id)
                .map(interviewMapper::toDto);
        if (result.isPresent()) {
            logger.debug("getInterviewById - entrevista encontrada id={}", id);
        } else {
            logger.debug("getInterviewById - no encontrada id={}", id);
        }
        return result;
    }

    @Override
    public void deleteInterview(Long id) {
        logger.debug("deleteInterview - inicio. id={}", id);
        try {
            interviewService.deleteById(id);
            logger.info("deleteInterview - eliminado correctamente id={}", id);
        } catch (Exception ex) {
            logger.error("deleteInterview - error al eliminar id={}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    @Transactional
    public InterviewDto saveInterview(InterviewDto interviewDto) {
        logger.debug("saveInterview - inicio. interviewDto={}", interviewDto);

        if (interviewDto == null) {
            logger.error("saveInterview - interviewDto es null");
            throw new IllegalArgumentException("InterviewDto is required");
        }

        Long applicationId = interviewDto.getApplicationId();
        if (applicationId == null) {
            logger.error("saveInterview - applicationId es null en interviewDto={}", interviewDto);
            throw new IllegalArgumentException("applicationId is required");
        }

        logger.debug("saveInterview - buscando JobApplication id={}", applicationId);
        JobApplication application;
        try {
            Optional<JobApplication> optApp = jobApplicationService.findById(applicationId);
            if (!optApp.isPresent()) {
                logger.warn("saveInterview - JobApplication no encontrada id={}", applicationId);
                throw new NoSuchElementException("JobApplication not found: id=" + applicationId);
            }
            application = optApp.get();
            logger.debug("saveInterview - JobApplication encontrada id={}, candidateId={}", application.getId(),
                    application.getCandidate() != null ? application.getCandidate().getId() : "null");
        } catch (NoSuchElementException nse) {
            logger.error("saveInterview - JobApplication no encontrada id={}: {}", applicationId, nse.getMessage());
            throw nse;
        } catch (Exception ex) {
            logger.error("saveInterview - excepción al buscar JobApplication id={}: {}", applicationId, ex.getMessage(), ex);
            throw new RuntimeException("Error retrieving JobApplication", ex);
        }

        // Mapear DTO a entidad (mapper no resuelve la relación)
        logger.debug("saveInterview - mapeando DTO a entidad (sin relación)");
        Interview interviewEntity = interviewMapper.toEntity(interviewDto);
        logger.debug("saveInterview - entidad mapeada: {}", interviewEntity);

        // Asignar la entidad gestionada antes de persistir
        logger.debug("saveInterview - asignando JobApplication (id={}) a la entidad Interview", application.getId());
        interviewEntity.setApplication(application);

        // Persistir
        Interview saved;
        try {
            logger.debug("saveInterview - guardando entidad Interview en el servicio");
            saved = interviewService.saveInterview(interviewEntity);
            logger.info("saveInterview - guardado exitoso id={}", saved.getId());
        } catch (Exception ex) {
            logger.error("saveInterview - error al guardar Interview: {}", ex.getMessage(), ex);
            throw new RuntimeException("Error saving Interview", ex);
        }

        // Mapear a DTO de respuesta
        InterviewDto responseDto = interviewMapper.toDto(saved);
        logger.debug("saveInterview - mapeo a DTO de respuesta completado: {}", responseDto);
        logger.debug("saveInterview - fin");
        return responseDto;
    }
}