package com.mtripode.jobapp.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mtripode.jobapp.service.model.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByName(String name);

    List<Company> findByNameContainingIgnoreCase(String keyword);

    Optional<Company> findByWebsite(String website);

    List<Company> findByNameStartingWithIgnoreCase(String prefix);

    List<Company> findByNameEndingWithIgnoreCase(String suffix);

    Optional<Company> findByNameIgnoreCase(String name);

}