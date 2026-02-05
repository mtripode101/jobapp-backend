package com.mtripode.jobapp.facade.facade;

import java.util.List;
import java.util.Optional;

import com.mtripode.jobapp.facade.dto.InterviewDto;

public interface InterviewFacade {

    List<InterviewDto> getAllInterviews();

    Optional<InterviewDto> getInterviewById(Long id);

    void deleteInterview(Long id);

    InterviewDto saveInterview(InterviewDto interviewDto);

}
