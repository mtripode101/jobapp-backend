package com.mtripode.jobapp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mtripode.jobapp.facade.dto.InterviewDto;
import com.mtripode.jobapp.facade.facade.impl.InterviewFacadeImpl;

class InterviewControllerTest {

    private StubInterviewFacade facade;
    private InterviewController controller;

    @BeforeEach
    void setUp() {
        facade = new StubInterviewFacade();
        controller = new InterviewController(facade);
    }

    @Test
    void shouldGetInterviewById() {
        InterviewDto dto = new InterviewDto();
        dto.setId(1L);
        facade.byId = Optional.of(dto);

        ResponseEntity<InterviewDto> response = controller.getInterviewById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(dto);
    }

    @Test
    void shouldReturnNotFoundWhenInterviewDoesNotExist() {
        facade.byId = Optional.empty();

        ResponseEntity<InterviewDto> response = controller.getInterviewById(2L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCreateUpdateListAndDeleteInterview() {
        InterviewDto dto = new InterviewDto();
        dto.setId(3L);
        facade.saved = dto;
        facade.all = List.of(dto);

        ResponseEntity<InterviewDto> createResponse = controller.createInterview(new InterviewDto());
        ResponseEntity<InterviewDto> updateResponse = controller.updateInterview(3L, new InterviewDto());
        List<InterviewDto> allResponse = controller.getAllInterviews();
        ResponseEntity<Void> deleteResponse = controller.deleteInterview(3L);

        assertThat(createResponse.getBody()).isSameAs(dto);
        assertThat(updateResponse.getBody()).isSameAs(dto);
        assertThat(allResponse).hasSize(1);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    static class StubInterviewFacade extends InterviewFacadeImpl {

        Optional<InterviewDto> byId = Optional.empty();
        List<InterviewDto> all = List.of();
        InterviewDto saved;

        StubInterviewFacade() {
            super(null, null);
        }

        @Override
        public Optional<InterviewDto> getInterviewById(Long id) {
            return byId;
        }

        @Override
        public List<InterviewDto> getAllInterviews() {
            return all;
        }

        @Override
        public InterviewDto saveInterview(InterviewDto interviewDto) {
            return saved;
        }

        @Override
        public void deleteInterview(Long id) {
        }
    }
}
