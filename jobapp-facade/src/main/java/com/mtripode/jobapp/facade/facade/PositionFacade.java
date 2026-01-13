package com.mtripode.jobapp.facade.facade;

import java.util.List;
import java.util.Optional;

import com.mtripode.jobapp.facade.dto.PositionDto;

public interface PositionFacade {

    // Guardar o actualizar una posición
    PositionDto savePosition(PositionDto dto);

    // Buscar posición por ID
    Optional<PositionDto> findById(Long id);

    // Listar todas las posiciones
    List<PositionDto> findAll();

    // Eliminar posición por ID
    void deleteById(Long id);

    // Buscar posiciones por título exacto
    List<PositionDto> findByTitle(String title);

    // Buscar posiciones cuyo título contenga un keyword (case-insensitive)
    List<PositionDto> findByTitleContaining(String keyword);

    // Buscar posiciones por ubicación
    List<PositionDto> findByLocation(String location);

    // Buscar posiciones por nombre de compañía
    List<PositionDto> findByCompanyName(String companyName);

    // Buscar posiciones con aplicaciones asociadas
    List<PositionDto> findWithApplications();

    // Actualizar posición por ID
    PositionDto updatePosition(Long id, PositionDto dto);
}
