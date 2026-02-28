package com.mtripode.jobapp.service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mtripode.jobapp.service.model.Company;
import com.mtripode.jobapp.service.model.Position;
import com.mtripode.jobapp.service.repository.PositionRepository;
import com.mtripode.jobapp.service.service.CompanyService;

@ExtendWith(MockitoExtension.class)
class PositionServiceImplTest {

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private CompanyService companyService;

    private PositionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PositionServiceImpl(positionRepository, companyService);
    }

    @Test
    void savePositionShouldValidateTitle() {
        Position position = new Position();
        position.setTitle(" ");
        position.setLocation("Remote");
        position.setCompany(buildCompany(1L));

        assertThatThrownBy(() -> service.savePosition(position))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("position title is mandatory");
    }

    @Test
    void savePositionShouldValidateLocation() {
        Position position = new Position();
        position.setTitle("Engineer");
        position.setLocation(" ");
        position.setCompany(buildCompany(1L));

        assertThatThrownBy(() -> service.savePosition(position))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("position location is mandatory");
    }

    @Test
    void savePositionShouldValidateCompanyAssociation() {
        Position position = new Position();
        position.setTitle("Engineer");
        position.setLocation("Remote");

        assertThatThrownBy(() -> service.savePosition(position))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be associated with a company");
    }

    @Test
    void savePositionShouldThrowWhenCompanyDoesNotExist() {
        Position position = new Position();
        position.setTitle("Engineer");
        position.setLocation("Remote");
        position.setCompany(buildCompany(2L));
        when(companyService.getCompanyById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.savePosition(position))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("specified company does not exist");
    }

    @Test
    void savePositionShouldPersistWhenValid() {
        Position position = new Position();
        position.setTitle("Engineer");
        position.setLocation("Remote");
        position.setCompany(buildCompany(3L));

        when(companyService.getCompanyById(3L)).thenReturn(Optional.of(position.getCompany()));
        when(positionRepository.save(position)).thenReturn(position);

        Position result = service.savePosition(position);

        assertThat(result).isSameAs(position);
        verify(positionRepository).save(position);
    }

    @Test
    void updatePositionShouldThrowWhenPositionNotFound() {
        when(positionRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updatePosition(10L, new Position()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Position not found with id 10");
    }

    @Test
    void updatePositionShouldUpdateFieldsAndCompanyWhenPresent() {
        Position existing = new Position();
        existing.setTitle("Old");
        existing.setLocation("OldLoc");
        existing.setDescription("OldDesc");
        existing.setCompany(buildCompany(1L));

        Position updated = new Position();
        updated.setTitle("New");
        updated.setLocation("NewLoc");
        updated.setDescription("NewDesc");
        updated.setCompany(buildCompany(2L));

        Company resolvedCompany = buildCompany(2L);

        when(positionRepository.findById(11L)).thenReturn(Optional.of(existing));
        when(companyService.getCompanyById(2L)).thenReturn(Optional.of(resolvedCompany));
        when(positionRepository.save(existing)).thenReturn(existing);

        Position result = service.updatePosition(11L, updated);

        assertThat(result.getTitle()).isEqualTo("New");
        assertThat(result.getLocation()).isEqualTo("NewLoc");
        assertThat(result.getDescription()).isEqualTo("NewDesc");
        assertThat(result.getCompany()).isSameAs(resolvedCompany);
        verify(positionRepository).save(existing);
    }

    @Test
    void updatePositionShouldThrowWhenNewCompanyDoesNotExist() {
        Position existing = new Position();
        Position updated = new Position();
        updated.setCompany(buildCompany(4L));

        when(positionRepository.findById(12L)).thenReturn(Optional.of(existing));
        when(companyService.getCompanyById(4L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updatePosition(12L, updated))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Company not found with id 4");
    }

    @Test
    void findersAndDeleteShouldDelegateToRepository() {
        List<Position> expected = List.of(new Position());
        when(positionRepository.findById(1L)).thenReturn(Optional.of(expected.get(0)));
        when(positionRepository.findAll()).thenReturn(expected);
        when(positionRepository.findByTitle("Engineer")).thenReturn(expected);
        when(positionRepository.findByTitleContainingIgnoreCase("eng")).thenReturn(expected);
        when(positionRepository.findByLocation("Remote")).thenReturn(expected);
        when(positionRepository.findByCompany_Name("Acme")).thenReturn(expected);
        when(positionRepository.findByApplicationsIsNotEmpty()).thenReturn(expected);

        assertThat(service.findById(1L)).contains(expected.get(0));
        assertThat(service.findAll()).isSameAs(expected);
        service.deleteById(1L);
        assertThat(service.findByTitle("Engineer")).isSameAs(expected);
        assertThat(service.findByTitleContainingIgnoreCase("eng")).isSameAs(expected);
        assertThat(service.findByLocation("Remote")).isSameAs(expected);
        assertThat(service.findByCompanyName("Acme")).isSameAs(expected);
        assertThat(service.findWithApplications()).isSameAs(expected);

        verify(positionRepository).findById(1L);
        verify(positionRepository).findAll();
        verify(positionRepository).deleteById(1L);
        verify(positionRepository).findByTitle("Engineer");
        verify(positionRepository).findByTitleContainingIgnoreCase("eng");
        verify(positionRepository).findByLocation("Remote");
        verify(positionRepository).findByCompany_Name("Acme");
        verify(positionRepository).findByApplicationsIsNotEmpty();
    }

    private static Company buildCompany(Long id) {
        Company company = new Company();
        company.setId(id);
        return company;
    }
}
