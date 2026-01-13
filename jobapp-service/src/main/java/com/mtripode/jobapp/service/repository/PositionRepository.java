package com.mtripode.jobapp.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mtripode.jobapp.service.model.Position;


@Repository
public interface PositionRepository extends JpaRepository<Position, Long> {

    // Find positions by exact title
    List<Position> findByTitle(String title);

    // Find positions where title contains a keyword (case-insensitive)
    List<Position> findByTitleContainingIgnoreCase(String keyword);

    // Find positions by location
    List<Position> findByLocation(String location);

    // Find positions by company name
    List<Position> findByCompany_Name(String companyName);

    // Find positions that have job applications linked
    List<Position> findByApplicationsIsNotEmpty();
}