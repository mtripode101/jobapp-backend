package com.mtripode.jobapp.service.repository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.mtripode.jobapp.service.model.Candidate;
import com.mtripode.jobapp.service.model.Company;
import com.mtripode.jobapp.service.model.ContactInfo;
import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.Position;
import com.mtripode.jobapp.service.model.Status;
import com.mtripode.jobapp.service.repository.PositionRepository;

@DataJpaTest
@ActiveProfiles("test")
class PositionRepositoryTest {

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    private Position buildPosition(String title, String location, String companyName) {
        Company company = companyRepository.save(new Company(companyName, "https://" + companyName.toLowerCase() + ".com", ""));
        return new Position(title, "Description", location, company);
    }

    @Test
    @DisplayName("Find positions by exact title")
    void testFindByTitle() {
        Position position = buildPosition("Backend Engineer", "Remote", "TechCorp");
        positionRepository.save(position);

        List<Position> results = positionRepository.findByTitle("Backend Engineer");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getTitle()).isEqualTo("Backend Engineer");
    }

    @Test
    @DisplayName("Find positions where title contains keyword (case-insensitive)")
    void testFindByTitleContainingIgnoreCase() {
        Position position = buildPosition("Frontend Engineer", "Remote", "Globant");
        positionRepository.save(position);

        List<Position> results = positionRepository.findByTitleContainingIgnoreCase("frontend");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getTitle()).containsIgnoringCase("Frontend");
    }

    @Test
    @DisplayName("Find positions by location")
    void testFindByLocation() {
        Position position = buildPosition("QA Engineer", "Buenos Aires", "TechCorp");
        positionRepository.save(position);

        List<Position> results = positionRepository.findByLocation("Buenos Aires");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getLocation()).isEqualTo("Buenos Aires");
    }

    @Test
    @DisplayName("Find positions by company name")
    void testFindByCompanyName() {
        Position position = buildPosition("DevOps Engineer", "Remote", "Globant");
        positionRepository.save(position);

        List<Position> results = positionRepository.findByCompany_Name("Globant");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getCompany().getName()).isEqualTo("Globant");
    }

    @Test
    @DisplayName("Find positions that have job applications linked")
    void testFindByApplicationsIsNotEmpty() {
        Position position = positionRepository.save(buildPosition("Data Engineer", "Remote", "TechCorp"));

        // Crear ContactInfo embebido
        ContactInfo contactInfo = new ContactInfo(
                "john.doe@example.com", // email
                "1234567890", // phone
                null, // linkedIn opcional
                null // github opcional
        );

        Candidate candidate = candidateRepository.save(new Candidate("John Doe", contactInfo));
        Company company = position.getCompany();

        JobApplication application = new JobApplication(
                "link",
                "website",
                LocalDate.now(),
                "desc",
                candidate,
                company,
                position,
                Status.APPLIED,
                "JOB123"
        );

        // mantener relación bidireccional
        position.getApplications().add(application);
        application.setPosition(position);

        jobApplicationRepository.save(application);
        positionRepository.save(position);

        List<Position> results = positionRepository.findByApplicationsIsNotEmpty();
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getApplications()).hasSize(1);
    }
}
