package com.mtripode.jobapp.service.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.mtripode.jobapp.service.model.Interview;
import com.mtripode.jobapp.service.model.InterviewType;
import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.Position;
import com.mtripode.jobapp.service.model.Status;

@DataJpaTest
@ActiveProfiles("test")
class InterviewRepositoryTest {

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    private Interview buildInterview(InterviewType type, String feedback, LocalDateTime scheduledAt) {
        // Crear ContactInfo embebido
        ContactInfo contactInfo = new ContactInfo(
                "john.doe@example.com", // email
                "1234567890", // phone
                null, // linkedIn opcional
                null // github opcional
        );

        Candidate candidate = candidateRepository.save(new Candidate("John Doe", contactInfo));

        Company company = companyRepository.save(new Company("TechCorp", "https://techcorp.com", ""));
        Position position = positionRepository.save(new Position("Backend Engineer", "Java role", "Remote", company));

        JobApplication application = jobApplicationRepository.save(
                new JobApplication("link", "website", LocalDate.now(), "desc", candidate, company, position, Status.APPLIED)
        );

        return new Interview(scheduledAt, type, feedback, application);
    }

    @Test
    @DisplayName("Find interviews by type")
    void testFindByType() {
        Interview interview = buildInterview(InterviewType.ONLINE, "Good communication", LocalDateTime.now().plusDays(1));
        interviewRepository.save(interview);

        List<Interview> results = interviewRepository.findByType(InterviewType.ONLINE);
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getType()).isEqualTo(InterviewType.ONLINE);
    }

    @Test
    @DisplayName("Find interviews scheduled after a specific date/time")
    void testFindByScheduledAtAfter() {
        Interview interview = buildInterview(InterviewType.PHONE, "Strong technical skills", LocalDateTime.now().plusDays(2));
        interviewRepository.save(interview);

        List<Interview> results = interviewRepository.findByScheduledAtAfter(LocalDateTime.now().plusDays(1));
        assertThat(results).isNotEmpty();
    }

    @Test
    @DisplayName("Find interviews scheduled before a specific date/time")
    void testFindByScheduledAtBefore() {
        Interview interview = buildInterview(InterviewType.ONSITE, "Needs improvement", LocalDateTime.now().minusDays(1));
        interviewRepository.save(interview);

        List<Interview> results = interviewRepository.findByScheduledAtBefore(LocalDateTime.now());
        assertThat(results).isNotEmpty();
    }

    @Test
    @DisplayName("Find interviews where feedback contains keyword (case-insensitive)")
    void testFindByFeedbackContainingIgnoreCase() {
        Interview interview = buildInterview(InterviewType.ONLINE, "Excellent problem-solving", LocalDateTime.now().plusDays(3));
        interviewRepository.save(interview);

        List<Interview> results = interviewRepository.findByFeedbackContainingIgnoreCase("excellent");
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getFeedback()).containsIgnoringCase("excellent");
    }

    @Test
    @DisplayName("Find interviews linked to a specific job application")
    void testFindByApplicationId() {
        Interview interview = buildInterview(InterviewType.PHONE, "Average performance", LocalDateTime.now().plusDays(4));
        JobApplication application = interview.getApplication();
        interviewRepository.save(interview);

        List<Interview> results = interviewRepository.findByApplication_Id(application.getId());
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getApplication().getId()).isEqualTo(application.getId());
    }
}
