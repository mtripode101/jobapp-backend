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
import com.mtripode.jobapp.service.service.JobOfferService;

@Component
public class InterviewFacadeImpl implements InterviewFacade {

    private static final Logger logger = LoggerFactory.getLogger(InterviewFacadeImpl.class);

    private final InterviewService interviewService;
    private final InterviewMapper interviewMapper;
    private final JobApplicationService jobApplicationService;
    private final JobOfferService jobOfferService;

    public InterviewFacadeImpl(InterviewService interviewService,
            InterviewMapper interviewMapper,
            JobApplicationService jobApplicationService,
            JobOfferService jobOfferService) {
        this.interviewService = interviewService;
        this.interviewMapper = interviewMapper;
        this.jobApplicationService = jobApplicationService;
        this.jobOfferService = jobOfferService;
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
    @CacheEvict(value = "interviews", key = "#id")
    public void deleteInterview(Long id) {
        logger.debug("deleteInterview - inicio. id={}", id);
        interviewService.deleteById(id);
        logger.info("deleteInterview - eliminado correctamente id={}", id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "interviews", allEntries = true)
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

        Interview interviewEntity = interviewMapper.toEntity(interviewDto);
        interviewEntity.setApplication(application);

        Interview saved = interviewService.saveInterview(interviewEntity);
        return interviewMapper.toDto(saved);
    }

    @Override
    @Transactional
    @CacheEvict(value = "interviews", allEntries = true)
    public void acceptOffer(Long offerId) {
        jobOfferService.acceptOffer(offerId);
    }

    @Override
    @Transactional
    @CacheEvict(value = "interviews", allEntries = true)
    public void rejectOffer(Long offerId) {
        jobOfferService.rejectOffer(offerId);
    }
}
