package com.mtripode.jobapp.service.service;

import java.util.List;
import java.util.Optional;

import com.mtripode.jobapp.service.model.Position;

public interface PositionService {

    // Save or update a position with validations
    Position savePosition(Position position);

    // Update a position by ID
    Position updatePosition(Long id, Position updatedData);

    // Find position by ID
    Optional<Position> findById(Long id);

    // Find all positions
    List<Position> findAll();

    // Delete position by ID
    void deleteById(Long id);

    // Find positions by exact title
    List<Position> findByTitle(String title);

    // Find positions where title contains a keyword (case-insensitive)
    List<Position> findByTitleContainingIgnoreCase(String keyword);

    // Find positions by location
    List<Position> findByLocation(String location);

    // Find positions by company name
    List<Position> findByCompanyName(String companyName);

    // Find positions that have job applications linked
    List<Position> findWithApplications();
}
