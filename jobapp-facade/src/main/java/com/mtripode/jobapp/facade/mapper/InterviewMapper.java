package com.mtripode.jobapp.facade.mapper;

import org.springframework.stereotype.Component;

import com.mtripode.jobapp.facade.dto.InterviewDto;
import com.mtripode.jobapp.service.model.Interview;
import com.mtripode.jobapp.service.model.JobApplication;

@Component
public class InterviewMapper {

    public static InterviewDto toDto(Interview interview) {
        if (interview == null) {
            return null;
        }
        InterviewDto dto = new InterviewDto(
                interview.getScheduledAt(),
                interview.getType(),
                interview.getFeedback()
        );
        // Copiar id y applicationId si están presentes
        dto.setId(interview.getId());
        if (interview.getApplication() != null) {
            dto.setApplicationId(interview.getApplication().getId());
        }
        return dto;
    }

    public static Interview toEntity(InterviewDto dto) {
        if (dto == null) {
            return null;
        }
        // Usar constructor si existe, y luego setear id si viene en el DTO
        Interview entity = new Interview(
                dto.getScheduledAt(),
                dto.getType(),
                dto.getFeedback()
        );
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        // NOTA: no seteamos application aquí; el caller debe hacerlo (fachada/servicio)
        return entity;
    }

    /**
     * Conveniencia: convierte DTO a entidad y asigna la JobApplication ya resuelta.
     * Útil para tests o para simplificar la fachada si prefieres delegar la asignación aquí.
     */
    public Interview toEntity(InterviewDto dto, JobApplication application) {
        Interview entity = toEntity(dto);
        if (application != null) {
            entity.setApplication(application);
        }
        return entity;
    }
}