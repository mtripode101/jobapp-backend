package com.mtripode.jobapp.service.service;

import java.util.List;
import java.util.Optional;

import com.mtripode.jobapp.service.model.Company;

public interface CompanyService {

      Company saveCompany(Company company);

    List<Company> getAllCompanies();

    Optional<Company> getCompanyById(Long id);

    Optional<Company> getCompanyByName(String name);

    void deleteCompany(Long id);

    Optional<Company> findByNameIgnoreCase(String name);

}
