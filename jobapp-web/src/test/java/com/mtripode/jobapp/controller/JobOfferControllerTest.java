package com.mtripode.jobapp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mtripode.jobapp.facade.dto.JobOfferDTO;
import com.mtripode.jobapp.facade.facade.impl.JobOfferFacadeImpl;
import com.mtripode.jobapp.service.model.JobOfferStatus;

class JobOfferControllerTest {

    private StubJobOfferFacade facade;
    private JobOfferController controller;

    @BeforeEach
    void setUp() {
        facade = new StubJobOfferFacade();
        controller = new JobOfferController(facade);
    }

    @Test
    void shouldGetOfferByIdAndAllOffers() {
        JobOfferDTO dto = offer(1L);
        facade.all = List.of(dto);
        facade.byId = Optional.of(dto);

        assertThat(controller.getAllOffers()).hasSize(1);
        assertThat(controller.getOfferById(1L).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldReturnNotFoundWhenOfferMissing() {
        facade.byId = Optional.empty();

        ResponseEntity<JobOfferDTO> response = controller.getOfferById(9L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateUpdateDeleteAcceptRejectAndFilterByStatus() {
        JobOfferDTO dto = offer(2L);
        facade.created = dto;
        facade.updated = dto;
        facade.accepted = dto;
        facade.rejected = dto;
        facade.byStatus = List.of(dto);

        JobOfferDTO created = controller.createOffer(100L, LocalDate.now(), JobOfferStatus.PENDING);
        ResponseEntity<JobOfferDTO> updated = controller.updateOffer(2L, offer(null));
        ResponseEntity<Void> deleted = controller.deleteOffer(2L);
        ResponseEntity<JobOfferDTO> accepted = controller.acceptOffer(2L);
        ResponseEntity<JobOfferDTO> rejected = controller.rejectOffer(2L);

        assertThat(created).isSameAs(dto);
        assertThat(updated.getBody()).isSameAs(dto);
        assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(accepted.getBody()).isSameAs(dto);
        assertThat(rejected.getBody()).isSameAs(dto);
        assertThat(controller.getOffersByStatus(JobOfferStatus.PENDING)).hasSize(1);
    }

    @Test
    void shouldDelegateSalaryQueries() {
        JobOfferDTO dto = offer(3L);
        List<JobOfferDTO> expected = List.of(dto);
        facade.salaryResult = expected;

        assertThat(controller.findByExpectedSalaryGreaterThan(1000.0)).isSameAs(expected);
        assertThat(controller.findByOfferedSalaryLessThan(5000.0)).isSameAs(expected);
        assertThat(controller.findByExpectedSalaryBetween(1000.0, 2000.0)).isSameAs(expected);
        assertThat(controller.findByOfferedSalaryBetween(1000.0, 2000.0)).isSameAs(expected);
        assertThat(controller.findByExpectedSalaryIsNull()).isSameAs(expected);
        assertThat(controller.findByOfferedSalaryIsNull()).isSameAs(expected);
        assertThat(controller.findByExpectedSalaryIsNotNull()).isSameAs(expected);
        assertThat(controller.findByOfferedSalaryIsNotNull()).isSameAs(expected);
        assertThat(controller.findByExpectedSalaryGreaterThanAndOfferedSalaryLessThan(10.0, 20.0)).isSameAs(expected);
        assertThat(controller.findByExpectedSalaryLessThanAndOfferedSalaryGreaterThan(20.0, 10.0)).isSameAs(expected);
        assertThat(controller.findByExpectedSalaryEqualsOfferedSalary()).isSameAs(expected);
    }

    private static JobOfferDTO offer(Long id) {
        JobOfferDTO dto = new JobOfferDTO();
        dto.setId(id);
        return dto;
    }

    static class StubJobOfferFacade extends JobOfferFacadeImpl {

        List<JobOfferDTO> all = List.of();
        Optional<JobOfferDTO> byId = Optional.empty();
        JobOfferDTO created;
        JobOfferDTO updated;
        JobOfferDTO accepted;
        JobOfferDTO rejected;
        List<JobOfferDTO> byStatus = List.of();
        List<JobOfferDTO> salaryResult = List.of();

        StubJobOfferFacade() {
            super(null);
        }

        @Override
        public List<JobOfferDTO> getAllOffers() {
            return all;
        }

        @Override
        public Optional<JobOfferDTO> getOfferById(Long id) {
            return byId;
        }

        @Override
        public JobOfferDTO createOffer(Long applicationId, LocalDate offeredAt, JobOfferStatus status) {
            return created;
        }

        @Override
        public JobOfferDTO updateOffer(JobOfferDTO dto) {
            return updated;
        }

        @Override
        public void deleteOffer(Long id) {
        }

        @Override
        public List<JobOfferDTO> getOffersByStatus(JobOfferStatus status) {
            return byStatus;
        }

        @Override
        public JobOfferDTO acceptOffer(Long id) {
            return accepted;
        }

        @Override
        public JobOfferDTO rejectOffer(Long id) {
            return rejected;
        }

        @Override
        public List<JobOfferDTO> findByExpectedSalaryGreaterThan(Double salary) {
            return salaryResult;
        }

        @Override
        public List<JobOfferDTO> findByOfferedSalaryLessThan(Double salary) {
            return salaryResult;
        }

        @Override
        public List<JobOfferDTO> findByExpectedSalaryBetween(Double minSalary, Double maxSalary) {
            return salaryResult;
        }

        @Override
        public List<JobOfferDTO> findByOfferedSalaryBetween(Double minSalary, Double maxSalary) {
            return salaryResult;
        }

        @Override
        public List<JobOfferDTO> findByExpectedSalaryIsNull() {
            return salaryResult;
        }

        @Override
        public List<JobOfferDTO> findByOfferedSalaryIsNull() {
            return salaryResult;
        }

        @Override
        public List<JobOfferDTO> findByExpectedSalaryIsNotNull() {
            return salaryResult;
        }

        @Override
        public List<JobOfferDTO> findByOfferedSalaryIsNotNull() {
            return salaryResult;
        }

        @Override
        public List<JobOfferDTO> findByExpectedSalaryGreaterThanAndOfferedSalaryLessThan(Double expectedMin, Double offeredMax) {
            return salaryResult;
        }

        @Override
        public List<JobOfferDTO> findByExpectedSalaryLessThanAndOfferedSalaryGreaterThan(Double expectedMax, Double offeredMin) {
            return salaryResult;
        }

        @Override
        public List<JobOfferDTO> findByExpectedSalaryEqualsOfferedSalary() {
            return salaryResult;
        }
    }
}
