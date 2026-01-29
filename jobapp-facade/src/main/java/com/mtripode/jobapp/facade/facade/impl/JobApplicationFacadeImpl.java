package com.mtripode.jobapp.facade.facade.impl;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.mtripode.jobapp.facade.dto.JobApplicationDto;
import com.mtripode.jobapp.facade.facade.JobApplicationFacade;
import com.mtripode.jobapp.facade.mapper.JobApplicationMapper;
import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.Status;
import com.mtripode.jobapp.service.service.JobApplicationService;

@Component
public class JobApplicationFacadeImpl implements JobApplicationFacade {

    private final JobApplicationService jobApplicationService;
    private final JobApplicationMapper jobApplicationMapper;

    public JobApplicationFacadeImpl(JobApplicationService jobApplicationService,
            JobApplicationMapper jobApplicationMapper) {
        this.jobApplicationService = jobApplicationService;
        this.jobApplicationMapper = jobApplicationMapper;
    }

    @Override
    public JobApplicationDto applyToJob(JobApplicationDto dto) {
        JobApplication entity = jobApplicationMapper.toEntity(dto);
        JobApplication saved = jobApplicationService.applyToJob(
                entity.getSourceLink(),
                entity.getWebsiteSource(),
                entity.getDescription(),
                entity.getCandidate(),
                entity.getCompany(),
                entity.getPosition()
        );
        return jobApplicationMapper.toDto(saved);
    }

    @Override
    public JobApplicationDto applyRejected(JobApplicationDto dto) {
        JobApplication entity = jobApplicationMapper.toEntity(dto);
        JobApplication saved = jobApplicationService.applyRejected(
                entity.getSourceLink(),
                entity.getWebsiteSource(),
                entity.getDescription(),
                entity.getCandidate(),
                entity.getCompany(),
                entity.getPosition()
        );
        return jobApplicationMapper.toDto(saved);
    }

    @Override
    public Optional<JobApplicationDto> findById(Long id) {
        return jobApplicationService.findById(id).map(jobApplicationMapper::toDto);
    }

    @Override
    public List<JobApplicationDto> findAll() {
        return jobApplicationService.listAll()
                .stream()
                .map(jobApplicationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<List<JobApplicationDto>> findAllAsync() {
        return jobApplicationService.listAllAsync()
                .thenApply(list -> list.stream()
                .map(jobApplicationMapper::toDto)
                .collect(Collectors.toList()));
    }

    @Override
    public void deleteById(Long id) {
        jobApplicationService.deleteById(id);
    }

    @Override
    public JobApplicationDto updateStatus(Long id, String newStatus) {
        JobApplication updated = jobApplicationService.updateStatus(id, Status.valueOf(newStatus));
        return jobApplicationMapper.toDto(updated);
    }

    @Override
    public List<JobApplicationDto> findByStatus(String status) {
        return jobApplicationService.listByStatus(Status.valueOf(status))
                .stream()
                .map(jobApplicationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobApplicationDto> findByCompanyName(String companyName) {
        return jobApplicationService.listByCompanyName(companyName)
                .stream()
                .map(jobApplicationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobApplicationDto> findByCandidateFullName(String fullName) {
        return jobApplicationService.listByCandidateFullName(fullName)
                .stream()
                .map(jobApplicationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobApplicationDto> findByPositionTitle(String title) {
        return jobApplicationService.listByPositionTitle(title)
                .stream()
                .map(jobApplicationMapper::toDto)
                .collect(Collectors.toList());
    }
}
