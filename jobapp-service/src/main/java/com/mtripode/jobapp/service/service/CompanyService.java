package com.mtripode.jobapp.service.service;

import java.util.List;
import java.util.Optional;

import com.mtripode.jobapp.service.model.Company;

public interface CompanyService {

    // Crear o actualizar una compañía
    Company saveCompany(Company company);

    // Obtener todas las compañías
    List<Company> getAllCompanies();

    // Buscar compañía por ID
    Optional<Company> getCompanyById(Long id);

    // Buscar compañía por nombre
    Optional<Company> getCompanyByName(String name);

    // Eliminar compañía por ID
    void deleteCompany(Long id);
}
