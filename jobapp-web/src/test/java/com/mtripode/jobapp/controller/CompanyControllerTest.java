package com.mtripode.jobapp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mtripode.jobapp.facade.dto.CompanyDto;
import com.mtripode.jobapp.facade.facade.impl.CompanyFacadeImpl;

class CompanyControllerTest {

    private StubCompanyFacade facade;
    private CompanyController controller;

    @BeforeEach
    void setUp() {
        facade = new StubCompanyFacade();
        controller = new CompanyController(facade);
    }

    @Test
    void shouldGetAllCompanies() {
        facade.allCompanies = List.of(company(1L, "Acme"));

        List<CompanyDto> result = controller.getAllCompanies();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Acme");
    }

    @Test
    void shouldGetCompanyByIdAndByName() {
        CompanyDto dto = company(2L, "Globant");
        facade.byId = Optional.of(dto);
        facade.byName = Optional.of(dto);

        ResponseEntity<CompanyDto> byIdResponse = controller.getCompanyById(2L);
        ResponseEntity<CompanyDto> byNameResponse = controller.getCompanyByName("globant");

        assertThat(byIdResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(byNameResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturnNotFoundForMissingCompany() {
        facade.byId = Optional.empty();
        facade.byName = Optional.empty();

        assertThat(controller.getCompanyById(99L).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(controller.getCompanyByName("none").getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateAndUpdateCompany() {
        CompanyDto saved = company(10L, "Saved");
        facade.savedResult = saved;

        ResponseEntity<CompanyDto> create = controller.createCompany(company(null, "Input"));
        ResponseEntity<CompanyDto> update = controller.updateCompany(10L, company(null, "Update"));

        assertThat(create.getBody()).isSameAs(saved);
        assertThat(update.getBody()).isSameAs(saved);
        assertThat(facade.lastSavedDto.getId()).isEqualTo(10L);
    }

    @Test
    void shouldDeleteCompanyAndHandleError() {
        ResponseEntity<?> okResponse = controller.deleteCompany(5L);
        assertThat(okResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        facade.throwOnDelete = true;
        ResponseEntity<?> errorResponse = controller.deleteCompany(6L);
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(errorResponse.getBody()).isEqualTo("The company could not be deleted.");
    }

    private static CompanyDto company(Long id, String name) {
        CompanyDto dto = new CompanyDto();
        dto.setId(id);
        dto.setName(name);
        return dto;
    }

    static class StubCompanyFacade extends CompanyFacadeImpl {

        List<CompanyDto> allCompanies = List.of();
        Optional<CompanyDto> byId = Optional.empty();
        Optional<CompanyDto> byName = Optional.empty();
        CompanyDto savedResult;
        CompanyDto lastSavedDto;
        boolean throwOnDelete;

        StubCompanyFacade() {
            super(null, null);
        }

        @Override
        public List<CompanyDto> getAllCompanies() {
            return allCompanies;
        }

        @Override
        public Optional<CompanyDto> getCompanyById(Long id) {
            return byId;
        }

        @Override
        public Optional<CompanyDto> findByNameIgnoreCase(String name) {
            return byName;
        }

        @Override
        public CompanyDto saveCompany(CompanyDto dto) {
            this.lastSavedDto = dto;
            return savedResult;
        }

        @Override
        public void deleteCompany(Long id) {
            if (throwOnDelete) {
                throw new RuntimeException("delete error");
            }
        }
    }
}
