package com.mtripode.jobapp.facade.facade;

import com.mtripode.jobapp.facade.dto.PositionDto;
import com.mtripode.jobapp.facade.mapper.PositionMapper;
import com.mtripode.jobapp.service.model.Company;
import com.mtripode.jobapp.service.model.Position;
import com.mtripode.jobapp.service.service.CompanyService;
import com.mtripode.jobapp.service.service.PositionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mtripode.jobapp.facade.facade.impl.PositionFacadeImpl;

@ExtendWith(MockitoExtension.class)
class PositionFacadeTest {

    @Mock
    private PositionService positionService;

    @Mock
    private CompanyService companyService; // mock para inyectar en el mapper

    private PositionMapper positionMapper;
    private PositionFacadeImpl positionFacade;

    @BeforeEach
    void setUp() {
        positionMapper = new PositionMapper(companyService);
        positionFacade = new PositionFacadeImpl(positionService, positionMapper);
    }

    private Company buildCompany() {
        Company company = new Company();
        company.setId(10L);
        company.setName("Tech Corp");
        return company;
    }

    private Position buildPosition() {
        Position position = new Position();
        position.setId(1L);
        position.setTitle("Backend Developer");
        position.setLocation("Remote");
        position.setDescription("Work on scalable backend systems");
        position.setCompany(buildCompany());
        return position;
    }

    private PositionDto buildPositionDto() {
        PositionDto dto = new PositionDto();
        dto.setId(1L);
        dto.setTitle("Backend Developer");
        dto.setLocation("Remote");
        dto.setDescription("Work on scalable backend systems");
        dto.setCompanyName("Tech Corp");
        return dto;
    }

    @Test
    @DisplayName("Save position should persist and return DTO")
    void testSavePosition() {
        Position position = buildPosition();
        PositionDto dto = buildPositionDto();

        when(companyService.getCompanyByName("Tech Corp")).thenReturn(Optional.of(buildCompany()));
        when(positionService.savePosition(any(Position.class))).thenReturn(position);

        PositionDto saved = positionFacade.savePosition(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Backend Developer");
        assertThat(saved.getCompanyName()).isEqualTo("Tech Corp");
        verify(positionService, times(1)).savePosition(any(Position.class));
    }

    @Test
    @DisplayName("Find position by ID should return Optional DTO")
    void testFindById() {
        Position position = buildPosition();
        when(positionService.findById(1L)).thenReturn(Optional.of(position));

        Optional<PositionDto> result = positionFacade.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getLocation()).isEqualTo("Remote");
        verify(positionService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Find all positions should return list of DTOs")
    void testFindAll() {
        Position position = buildPosition();
        when(positionService.findAll()).thenReturn(List.of(position));

        List<PositionDto> results = positionFacade.findAll();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Backend Developer");
        verify(positionService, times(1)).findAll();
    }

    @Test
    @DisplayName("Delete position should call service delete")
    void testDeleteById() {
        positionFacade.deleteById(1L);
        verify(positionService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Find positions by title should return list of DTOs")
    void testFindByTitle() {
        Position position = buildPosition();
        when(positionService.findByTitle("Backend Developer")).thenReturn(List.of(position));

        List<PositionDto> results = positionFacade.findByTitle("Backend Developer");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Backend Developer");
        verify(positionService, times(1)).findByTitle("Backend Developer");
    }

    @Test
    @DisplayName("Find positions by title containing keyword should return list of DTOs")
    void testFindByTitleContaining() {
        Position position = buildPosition();
        when(positionService.findByTitleContainingIgnoreCase("backend")).thenReturn(List.of(position));

        List<PositionDto> results = positionFacade.findByTitleContaining("backend");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).containsIgnoringCase("backend");
        verify(positionService, times(1)).findByTitleContainingIgnoreCase("backend");
    }

    @Test
    @DisplayName("Find positions by location should return list of DTOs")
    void testFindByLocation() {
        Position position = buildPosition();
        when(positionService.findByLocation("Remote")).thenReturn(List.of(position));

        List<PositionDto> results = positionFacade.findByLocation("Remote");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLocation()).isEqualTo("Remote");
        verify(positionService, times(1)).findByLocation("Remote");
    }

    @Test
    @DisplayName("Find positions by company name should return list of DTOs")
    void testFindByCompanyName() {
        Position position = buildPosition();
        when(positionService.findByCompanyName("Tech Corp")).thenReturn(List.of(position));

        List<PositionDto> results = positionFacade.findByCompanyName("Tech Corp");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCompanyName()).isEqualTo("Tech Corp");
        verify(positionService, times(1)).findByCompanyName("Tech Corp");
    }

    @Test
    @DisplayName("Find positions with applications should return list of DTOs")
    void testFindWithApplications() {
        Position position = buildPosition();
        when(positionService.findWithApplications()).thenReturn(List.of(position));

        List<PositionDto> results = positionFacade.findWithApplications();

        assertThat(results).hasSize(1);
        verify(positionService, times(1)).findWithApplications();
    }

    @Test
    @DisplayName("Update position should modify and return DTO")
    void testUpdatePosition() {
        Position position = buildPosition();
        PositionDto dto = buildPositionDto();

        when(companyService.getCompanyByName("Tech Corp")).thenReturn(Optional.of(buildCompany()));
        when(positionService.updatePosition(1L, position)).thenReturn(position);

        PositionDto updated = positionFacade.updatePosition(1L, dto);

        assertThat(updated.getTitle()).isEqualTo("Backend Developer");
        assertThat(updated.getCompanyName()).isEqualTo("Tech Corp");
        verify(positionService, times(1)).updatePosition(1L, position);
    }
}