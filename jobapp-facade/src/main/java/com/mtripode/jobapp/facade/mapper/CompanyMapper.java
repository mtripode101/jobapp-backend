package com.mtripode.jobapp.facade.mapper;

import com.mtripode.jobapp.facade.dto.CompanyDto;
import com.mtripode.jobapp.service.model.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyDto toDto(Company company) {
        if (company == null) {
            return null;
        }
        return new CompanyDto(
                company.getId(),
                company.getName(),
                company.getWebsite(),
                company.getDescription() != null ? company.getDescription() : ""
        );
    }

    public Company toEntity(CompanyDto dto) {
        if (dto == null) {
            return null;
        }
        Company company = new Company();
        company.setId(dto.getId());
        company.setName(dto.getName());
        company.setWebsite(dto.getWebsite());
        company.setDescription(dto.getDescription());
        return company;
    }
}
