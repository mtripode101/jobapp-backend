package com.mtripode.jobapp.facade.facade.impl;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mtripode.jobapp.facade.dto.InterviewDto;
import com.mtripode.jobapp.facade.facade.InterviewFacade;
import com.mtripode.jobapp.facade.mapper.InterviewMapper;
import com.mtripode.jobapp.service.model.Interview;
import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.service.InterviewService;
import com.mtripode.jobapp.service.service.JobApplicationService;

@Component
public class InterviewFacadeImpl implements InterviewFacade {

    private static final Logger logger = LoggerFactory.getLogger(InterviewFacadeImpl.class);

    private final InterviewService interviewService;
    private final JobApplicationService jobApplicationService;

    public InterviewFacadeImpl(InterviewService interviewService,
              JobApplicationService jobApplicationService) {
        this.interviewService = interviewService;
        this.jobApplicationService = jobApplicationService;
    }

    @Override
    @Cacheable(value = "interviews", key = "'all'")
    public List<InterviewDto> getAllInterviews() {
        logger.debug("getAllInterviews - inicio");
        List<Interview> entities = interviewService.findAll();
        logger.debug("getAllInterviews - {} entrevistas recuperadas desde el servicio", entities.size());
        return entities.stream()
                .map(InterviewMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "interviews", key = "#id")
    public Optional<InterviewDto> getInterviewById(Long id) {
        logger.debug("getInterviewById - inicio. id={}", id);
        return interviewService.findById(id).map(InterviewMapper::toDto);
    }

    @Override
    @CacheEvict(value = {"interviews", "jobs-applications"}, key = "#id")
    public void deleteInterview(Long id) {
        logger.debug("deleteInterview - inicio. id={}", id);
        interviewService.deleteById(id);
        logger.info("deleteInterview - eliminado correctamente id={}", id);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"interviews", "jobs-applications"}, allEntries = true)
    public InterviewDto saveInterview(InterviewDto interviewDto) {
        logger.debug("saveInterview - inicio. interviewDto={}", interviewDto);

        if (interviewDto == null) {
            throw new IllegalArgumentException("InterviewDto is required");
        }

        Long applicationId = interviewDto.getApplicationId();
        if (applicationId == null) {
            throw new IllegalArgumentException("applicationId is required");
        }

        JobApplication application = jobApplicationService.findById(applicationId)
                .orElseThrow(() -> new NoSuchElementException("JobApplication not found: id=" + applicationId));

        Interview interviewEntity = InterviewMapper.toEntity(interviewDto);
        interviewEntity.setApplication(application);

        Interview saved = interviewService.saveInterview(interviewEntity);
        return InterviewMapper.toDto(saved);
    }

}
