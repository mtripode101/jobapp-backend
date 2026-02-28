package com.mtripode.jobapp.service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.mtripode.jobapp.service.repository.CompanyRepository;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    private CompanyServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CompanyServiceImpl(companyRepository);
    }

    @Test
    void methodsShouldDelegateToRepository() {
        Company company = new Company();
        List<Company> companies = List.of(company);
        Optional<Company> optional = Optional.of(company);

        when(companyRepository.save(company)).thenReturn(company);
        when(companyRepository.findAll()).thenReturn(companies);
        when(companyRepository.findById(1L)).thenReturn(optional);
        when(companyRepository.findByName("Acme")).thenReturn(optional);
        when(companyRepository.findByNameIgnoreCase("acme")).thenReturn(optional);

        assertThat(service.saveCompany(company)).isSameAs(company);
        assertThat(service.getAllCompanies()).isSameAs(companies);
        assertThat(service.getCompanyById(1L)).isSameAs(optional);
        assertThat(service.getCompanyByName("Acme")).isSameAs(optional);
        service.deleteCompany(1L);
        assertThat(service.findByNameIgnoreCase("acme")).isSameAs(optional);

        verify(companyRepository).save(company);
        verify(companyRepository).findAll();
        verify(companyRepository).findById(1L);
        verify(companyRepository).findByName("Acme");
        verify(companyRepository).deleteById(1L);
        verify(companyRepository).findByNameIgnoreCase("acme");
    }
}
