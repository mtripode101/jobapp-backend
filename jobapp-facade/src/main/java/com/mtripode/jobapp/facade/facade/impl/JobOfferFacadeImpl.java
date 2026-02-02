package com.mtripode.jobapp.facade.facade.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @Cacheable(value = "job-offers", key = "'all'")
    public List<JobOfferDTO> getAllOffers() {
        return jobOfferService.findAll()
                .stream()
                .map(JobOfferMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "job-offers", key = "#id")
    public Optional<JobOfferDTO> getOfferById(Long id) {
        return jobOfferService.findById(id).map(JobOfferMapper::toDTO);
    }

    @Override
    @CacheEvict(value = "job-offers", allEntries = true)
    public JobOfferDTO createOffer(Long applicationId, LocalDate offeredAt, JobOfferStatus status) {
        return JobOfferMapper.toDTO(jobOfferService.createOffer(applicationId, offeredAt, status));
    }

    @Override
    @CacheEvict(value = "job-offers", key = "#dto.id",  allEntries = true)
    public JobOfferDTO updateOffer(JobOfferDTO dto) {
        var existing = jobOfferService.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Offer not found"));

        var entity = JobOfferMapper.toEntity(dto);

        if (Objects.isNull(entity.getApplication())) {
            entity.setApplication(existing.getApplication());
        }

        return JobOfferMapper.toDTO(jobOfferService.saveJobOffer(entity));
    }

    @Override
    @CacheEvict(value = "job-offers", key = "#id",  allEntries = true)
    public void deleteOffer(Long id) {
        jobOfferService.deleteById(id);
    }

    @Override
    @Cacheable(value = "job-offers", key = "'status:' + #status")
    public List<JobOfferDTO> getOffersByStatus(JobOfferStatus status) {
        return jobOfferService.findByStatus(status)
                .stream()
                .map(JobOfferMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "job-offers", key = "#id",  allEntries = true)
    public JobOfferDTO acceptOffer(Long id) {
        return JobOfferMapper.toDTO(jobOfferService.acceptOffer(id));
    }

    @Override
    @CacheEvict(value = "job-offers", key = "#id",  allEntries = true)
    public JobOfferDTO rejectOffer(Long id) {
        return JobOfferMapper.toDTO(jobOfferService.rejectOffer(id));
    }

    @Override
    public List<JobOfferDTO> findByExpectedSalaryGreaterThan(Double salary) {
        return jobOfferService.findByExpectedSalaryGreaterThan(salary)
                .stream()
                .map(JobOfferMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobOfferDTO> findByOfferedSalaryLessThan(Double salary) {
        return jobOfferService.findByOfferedSalaryLessThan(salary)
                .stream()
                .map(JobOfferMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobOfferDTO> findByExpectedSalaryBetween(Double minSalary, Double maxSalary) {
        return jobOfferService.findByExpectedSalaryBetween(minSalary, maxSalary)
                .stream()
                .map(JobOfferMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobOfferDTO> findByOfferedSalaryBetween(Double minSalary, Double maxSalary) {
        return jobOfferService.findByOfferedSalaryBetween(minSalary, maxSalary)
                .stream()
                .map(JobOfferMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobOfferDTO> findByExpectedSalaryIsNull() {
        return jobOfferService.findByExpectedSalaryIsNull()
                .stream()
                .map(JobOfferMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobOfferDTO> findByOfferedSalaryIsNull() {
        return jobOfferService.findByOfferedSalaryIsNull()
                .stream()
                .map(JobOfferMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobOfferDTO> findByExpectedSalaryIsNotNull() {
        return jobOfferService.findByExpectedSalaryIsNotNull()
                .stream()
                .map(JobOfferMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobOfferDTO> findByOfferedSalaryIsNotNull() {
        return jobOfferService.findByOfferedSalaryIsNotNull()
                .stream()
                .map(JobOfferMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobOfferDTO> findByExpectedSalaryGreaterThanAndOfferedSalaryLessThan(Double expectedMin, Double offeredMax) {
        return jobOfferService.findByExpectedSalaryGreaterThanAndOfferedSalaryLessThan(expectedMin, offeredMax)
                .stream()
                .map(JobOfferMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobOfferDTO> findByExpectedSalaryLessThanAndOfferedSalaryGreaterThan(Double expectedMax, Double offeredMin) {
        return jobOfferService.findByExpectedSalaryLessThanAndOfferedSalaryGreaterThan(expectedMax, offeredMin)
                .stream()
                .map(JobOfferMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobOfferDTO> findByExpectedSalaryEqualsOfferedSalary() {
        return jobOfferService.findByExpectedSalaryEqualsOfferedSalary()
                .stream()
                .map(JobOfferMapper::toDTO)
                .collect(Collectors.toList());
    }

}
