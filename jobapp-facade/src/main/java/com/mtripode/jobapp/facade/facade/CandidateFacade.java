package com.mtripode.jobapp.facade.facade;

import com.mtripode.jobapp.facade.dto.CandidateDto;

import java.util.List;
import java.util.Optional;

public interface CandidateFacade {

    // Obtener todos los candidatos
    List<CandidateDto> getAllCandidates();

    // Obtener candidato por ID
    Optional<CandidateDto> getCandidateById(Long id);

    // Crear nuevo candidato
    CandidateDto createCandidate(CandidateDto candidateDto);

    // Actualizar candidato existente
    CandidateDto updateCandidate(Long id, CandidateDto candidateDto);

    // Eliminar candidato por ID
    void deleteCandidate(Long id);

    // Buscar candidatos por nombre completo
    List<CandidateDto> findByFullName(String fullName);

    // Buscar candidato por email
    Optional<CandidateDto> findByEmail(String email);

    // Buscar candidatos por teléfono
    List<CandidateDto> findByPhone(String phone);

    // Buscar candidatos cuyo nombre contenga un keyword (case-insensitive)
    List<CandidateDto> findByFullNameContaining(String keyword);

    // Buscar candidatos con aplicaciones asociadas
    List<CandidateDto> findWithApplications();

    // Buscar candidatos con más de X aplicaciones
    List<CandidateDto> findCandidatesWithMoreThan(int minApps);
}
