package com.mtripode.jobapp.service.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mtripode.jobapp.service.model.Company;
import com.mtripode.jobapp.service.repository.CompanyRepository;
import com.mtripode.jobapp.service.service.CompanyService;

@Service
public class CompanyServiceImpl implements CompanyService{

    private final CompanyRepository companyRepository;

    // Inyección de dependencias vía constructor
    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // Crear o actualizar una compañía
    @Override
    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    // Obtener todas las compañías
    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    // Buscar compañía por ID
    @Override
    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    // Buscar compañía por nombre
    @Override
    public Optional<Company> getCompanyByName(String name) {
        return companyRepository.findByName(name);
    }

    // Eliminar compañía por ID
    @Override
    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

    @Override
    public Optional<Company> findByNameIgnoreCase(String name) {
        return companyRepository.findByNameIgnoreCase(name);
    }

}

