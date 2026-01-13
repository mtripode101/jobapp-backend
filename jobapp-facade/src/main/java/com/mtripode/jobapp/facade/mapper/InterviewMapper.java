package com.mtripode.jobapp.facade.mapper;

import org.springframework.stereotype.Component;

import com.mtripode.jobapp.facade.dto.InterviewDto;
import com.mtripode.jobapp.service.model.Interview;

@Component
public class InterviewMapper {

    public InterviewDto toDto(Interview interview) {
        if (interview == null) {
            return null;
        }
        return new InterviewDto(
                interview.getScheduledAt(),
                interview.getType(),
                interview.getFeedback()
        );
    }

    public Interview toEntity(InterviewDto dto) {
        if (dto == null) {
            return null;
        }
        return new Interview(
                dto.getScheduledAt(),
                dto.getType(),
                dto.getFeedback()
        );
    }
}