package com.mtripode.jobapp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mtripode.jobapp.facade.dto.PositionDto;
import com.mtripode.jobapp.facade.facade.impl.PositionFacadeImpl;

class PositionControllerTest {

    private StubPositionFacade facade;
    private PositionController controller;

    @BeforeEach
    void setUp() {
        facade = new StubPositionFacade();
        controller = new PositionController(facade);
    }

    @Test
    void shouldCreateGetUpdateDeleteAndListPositions() {
        PositionDto dto = position(1L, "Backend");
        facade.saved = dto;
        facade.byId = Optional.of(dto);
        facade.all = List.of(dto);

        ResponseEntity<PositionDto> create = controller.createPosition(position(null, "Backend"));
        ResponseEntity<PositionDto> get = controller.getPositionById(1L);
        ResponseEntity<PositionDto> update = controller.updatePosition(1L, position(null, "New"));
        ResponseEntity<List<PositionDto>> all = controller.getAllPositions();
        ResponseEntity<Void> delete = controller.deletePosition(1L);

        assertThat(create.getBody()).isSameAs(dto);
        assertThat(get.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(update.getBody()).isSameAs(dto);
        assertThat(all.getBody()).hasSize(1);
        assertThat(delete.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void shouldReturnNotFoundWhenPositionMissing() {
        facade.byId = Optional.empty();

        ResponseEntity<PositionDto> response = controller.getPositionById(999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldSearchPositions() {
        PositionDto dto = position(2L, "Java");
        facade.byTitle = List.of(dto);
        facade.byTitleContains = List.of(dto);
        facade.byLocation = List.of(dto);
        facade.byCompany = List.of(dto);
        facade.withApplications = List.of(dto);

        assertThat(controller.getByTitle("Java").getBody()).hasSize(1);
        assertThat(controller.getByTitleContaining("J").getBody()).hasSize(1);
        assertThat(controller.getByLocation("Remote").getBody()).hasSize(1);
        assertThat(controller.getByCompanyName("Acme").getBody()).hasSize(1);
        assertThat(controller.getWithApplications().getBody()).hasSize(1);
    }

    private static PositionDto position(Long id, String title) {
        PositionDto dto = new PositionDto();
        dto.setId(id);
        dto.setTitle(title);
        return dto;
    }

    static class StubPositionFacade extends PositionFacadeImpl {

        PositionDto saved;
        Optional<PositionDto> byId = Optional.empty();
        List<PositionDto> all = List.of();
        List<PositionDto> byTitle = List.of();
        List<PositionDto> byTitleContains = List.of();
        List<PositionDto> byLocation = List.of();
        List<PositionDto> byCompany = List.of();
        List<PositionDto> withApplications = List.of();

        StubPositionFacade() {
            super(null, null);
        }

        @Override
        public PositionDto savePosition(PositionDto dto) {
            return saved;
        }

        @Override
        public Optional<PositionDto> findById(Long id) {
            return byId;
        }

        @Override
        public PositionDto updatePosition(Long id, PositionDto dto) {
            return saved;
        }

        @Override
        public List<PositionDto> findAll() {
            return all;
        }

        @Override
        public void deleteById(Long id) {
        }

        @Override
        public List<PositionDto> findByTitle(String title) {
            return byTitle;
        }

        @Override
        public List<PositionDto> findByTitleContaining(String keyword) {
            return byTitleContains;
        }

        @Override
        public List<PositionDto> findByLocation(String location) {
            return byLocation;
        }

        @Override
        public List<PositionDto> findByCompanyName(String companyName) {
            return byCompany;
        }

        @Override
        public List<PositionDto> findWithApplications() {
            return withApplications;
        }
    }
}
