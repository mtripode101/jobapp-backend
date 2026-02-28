package com.mtripode.jobapp.service.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mtripode.jobapp.service.model.Interview;
import com.mtripode.jobapp.service.model.InterviewType;
import com.mtripode.jobapp.service.repository.InterviewRepository;

@ExtendWith(MockitoExtension.class)
class InterviewServiceImplTest {

    @Mock
    private InterviewRepository interviewRepository;

    private InterviewServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new InterviewServiceImpl(interviewRepository);
    }

    @Test
    void methodsShouldDelegateToRepository() {
        Interview interview = new Interview();
        LocalDateTime now = LocalDateTime.now();
        List<Interview> expected = List.of(interview);

        when(interviewRepository.save(interview)).thenReturn(interview);
        when(interviewRepository.findById(1L)).thenReturn(Optional.of(interview));
        when(interviewRepository.findAll()).thenReturn(expected);
        when(interviewRepository.findByType(InterviewType.PHONE)).thenReturn(expected);
        when(interviewRepository.findByScheduledAtAfter(now)).thenReturn(expected);
        when(interviewRepository.findByScheduledAtBefore(now)).thenReturn(expected);
        when(interviewRepository.findByFeedbackContainingIgnoreCase("good")).thenReturn(expected);
        when(interviewRepository.findByApplication_Id(5L)).thenReturn(expected);

        assertThat(service.saveInterview(interview)).isSameAs(interview);
        assertThat(service.findById(1L)).contains(interview);
        assertThat(service.findAll()).isSameAs(expected);
        service.deleteById(1L);
        assertThat(service.findByType(InterviewType.PHONE)).isSameAs(expected);
        assertThat(service.findByScheduledAtAfter(now)).isSameAs(expected);
        assertThat(service.findByScheduledAtBefore(now)).isSameAs(expected);
        assertThat(service.findByFeedbackContainingIgnoreCase("good")).isSameAs(expected);
        assertThat(service.findByApplicationId(5L)).isSameAs(expected);

        verify(interviewRepository).save(interview);
        verify(interviewRepository).findById(1L);
        verify(interviewRepository).findAll();
        verify(interviewRepository).deleteById(1L);
        verify(interviewRepository).findByType(InterviewType.PHONE);
        verify(interviewRepository).findByScheduledAtAfter(now);
        verify(interviewRepository).findByScheduledAtBefore(now);
        verify(interviewRepository).findByFeedbackContainingIgnoreCase("good");
        verify(interviewRepository).findByApplication_Id(5L);
    }
}
