package com.mtripode.jobapp.service.service.impl;

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

import com.mtripode.jobapp.service.model.Candidate;
import com.mtripode.jobapp.service.repository.CandidateRepository;

@ExtendWith(MockitoExtension.class)
class CandidateServiceImpTest {

    @Mock
    private CandidateRepository candidateRepository;

    private CandidateServiceImp service;

    @BeforeEach
    void setUp() {
        service = new CandidateServiceImp(candidateRepository);
    }

    @Test
    void methodsShouldDelegateToRepository() {
        Candidate candidate = new Candidate();
        List<Candidate> expectedList = List.of(candidate);
        Optional<Candidate> expectedOptional = Optional.of(candidate);

        when(candidateRepository.save(candidate)).thenReturn(candidate);
        when(candidateRepository.findById(1L)).thenReturn(expectedOptional);
        when(candidateRepository.findAll()).thenReturn(expectedList);
        when(candidateRepository.findByFullName("Jane Doe")).thenReturn(expectedList);
        when(candidateRepository.findByContactInfoEmail("mail@test.com")).thenReturn(expectedOptional);
        when(candidateRepository.findByContactInfoPhone("123")).thenReturn(expectedList);
        when(candidateRepository.findByFullNameContainingIgnoreCase("jane")).thenReturn(expectedList);
        when(candidateRepository.findByApplicationsIsNotEmpty()).thenReturn(expectedList);
        when(candidateRepository.findCandidatesWithMoreThan(2)).thenReturn(expectedList);

        assertThat(service.saveCandidate(candidate)).isSameAs(candidate);
        assertThat(service.findById(1L)).isSameAs(expectedOptional);
        assertThat(service.findAll()).isSameAs(expectedList);
        service.deleteById(1L);
        assertThat(service.findByFullName("Jane Doe")).isSameAs(expectedList);
        assertThat(service.findByEmail("mail@test.com")).isSameAs(expectedOptional);
        assertThat(service.findByPhone("123")).isSameAs(expectedList);
        assertThat(service.findByFullNameContainingIgnoreCase("jane")).isSameAs(expectedList);
        assertThat(service.findWithApplications()).isSameAs(expectedList);
        assertThat(service.findCandidatesWithMoreThan(2)).isSameAs(expectedList);

        verify(candidateRepository).save(candidate);
        verify(candidateRepository).findById(1L);
        verify(candidateRepository).findAll();
        verify(candidateRepository).deleteById(1L);
        verify(candidateRepository).findByFullName("Jane Doe");
        verify(candidateRepository).findByContactInfoEmail("mail@test.com");
        verify(candidateRepository).findByContactInfoPhone("123");
        verify(candidateRepository).findByFullNameContainingIgnoreCase("jane");
        verify(candidateRepository).findByApplicationsIsNotEmpty();
        verify(candidateRepository).findCandidatesWithMoreThan(2);
    }
}
