package com.mtripode.jobapp.service.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mtripode.jobapp.service.model.Company;
import com.mtripode.jobapp.service.model.Position;
import com.mtripode.jobapp.service.repository.PositionRepository;
import com.mtripode.jobapp.service.service.CompanyService;
import com.mtripode.jobapp.service.service.PositionService;

@Service
@Transactional
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;
    private final CompanyService companyService;

    public PositionServiceImpl(PositionRepository positionRepository, CompanyService companyService) {
        this.positionRepository = positionRepository;
        this.companyService = companyService;
    }

    // Save or update a position with validations
    @Override
    public Position savePosition(Position position) {
        // Validation: title is mandatory
        if (position.getTitle() == null || position.getTitle().isBlank()) {
            throw new IllegalArgumentException("The position title is mandatory");
        }

        // Validation: location is mandatory
        if (position.getLocation() == null || position.getLocation().isBlank()) {
            throw new IllegalArgumentException("The position location is mandatory");
        }

        // Validation: company must exist
        if (position.getCompany() == null || position.getCompany().getId() == null) {
            throw new IllegalArgumentException("The position must be associated with a company");
        } else {
            companyService.getCompanyById(position.getCompany().getId())
                          .orElseThrow(() -> new IllegalArgumentException("The specified company does not exist"));
        }

        // If all validations pass, save the position
        return positionRepository.save(position);
    }

    // ✅ Updated method to update a Position using companyService
    @Override
    public Position updatePosition(Long id, Position updatedData) {
        return positionRepository.findById(id)
                .map(existing -> {
                    // Update basic fields
                    existing.setTitle(updatedData.getTitle());
                    existing.setLocation(updatedData.getLocation());
                    existing.setDescription(updatedData.getDescription());

                    // Update company relationship safely
                    if (updatedData.getCompany() != null && updatedData.getCompany().getId() != null) {
                        Company company = companyService.getCompanyById(updatedData.getCompany().getId())
                                .orElseThrow(() -> new RuntimeException(
                                        "Company not found with id " + updatedData.getCompany().getId()));
                        existing.setCompany(company);
                    }

                    // Save and return the updated entity
                    return positionRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Position not found with id " + id));
    }

    // Find position by ID
    @Override
    public Optional<Position> findById(Long id) {
        return positionRepository.findById(id);
    }

    // Find all positions
    @Override
    public List<Position> findAll() {
        return positionRepository.findAll();
    }

    // Delete position by ID
    @Override
    public void deleteById(Long id) {
        positionRepository.deleteById(id);
    }

    // Find positions by exact title
    @Override
    public List<Position> findByTitle(String title) {
        return positionRepository.findByTitle(title);
    }

    // Find positions where title contains a keyword (case-insensitive)
    @Override
    public List<Position> findByTitleContainingIgnoreCase(String keyword) {
        return positionRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // Find positions by location
    @Override
    public List<Position> findByLocation(String location) {
        return positionRepository.findByLocation(location);
    }

    // Find positions by company name
    @Override
    public List<Position> findByCompanyName(String companyName) {
        return positionRepository.findByCompany_Name(companyName);
    }

    // Find positions that have job applications linked
    @Override
    public List<Position> findWithApplications() {
        return positionRepository.findByApplicationsIsNotEmpty();
    }
}