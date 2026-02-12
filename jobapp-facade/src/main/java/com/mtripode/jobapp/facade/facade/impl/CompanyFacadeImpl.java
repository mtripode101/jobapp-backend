package com.mtripode.jobapp.facade.facade.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.mtripode.jobapp.facade.dto.CompanyDto;
import com.mtripode.jobapp.facade.facade.CompanyFacade;
import com.mtripode.jobapp.facade.mapper.CompanyMapper;
import com.mtripode.jobapp.service.model.Company;
import com.mtripode.jobapp.service.service.CompanyService;

@Component
public class CompanyFacadeImpl implements CompanyFacade {

    private final CompanyService companyService;
    private final CompanyMapper companyMapper;

    public CompanyFacadeImpl(CompanyService companyService, CompanyMapper companyMapper) {
        this.companyService = companyService;
        this.companyMapper = companyMapper;
    }

    @Override
    @Cacheable(value = "companies", key = "'all'")
    public List<CompanyDto> getAllCompanies() {
        return companyService.getAllCompanies()
                .stream()
                .map(companyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "companies", key = "#id")
    public Optional<CompanyDto> getCompanyById(Long id) {
        return companyService.getCompanyById(id)
                .map(companyMapper::toDto);
    }

    @Override
    @CacheEvict(value = "companies", key = "#dto.id", allEntries = true)
    public CompanyDto saveCompany(CompanyDto dto) {
        Company company = companyMapper.toEntity(dto);
        Company saved = companyService.saveCompany(company);
        return companyMapper.toDto(saved);
    }

    @Override
    @CacheEvict(value = "companies", key = "#id", allEntries = true)
    public void deleteCompany(Long id) {
        companyService.deleteCompany(id);
    }

    @Override
    @CacheEvict(value = "companies", key = "#name", allEntries = true)
    public Optional<CompanyDto> findByNameIgnoreCase(String name) {
        return companyService.findByNameIgnoreCase(name)
                .map(companyMapper::toDto);
    }

}
