package com.mtripode.jobapp.facade.facade;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mtripode.jobapp.facade.facade.impl.CandidateFacadeImpl;
import com.mtripode.jobapp.facade.mapper.CandidateMapper;
import com.mtripode.jobapp.service.model.Candidate;
import com.mtripode.jobapp.service.model.ContactInfo;
import com.mtripode.jobapp.service.service.CandidateService;
import com.mtripode.jobapp.facade.dto.CandidateDto;

@ExtendWith(MockitoExtension.class)
class CandidateFacadeTest {

    @Mock
    private CandidateService candidateService;

    private CandidateMapper candidateMapper;
    private CandidateFacadeImpl candidateFacade;

    @BeforeEach
    void setUp() {
        candidateMapper = new CandidateMapper(); // real instance
        candidateFacade = new CandidateFacadeImpl(candidateService, candidateMapper);
    }

    private Candidate buildCandidate() {
        ContactInfo contactInfo = new ContactInfo(
                "john.doe@example.com",
                "1234567890",
                "linkedin.com/johndoe",
                "github.com/johndoe"
        );
        Candidate candidate = new Candidate("John Doe", contactInfo);
        candidate.setId(1L);
        return candidate;
    }

    private CandidateDto buildCandidateDto() {
        CandidateDto dto = new CandidateDto();
        dto.setId(1L);
        dto.setFullName("John Doe");
        dto.setEmail("john.doe@example.com");
        dto.setPhone("1234567890");
        dto.setLinkedIn("linkedin.com/johndoe");
        dto.setGithub("github.com/johndoe");
        return dto;
    }

    @Test
    @DisplayName("Get all candidates should return list of DTOs")
    void testGetAllCandidates() {
        Candidate candidate = buildCandidate();
        when(candidateService.findAll()).thenReturn(List.of(candidate));

        List<CandidateDto> results = candidateFacade.getAllCandidates();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFullName()).isEqualTo("John Doe");
        verify(candidateService, times(1)).findAll();
    }

    @Test
    @DisplayName("Get candidate by ID should return Optional DTO")
    void testGetCandidateById() {
        Candidate candidate = buildCandidate();
        when(candidateService.findById(1L)).thenReturn(Optional.of(candidate));

        Optional<CandidateDto> result = candidateFacade.getCandidateById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
        verify(candidateService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Create candidate should persist and return DTO")
    void testCreateCandidate() {
        Candidate candidate = buildCandidate();
        CandidateDto dto = buildCandidateDto();

        when(candidateService.saveCandidate(any(Candidate.class))).thenReturn(candidate);

        CandidateDto saved = candidateFacade.createCandidate(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getFullName()).isEqualTo("John Doe");
        verify(candidateService, times(1)).saveCandidate(any(Candidate.class));
    }

    @Test
    @DisplayName("Update candidate should modify and return DTO")
    void testUpdateCandidate() {
        Candidate candidate = buildCandidate();
        CandidateDto dto = buildCandidateDto();

        when(candidateService.findById(1L)).thenReturn(Optional.of(candidate));
        when(candidateService.saveCandidate(candidate)).thenReturn(candidate);

        CandidateDto updated = candidateFacade.updateCandidate(1L, dto);

        assertThat(updated.getFullName()).isEqualTo("John Doe");
        verify(candidateService, times(1)).findById(1L);
        verify(candidateService, times(1)).saveCandidate(candidate);
    }

    @Test
    @DisplayName("Delete candidate should call service delete")
    void testDeleteCandidate() {
        candidateFacade.deleteCandidate(1L);
        verify(candidateService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Find candidates by full name should return list of DTOs")
    void testFindByFullName() {
        Candidate candidate = buildCandidate();
        when(candidateService.findByFullName("John Doe")).thenReturn(List.of(candidate));

        List<CandidateDto> results = candidateFacade.findByFullName("John Doe");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFullName()).isEqualTo("John Doe");
        verify(candidateService, times(1)).findByFullName("John Doe");
    }

    @Test
    @DisplayName("Find candidate by email should return Optional DTO")
    void testFindByEmail() {
        Candidate candidate = buildCandidate();
        when(candidateService.findByEmail("john.doe@example.com")).thenReturn(Optional.of(candidate));

        Optional<CandidateDto> result = candidateFacade.findByEmail("john.doe@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getFullName()).isEqualTo("John Doe");
        verify(candidateService, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    @DisplayName("Find candidates by phone should return list of DTOs")
    void testFindByPhone() {
        Candidate candidate = buildCandidate();
        when(candidateService.findByPhone("1234567890")).thenReturn(List.of(candidate));

        List<CandidateDto> results = candidateFacade.findByPhone("1234567890");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getPhone()).isEqualTo("1234567890");
        verify(candidateService, times(1)).findByPhone("1234567890");
    }

    @Test
    @DisplayName("Find candidates by keyword in name should return list of DTOs")
    void testFindByFullNameContaining() {
        Candidate candidate = buildCandidate();
        when(candidateService.findByFullNameContainingIgnoreCase("john")).thenReturn(List.of(candidate));

        List<CandidateDto> results = candidateFacade.findByFullNameContaining("john");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFullName()).containsIgnoringCase("john");
        verify(candidateService, times(1)).findByFullNameContainingIgnoreCase("john");
    }

    @Test
    @DisplayName("Find candidates with applications should return list of DTOs")
    void testFindWithApplications() {
        Candidate candidate = buildCandidate();
        when(candidateService.findWithApplications()).thenReturn(List.of(candidate));

        List<CandidateDto> results = candidateFacade.findWithApplications();

        assertThat(results).hasSize(1);
        verify(candidateService, times(1)).findWithApplications();
    }

    @Test
    @DisplayName("Find candidates with more than X applications should return list of DTOs")
    void testFindCandidatesWithMoreThan() {
        Candidate candidate = buildCandidate();
        when(candidateService.findCandidatesWithMoreThan(2)).thenReturn(List.of(candidate));

        List<CandidateDto> results = candidateFacade.findCandidatesWithMoreThan(2);

        assertThat(results).hasSize(1);
        verify(candidateService, times(1)).findCandidatesWithMoreThan(2);
    }
}