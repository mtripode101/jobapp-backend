package com.mtripode.jobapp.facade.facade.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.mtripode.jobapp.facade.dto.JobOfferDTO;
import com.mtripode.jobapp.facade.facade.JobOfferFacade;
import com.mtripode.jobapp.facade.mapper.JobOfferMapper;
import com.mtripode.jobapp.service.model.JobOfferStatus;
import com.mtripode.jobapp.service.service.JobOfferService;

@Component
public class JobOfferFacadeImpl implements JobOfferFacade {

    private final JobOfferService jobOfferService;

    public JobOfferFacadeImpl(JobOfferService jobOfferService) {
        this.jobOfferService = jobOfferService;
    }

    @Override
    public List<JobOfferDTO> getAllOffers() {
        return jobOfferService.findAll()
                .stream()
                .map(JobOfferMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<JobOfferDTO> getOfferById(Long id) {
        return jobOfferService.findById(id).map(JobOfferMapper::toDTO);
    }

    @Override
    public JobOfferDTO createOffer(Long applicationId, LocalDate offeredAt, JobOfferStatus status) {
        return JobOfferMapper.toDTO(jobOfferService.createOffer(applicationId, offeredAt, status));
    }

    @Override
    public JobOfferDTO updateOffer(JobOfferDTO dto) {
        return JobOfferMapper.toDTO(jobOfferService.saveJobOffer(JobOfferMapper.toEntity(dto)));
    }

    @Override
    public void deleteOffer(Long id) {
        jobOfferService.deleteById(id);
    }

    @Override
    public List<JobOfferDTO> getOffersByStatus(JobOfferStatus status) {
        return jobOfferService.findByStatus(status)
                .stream()
                .map(JobOfferMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public JobOfferDTO acceptOffer(Long id) {
        return JobOfferMapper.toDTO(jobOfferService.acceptOffer(id));
    }

    @Override
    public JobOfferDTO rejectOffer(Long id) {
        return JobOfferMapper.toDTO(jobOfferService.rejectOffer(id));
    }
}
