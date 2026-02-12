package com.mtripode.jobapp.facade.facade;

import java.util.List;
import java.util.Optional;

import com.mtripode.jobapp.facade.dto.CompanyDto;

public interface CompanyFacade {

    List<CompanyDto> getAllCompanies();

    Optional<CompanyDto> getCompanyById(Long id);

    CompanyDto saveCompany(CompanyDto dto);

    void deleteCompany(Long id);

    Optional<CompanyDto> findByNameIgnoreCase(String name);

}
