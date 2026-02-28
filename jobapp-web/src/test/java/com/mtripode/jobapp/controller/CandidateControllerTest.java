package com.mtripode.jobapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mtripode.jobapp.facade.dto.CandidateDto;
import com.mtripode.jobapp.facade.facade.CandidateFacade;
import com.mtripode.jobapp.metrics.CandidateMetrics;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@ExtendWith(MockitoExtension.class)
class CandidateControllerTest {

    @Mock
    private CandidateFacade candidateFacade;

    private SimpleMeterRegistry registry;
    private CandidateController controller;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        CandidateMetrics candidateMetrics = new CandidateMetrics(registry);
        controller = new CandidateController(candidateFacade, candidateMetrics);
    }

    @Test
    void shouldGetAllCandidatesAndIncrementSearchMetric() {
        List<CandidateDto> expected = List.of(buildCandidate(1L, "John"));
        when(candidateFacade.getAllCandidates()).thenReturn(expected);

        List<CandidateDto> result = controller.getAllCandidates();

        assertThat(result).isSameAs(expected);
        assertThat(counter("jobapp.candidate.search.count")).isEqualTo(1.0);
    }

    @Test
    void shouldGetCandidateByIdWhenExists() {
        CandidateDto dto = buildCandidate(2L, "Jane");
        when(candidateFacade.getCandidateById(2L)).thenReturn(Optional.of(dto));

        ResponseEntity<CandidateDto> response = controller.getCandidateById(2L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(dto);
        assertThat(counter("jobapp.candidate.search.count")).isEqualTo(1.0);
    }

    @Test
    void shouldReturnNotFoundWhenCandidateByIdDoesNotExist() {
        when(candidateFacade.getCandidateById(99L)).thenReturn(Optional.empty());

        ResponseEntity<CandidateDto> response = controller.getCandidateById(99L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(counter("jobapp.candidate.search.count")).isEqualTo(1.0);
    }

    @Test
    void shouldCreateUpdateAndDeleteCandidateAndIncrementMetrics() {
        CandidateDto input = buildCandidate(null, "User");
        CandidateDto created = buildCandidate(10L, "User");
        CandidateDto updated = buildCandidate(10L, "User Updated");
        when(candidateFacade.createCandidate(input)).thenReturn(created);
        when(candidateFacade.updateCandidate(10L, input)).thenReturn(updated);

        CandidateDto createdResult = controller.createCandidate(input);
        CandidateDto updatedResult = controller.updateCandidate(10L, input);
        ResponseEntity<Void> deleteResponse = controller.deleteCandidate(10L);

        assertThat(createdResult).isSameAs(created);
        assertThat(updatedResult).isSameAs(updated);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(counter("jobapp.candidate.create.count")).isEqualTo(1.0);
        assertThat(counter("jobapp.candidate.update.count")).isEqualTo(1.0);
        assertThat(counter("jobapp.candidate.delete.count")).isEqualTo(1.0);
        verify(candidateFacade).deleteCandidate(10L);
    }

    @Test
    void shouldDelegateSearchEndpoints() {
        CandidateDto dto = buildCandidate(3L, "Martin");
        when(candidateFacade.findByFullName("Martin")).thenReturn(List.of(dto));
        when(candidateFacade.findByEmail("m@test.com")).thenReturn(Optional.of(dto));
        when(candidateFacade.findByPhone("123")).thenReturn(List.of(dto));
        when(candidateFacade.findByFullNameContaining("mar")).thenReturn(List.of(dto));
        when(candidateFacade.findWithApplications()).thenReturn(List.of(dto));
        when(candidateFacade.findCandidatesWithMoreThan(2)).thenReturn(List.of(dto));

        assertThat(controller.findByFullName("Martin")).hasSize(1);
        assertThat(controller.findByEmail("m@test.com").getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(controller.findByPhone("123")).hasSize(1);
        assertThat(controller.findByFullNameContaining("mar")).hasSize(1);
        assertThat(controller.findWithApplications()).hasSize(1);
        assertThat(controller.findCandidatesWithMoreThan(2)).hasSize(1);
        assertThat(counter("jobapp.candidate.search.count")).isEqualTo(6.0);
    }

    private CandidateDto buildCandidate(Long id, String name) {
        CandidateDto dto = new CandidateDto();
        dto.setId(id);
        dto.setFullName(name);
        return dto;
    }

    private double counter(String name) {
        return registry.find(name).counter().count();
    }
}
