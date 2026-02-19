package com.mtripode.jobapp.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mtripode.jobapp.facade.dto.CompanyDto;
import com.mtripode.jobapp.facade.facade.CompanyFacade;
import com.mtripode.jobapp.facade.facade.impl.CompanyFacadeImpl;

@RestController
@RequestMapping("/companies")
@CrossOrigin(origins = "http://localhost:3000") // habilita CORS solo para este controlador
public class CompanyController {

    private final CompanyFacade companyFacade;

    public CompanyController(CompanyFacadeImpl companyFacade) {
        this.companyFacade = companyFacade;
    }

    @GetMapping
    public List<CompanyDto> getAllCompanies() {
        return companyFacade.getAllCompanies();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDto> getCompanyById(@PathVariable Long id) {
        return companyFacade.getCompanyById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{name}/search")
    public ResponseEntity<CompanyDto> getCompanyByName(@PathVariable String name) {
        return companyFacade.findByNameIgnoreCase(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CompanyDto> createCompany(@RequestBody CompanyDto companyDto) {
        return ResponseEntity.ok(companyFacade.saveCompany(companyDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyDto> updateCompany(@PathVariable Long id, @RequestBody CompanyDto companyDto) {
        companyDto.setId(id);
        return ResponseEntity.ok(companyFacade.saveCompany(companyDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyFacade.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}
