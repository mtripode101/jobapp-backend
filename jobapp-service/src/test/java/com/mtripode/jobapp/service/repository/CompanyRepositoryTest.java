package com.mtripode.jobapp.service.repository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.mtripode.jobapp.service.model.Company;
import com.mtripode.jobapp.service.repository.CompanyRepository;

@DataJpaTest
@ActiveProfiles("test")
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    private Company buildCompany(String name, String website) {
        return new Company(name,  website, "");
    }

    @Test
    @DisplayName("Guardar y encontrar compañía por nombre")
    void testFindByName() {
        Company company = buildCompany("Globant",  "https://globant.com");

        companyRepository.save(company);

        Optional<Company> found = companyRepository.findByName("Globant");
        assertThat(found).isPresent();
    }

    @Test
    @DisplayName("Buscar compañías por palabra clave en nombre")
    void testFindByNameContainingIgnoreCase() {
        Company company = buildCompany("Globant", "https://globant.com");

        companyRepository.save(company);

        List<Company> result = companyRepository.findByNameContainingIgnoreCase("glo");
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getName()).isEqualTo("Globant");
    }

}