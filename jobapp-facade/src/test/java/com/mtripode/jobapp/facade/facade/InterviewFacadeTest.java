package com.mtripode.jobapp.facade.facade;

import java.time.LocalDateTime;
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

import com.mtripode.jobapp.facade.dto.InterviewDto;
import com.mtripode.jobapp.facade.mapper.InterviewMapper;
import com.mtripode.jobapp.facade.facade.impl.InterviewFacadeImpl;
import com.mtripode.jobapp.service.model.Interview;
import com.mtripode.jobapp.service.service.InterviewService;
import com.mtripode.jobapp.service.model.Interview;

@ExtendWith(MockitoExtension.class)
class InterviewFacadeTest {

    @Mock
    private InterviewService interviewService;

    private InterviewMapper interviewMapper;
    private InterviewFacadeImpl interviewFacade;

    @BeforeEach
    void setUp() {
        interviewMapper = new InterviewMapper(); // real mapper instance
        interviewFacade = new InterviewFacadeImpl(interviewService, interviewMapper);
    }

    private Interview buildInterview() {
        Interview interview = new Interview();
        interview.setId(1L);
        interview.setScheduledAt(LocalDateTime.of(2026, 1, 10, 14, 0));
        return interview;
    }

    private InterviewDto buildInterviewDto() {
        InterviewDto dto = new InterviewDto();
        dto.setId(1L);
        dto.setScheduledAt(LocalDateTime.of(2026, 1, 10, 14, 0));
        return dto;
    }

    @Test
    @DisplayName("Get all interviews should return list of DTOs")
    void testGetAllInterviews() {
        Interview interview = buildInterview();
        when(interviewService.findAll()).thenReturn(List.of(interview));

        List<InterviewDto> results = interviewFacade.getAllInterviews();

        assertThat(results).hasSize(1);

        verify(interviewService, times(1)).findAll();
    }

    @Test
    @DisplayName("Get interview by ID should return Optional DTO")
    void testGetInterviewById() {
        Interview interview = buildInterview();
        when(interviewService.findById(1L)).thenReturn(Optional.of(interview));

        Optional<InterviewDto> result = interviewFacade.getInterviewById(1L);

        assertThat(result).isPresent();
        verify(interviewService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Save interview should persist and return DTO")
    void testSaveInterview() {
        Interview interview = buildInterview();
        InterviewDto dto = buildInterviewDto();

        when(interviewService.saveInterview(any(Interview.class))).thenReturn(interview);

        InterviewDto saved = interviewFacade.saveInterview(dto);

        assertThat(saved).isNotNull();
        verify(interviewService, times(1)).saveInterview(any(Interview.class));
    }

    @Test
    @DisplayName("Delete interview should call service delete")
    void testDeleteInterview() {
        interviewFacade.deleteInterview(1L);
        verify(interviewService, times(1)).deleteById(1L);
    }
}