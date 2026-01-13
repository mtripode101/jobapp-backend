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

@DataJpaTest
@ActiveProfiles("test")
class JobApplicationRepositoryTest {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PositionRepository positionRepository;

    private JobApplication buildApplication(Status status, String companyName, String candidateName, String positionTitle) {
        // Ensure email is valid: replace spaces with dots
        String safeEmail = candidateName.toLowerCase().replace(" ", ".") + "@example.com";

        // Crear ContactInfo embebido
        ContactInfo contactInfo = new ContactInfo(
                safeEmail, // email
                "1234567890", // phone
                null, // linkedIn opcional
                null // github opcional
        );

        Candidate candidate = candidateRepository.save(new Candidate(candidateName, contactInfo));

        Company company = companyRepository.save(
                new Company(companyName, "https://" + companyName.toLowerCase() + ".com", "")
        );

        Position position = positionRepository.save(
                new Position(positionTitle, "Description", "Remote", company)
        );

        return new JobApplication("link", "website", LocalDate.now(),
                "desc", candidate, company, position, status);
    }

    @Test
    @DisplayName("Find applications by status")
    void testFindByStatus() {
        JobApplication app = buildApplication(Status.APPLIED, "TechCorp", "John Doe", "Backend Engineer");
        jobApplicationRepository.save(app);

        List<JobApplication> results = jobApplicationRepository.findByStatus(Status.APPLIED);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo(Status.APPLIED);
    }

    @Test
    @DisplayName("Find applications by company name")
    void testFindByCompanyName() {
        JobApplication app = buildApplication(Status.APPLIED, "Globant", "Jane Doe", "Frontend Engineer");
        jobApplicationRepository.save(app);

        List<JobApplication> results = jobApplicationRepository.findByCompany_Name("Globant");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getCompany().getName()).isEqualTo("Globant");
    }

    @Test
    @DisplayName("Find applications by website source")
    void testFindByWebsiteSource() {
        JobApplication app = buildApplication(Status.APPLIED, "TechCorp", "Alice", "QA Engineer");
        app.setWebsiteSource("LinkedIn");
        jobApplicationRepository.save(app);

        List<JobApplication> results = jobApplicationRepository.findByWebsiteSource("LinkedIn");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getWebsiteSource()).isEqualTo("LinkedIn");
    }

    @Test
    @DisplayName("Find applications by source link")
    void testFindBySourceLink() {
        JobApplication app = buildApplication(Status.APPLIED, "TechCorp", "Bob", "DevOps Engineer");
        app.setSourceLink("https://jobs.techcorp.com/apply");
        jobApplicationRepository.save(app);

        List<JobApplication> results = jobApplicationRepository.findBySourceLink("https://jobs.techcorp.com/apply");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getSourceLink()).contains("techcorp");
    }

    @Test
    @DisplayName("Find applications by status and rejection date")
    void testFindByStatusAndDateRejected() {
        JobApplication app = buildApplication(Status.REJECTED, "Globant", "Eva", "Data Engineer");
        app.setDateRejected(LocalDate.now());
        jobApplicationRepository.save(app);

        List<JobApplication> results = jobApplicationRepository.findByStatusAndDateRejected(Status.REJECTED, LocalDate.now());
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    @DisplayName("Find applications by candidate full name")
    void testFindByCandidateFullName() {
        JobApplication app = buildApplication(Status.APPLIED, "TechCorp", "Carlos Martin", "Backend Engineer");
        jobApplicationRepository.save(app);

        List<JobApplication> results = jobApplicationRepository.findByCandidate_FullName("Carlos Martin");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getCandidate().getFullName()).isEqualTo("Carlos Martin");
    }

    @Test
    @DisplayName("Find applications by position title")
    void testFindByPositionTitle() {
        JobApplication app = buildApplication(Status.APPLIED, "TechCorp", "Laura", "Cloud Architect");
        jobApplicationRepository.save(app);

        List<JobApplication> results = jobApplicationRepository.findByPosition_Title("Cloud Architect");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getPosition().getTitle()).isEqualTo("Cloud Architect");
    }

    @Test
    @DisplayName("Find applications submitted after a given date")
    void testFindByDateAppliedAfter() {
        JobApplication app = buildApplication(Status.APPLIED, "TechCorp", "Mike", "Security Engineer");
        app.setDateApplied(LocalDate.now().minusDays(1));
        jobApplicationRepository.save(app);

        List<JobApplication> results = jobApplicationRepository.findByDateAppliedAfter(LocalDate.now().minusDays(2));
        assertThat(results).isNotEmpty();
    }

    @Test
    @DisplayName("Find applications submitted before a given date")
    void testFindByDateAppliedBefore() {
        JobApplication app = buildApplication(Status.APPLIED, "TechCorp", "Nina", "ML Engineer");
        app.setDateApplied(LocalDate.now().minusDays(5));
        jobApplicationRepository.save(app);

        List<JobApplication> results = jobApplicationRepository.findByDateAppliedBefore(LocalDate.now().minusDays(2));
        assertThat(results).isNotEmpty();
    }

    @Test
    @DisplayName("Find applications by company and status")
    void testFindByCompanyNameAndStatus() {
        JobApplication app = buildApplication(Status.APPLIED, "Globant", "Oscar", "AI Engineer");
        jobApplicationRepository.save(app);

        List<JobApplication> results = jobApplicationRepository.findByCompany_NameAndStatus("Globant", Status.APPLIED);
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getCompany().getName()).isEqualTo("Globant");
        assertThat(results.get(0).getStatus()).isEqualTo(Status.APPLIED);
    }
}
