package com.mtripode.jobapp.facade.facade;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mtripode.jobapp.facade.dto.CompanyDto;
import com.mtripode.jobapp.facade.mapper.CompanyMapper;
import com.mtripode.jobapp.facade.facade.impl.CompanyFacadeImpl;
import com.mtripode.jobapp.service.model.Company;
import com.mtripode.jobapp.service.service.CompanyService;

@ExtendWith(MockitoExtension.class)
class CompanyFacadeTest {

    @Mock
    private CompanyService companyService;

    private CompanyMapper companyMapper;
    private CompanyFacadeImpl companyFacade;

    @BeforeEach
    void setUp() {
        companyMapper = new CompanyMapper(); // real mapper instance
        companyFacade = new CompanyFacadeImpl(companyService, companyMapper);
    }

    private Company buildCompany() {
        Company company = new Company();
        company.setId(1L);
        company.setName("Tech Corp");
        company.setWebsite("https://techcorp.com");
        company.setDescription("Innovative technology company");
        return company;
    }

    private CompanyDto buildCompanyDto() {
        return new CompanyDto(
                1L,
                "Tech Corp",
                "https://techcorp.com",
                "Innovative technology company"
        );
    }

    @Test
    @DisplayName("Get all companies should return list of DTOs")
    void testGetAllCompanies() {
        Company company = buildCompany();
        when(companyService.getAllCompanies()).thenReturn(List.of(company));

        List<CompanyDto> results = companyFacade.getAllCompanies();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("Tech Corp");
        verify(companyService, times(1)).getAllCompanies();
    }

    @Test
    @DisplayName("Get company by ID should return Optional DTO")
    void testGetCompanyById() {
        Company company = buildCompany();
        when(companyService.getCompanyById(1L)).thenReturn(Optional.of(company));

        Optional<CompanyDto> result = companyFacade.getCompanyById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getWebsite()).isEqualTo("https://techcorp.com");
        verify(companyService, times(1)).getCompanyById(1L);
    }

    @Test
    @DisplayName("Save company should persist and return DTO")
    void testSaveCompany() {
        Company company = buildCompany();
        CompanyDto dto = buildCompanyDto();

        when(companyService.saveCompany(any(Company.class))).thenReturn(company);

        CompanyDto saved = companyFacade.saveCompany(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getName()).isEqualTo("Tech Corp");
        verify(companyService, times(1)).saveCompany(any(Company.class));
    }

    @Test
    @DisplayName("Delete company should call service delete")
    void testDeleteCompany() {
        companyFacade.deleteCompany(1L);
        verify(companyService, times(1)).deleteCompany(1L);
    }
}