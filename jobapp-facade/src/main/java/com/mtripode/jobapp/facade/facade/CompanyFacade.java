package com.mtripode.jobapp.facade.facade;

import java.util.List;
import java.util.Optional;

import com.mtripode.jobapp.facade.dto.CompanyDto;

public interface CompanyFacade {

    // Obtener todas las compañías
    List<CompanyDto> getAllCompanies();

    // Obtener compañía por ID
    Optional<CompanyDto> getCompanyById(Long id);

    // Guardar o actualizar compañía
    CompanyDto saveCompany(CompanyDto dto);

    // Eliminar compañía por ID
    void deleteCompany(Long id);
}
