package com.mtripode.jobapp.facade.facade;

import java.util.List;
import java.util.Optional;

import com.mtripode.jobapp.facade.dto.InterviewDto;

public interface InterviewFacade {

    // Obtener todas las entrevistas
    List<InterviewDto> getAllInterviews();

    // Obtener entrevista por ID
    Optional<InterviewDto> getInterviewById(Long id);

    // Eliminar entrevista por ID
    void deleteInterview(Long id);

    // Guardar o actualizar entrevista
    InterviewDto saveInterview(InterviewDto interviewDto);

    // Marcar una oferta relacionada como aceptada (por id de oferta)
    void acceptOffer(Long offerId);

    // Marcar una oferta relacionada como rechazada (por id de oferta)
    void rejectOffer(Long offerId);

}
