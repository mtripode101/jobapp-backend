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
import com.mtripode.jobapp.service.model.JobOffer;
import com.mtripode.jobapp.service.model.JobOfferStatus;


@DataJpaTest
@ActiveProfiles("test")
class JobApplicationRepositoryOfferTest {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private JobOfferRepository jobOfferRepository;

    private JobApplication buildApplication() {
        // Crear ContactInfo embebido
        ContactInfo contactInfo = new ContactInfo(
                "jane@example.com", // email
                "9876543210", // phone
                null, // linkedIn opcional
                null // github opcional
        );

        Candidate candidate = candidateRepository.save(new Candidate("Jane Doe", contactInfo));

        Company company = companyRepository.save(new Company("Globant", "https://globant.com", ""));
        Position position = positionRepository.save(new Position("Frontend Engineer", "React role", "Remote", company));

        return jobApplicationRepository.save(
                new JobApplication("link", "website", LocalDate.now(),
                        "desc", candidate, company, position, Status.APPLIED, "JOB-2")
        );
    }

    @Test
    @DisplayName("Find applications with offers not empty")
    void testFindByOffersIsNotEmpty() {
        JobApplication application = buildApplication();
        JobOffer offer = jobOfferRepository.save(new JobOffer(LocalDate.now(), JobOfferStatus.PENDING, application));

        application.addOffer(offer);
        jobApplicationRepository.save(application);

        List<JobApplication> results = jobApplicationRepository.findByOffersIsNotEmpty();
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getOffers()).hasSize(1);
    }

    @Test
    @DisplayName("Find applications with offers empty")
    void testFindByOffersIsEmpty() {
        JobApplication application = buildApplication();

        List<JobApplication> results = jobApplicationRepository.findByOffersIsEmpty();
        assertThat(results).contains(application);
    }

    @Test
    @DisplayName("Find applications by offer status")
    void testFindByOffersStatus() {
        JobApplication application = buildApplication();
        JobOffer offer = jobOfferRepository.save(new JobOffer(LocalDate.now(), JobOfferStatus.ACCEPTED, application));

        application.addOffer(offer);
        jobApplicationRepository.save(application);

        List<JobApplication> results = jobApplicationRepository.findByOffers_Status(JobOfferStatus.ACCEPTED);
        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getOffers().get(0).getStatus()).isEqualTo(JobOfferStatus.ACCEPTED);
    }

    @Test
    @DisplayName("Find applications by offer date after")
    void testFindByOffersOfferedAtAfter() {
        JobApplication application = buildApplication();
        JobOffer offer = jobOfferRepository.save(new JobOffer(LocalDate.now().minusDays(1), JobOfferStatus.PENDING, application));

        application.addOffer(offer);
        jobApplicationRepository.save(application);

        List<JobApplication> results = jobApplicationRepository.findByOffers_OfferedAtAfter(LocalDate.now().minusDays(2));
        assertThat(results).isNotEmpty();
    }

@Test
@DisplayName("Find offers with expected salary greater than")
void testFindByExpectedSalaryGreaterThan() {
    JobApplication application = buildApplication();
    JobOffer offer = new JobOffer(LocalDate.now(), JobOfferStatus.PENDING, application);
    offer.setExpectedSalary(5000.0);
    jobOfferRepository.save(offer);

    List<JobOffer> results = jobOfferRepository.findByExpectedSalaryGreaterThan(4000.0);
    assertThat(results).isNotEmpty();
    assertThat(results.get(0).getExpectedSalary()).isGreaterThan(4000.0);
}

@Test
@DisplayName("Find offers with offered salary less than")
void testFindByOfferedSalaryLessThan() {
    JobApplication application = buildApplication();
    JobOffer offer = new JobOffer(LocalDate.now(), JobOfferStatus.PENDING, application);
    offer.setOfferedSalary(3000.0);
    jobOfferRepository.save(offer);

    List<JobOffer> results = jobOfferRepository.findByOfferedSalaryLessThan(4000.0);
    assertThat(results).isNotEmpty();
    assertThat(results.get(0).getOfferedSalary()).isLessThan(4000.0);
}

@Test
@DisplayName("Find offers with expected salary between range")
void testFindByExpectedSalaryBetween() {
    JobApplication application = buildApplication();
    JobOffer offer = new JobOffer(LocalDate.now(), JobOfferStatus.PENDING, application);
    offer.setExpectedSalary(4500.0);
    jobOfferRepository.save(offer);

    List<JobOffer> results = jobOfferRepository.findByExpectedSalaryBetween(4000.0, 5000.0);
    assertThat(results).isNotEmpty();
    assertThat(results.get(0).getExpectedSalary()).isBetween(4000.0, 5000.0);
}

@Test
@DisplayName("Find offers with offered salary between range")
void testFindByOfferedSalaryBetween() {
    JobApplication application = buildApplication();
    JobOffer offer = new JobOffer(LocalDate.now(), JobOfferStatus.PENDING, application);
    offer.setOfferedSalary(3500.0);
    jobOfferRepository.save(offer);

    List<JobOffer> results = jobOfferRepository.findByOfferedSalaryBetween(3000.0, 4000.0);
    assertThat(results).isNotEmpty();
    assertThat(results.get(0).getOfferedSalary()).isBetween(3000.0, 4000.0);
}

@Test
@DisplayName("Find offers with expected salary null")
void testFindByExpectedSalaryIsNull() {
    JobApplication application = buildApplication();
    JobOffer offer = new JobOffer(LocalDate.now(), JobOfferStatus.PENDING, application);
    offer.setExpectedSalary(null);
    jobOfferRepository.save(offer);

    List<JobOffer> results = jobOfferRepository.findByExpectedSalaryIsNull();
    assertThat(results).isNotEmpty();
    assertThat(results.get(0).getExpectedSalary()).isNull();
}

@Test
@DisplayName("Find offers with offered salary not null")
void testFindByOfferedSalaryIsNotNull() {
    JobApplication application = buildApplication();
    JobOffer offer = new JobOffer(LocalDate.now(), JobOfferStatus.PENDING, application);
    offer.setOfferedSalary(2500.0);
    jobOfferRepository.save(offer);

    List<JobOffer> results = jobOfferRepository.findByOfferedSalaryIsNotNull();
    assertThat(results).isNotEmpty();
    assertThat(results.get(0).getOfferedSalary()).isNotNull();
}

@Test
@DisplayName("Find offers with expected salary greater than and offered salary less than")
void testFindByExpectedSalaryGreaterThanAndOfferedSalaryLessThan() {
    JobApplication application = buildApplication();
    JobOffer offer = new JobOffer(LocalDate.now(), JobOfferStatus.PENDING, application);
    offer.setExpectedSalary(6000.0);
    offer.setOfferedSalary(3000.0);
    jobOfferRepository.save(offer);

    List<JobOffer> results = jobOfferRepository.findByExpectedSalaryGreaterThanAndOfferedSalaryLessThan(5000.0, 4000.0);
    assertThat(results).isNotEmpty();
    assertThat(results.get(0).getExpectedSalary()).isGreaterThan(5000.0);
    assertThat(results.get(0).getOfferedSalary()).isLessThan(4000.0);
}    
}
