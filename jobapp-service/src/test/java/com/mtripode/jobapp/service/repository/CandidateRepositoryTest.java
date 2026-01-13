package com.mtripode.jobapp.service.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
import com.mtripode.jobapp.service.repository.CandidateRepository;
import com.mtripode.jobapp.service.repository.CompanyRepository;
import com.mtripode.jobapp.service.repository.JobApplicationRepository;

@DataJpaTest
@ActiveProfiles("test")
class CandidateRepositoryTest {

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    private Candidate buildCandidate(String name, String email, String phone) {
        ContactInfo contactInfo = new ContactInfo(email, phone, null, null);
        return new Candidate(name, contactInfo);
    }

    @Test
    @DisplayName("Save and find candidate by full name")
    void testFindByFullName() {
        Candidate candidate = buildCandidate("John Doe", "john@example.com", "1234567890");
        candidateRepository.save(candidate);

        List<Candidate> found = candidateRepository.findByFullName("John Doe");
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getContactInfo().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Find candidate by email")
    void testFindByEmail() {
        Candidate candidate = buildCandidate("Jane Doe", "jane@example.com", "9876543210");
        candidateRepository.save(candidate);

        Optional<Candidate> found = candidateRepository.findByContactInfoEmail("jane@example.com");
        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Jane Doe");
    }

    @Test
    @DisplayName("Find candidates by phone number")
    void testFindByPhone() {
        Candidate candidate = buildCandidate("Alice", "alice@example.com", "5551234567");
        candidateRepository.save(candidate);

        List<Candidate> found = candidateRepository.findByContactInfoPhone("5551234567");
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getFullName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("Find candidates whose name contains keyword (case-insensitive)")
    void testFindByFullNameContainingIgnoreCase() {
        Candidate candidate = buildCandidate("Carlos Martin", "carlos@example.com", "1112223333");
        candidateRepository.save(candidate);

        List<Candidate> found = candidateRepository.findByFullNameContainingIgnoreCase("martin");
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getFullName()).contains("Carlos");
    }

    @Test
    @DisplayName("Find candidates with non-empty applications list")
    void testFindByApplicationsIsNotEmpty() {
        Candidate candidate = candidateRepository.save(buildCandidate("Bob", "bob@example.com", "3334445555"));
        Company company = companyRepository.save(new Company("TechCorp", "https://techcorp.com", ""));
        Position position = positionRepository.save(new Position("Backend Engineer", "Java role", "Remote", company));

        JobApplication app = new JobApplication("link", "website", LocalDate.now(),
                "desc", candidate, company, position, Status.APPLIED);

        // Fix: maintain both sides of the relationship
        candidate.getApplications().add(app);
        app.setCandidate(candidate);

        jobApplicationRepository.save(app);
        candidateRepository.save(candidate);

        List<Candidate> found = candidateRepository.findByApplicationsIsNotEmpty();
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getApplications()).hasSize(1);
    }

    @Test
    @DisplayName("Find candidates with more than X applications")
    void testFindCandidatesWithMoreThan() {
        Candidate candidate = candidateRepository.save(buildCandidate("Eva", "eva@example.com", "7778889999"));
        Company company = companyRepository.save(new Company("Globant", "https://globant.com", ""));
        Position position = positionRepository.save(new Position("Frontend Engineer", "React role", "Remote", company));

        JobApplication app1 = new JobApplication("link1", "website1", LocalDate.now(),
                "desc1", candidate, company, position, Status.APPLIED);
        JobApplication app2 = new JobApplication("link2", "website2", LocalDate.now(),
                "desc2", candidate, company, position, Status.APPLIED);

        // Fix: maintain both sides of the relationship
        candidate.getApplications().add(app1);
        candidate.getApplications().add(app2);
        app1.setCandidate(candidate);
        app2.setCandidate(candidate);

        jobApplicationRepository.save(app1);
        jobApplicationRepository.save(app2);
        candidateRepository.save(candidate);

        List<Candidate> found = candidateRepository.findCandidatesWithMoreThan(1);
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getApplications()).hasSizeGreaterThan(1);
    }
}
