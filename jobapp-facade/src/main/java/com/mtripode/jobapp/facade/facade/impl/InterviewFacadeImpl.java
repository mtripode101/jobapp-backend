package com.mtripode.jobapp.facade.facade.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.mtripode.jobapp.facade.dto.InterviewDto;
import com.mtripode.jobapp.facade.facade.InterviewFacade;
import com.mtripode.jobapp.facade.mapper.InterviewMapper;
import com.mtripode.jobapp.service.model.Interview;
import com.mtripode.jobapp.service.service.InterviewService;

@Component
public class InterviewFacadeImpl implements InterviewFacade {

    private final InterviewService interviewService;
    private final InterviewMapper interviewMapper;

    public InterviewFacadeImpl(InterviewService interviewService, InterviewMapper interviewMapper) {
        this.interviewService = interviewService;
        this.interviewMapper = interviewMapper;
    }

    @Override
    public List<InterviewDto> getAllInterviews() {
        return interviewService.findAll()
                .stream()
                .map(interviewMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<InterviewDto> getInterviewById(Long id) {
        return interviewService.findById(id)
                .map(interviewMapper::toDto);
    }

    @Override
    public void deleteInterview(Long id) {
        interviewService.deleteById(id);
    }

    @Override
    public InterviewDto saveInterview(InterviewDto interviewDto) {
        Interview interview = interviewMapper.toEntity(interviewDto);
        Interview saved = interviewService.saveInterview(interview);
        return interviewMapper.toDto(saved);
    }
}
