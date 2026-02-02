package com.mtripode.jobapp.facade.facade.impl;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
    @CacheEvict(value = "jobs-applications", key = "#dto.jobId",  allEntries = true)
    public JobApplicationDto applyToJob(JobApplicationDto dto) {
        JobApplication entity = jobApplicationMapper.toEntity(dto);
        JobApplication saved = jobApplicationService.applyToJob(
                entity.getSourceLink(),
                entity.getWebsiteSource(),
                entity.getDescription(),
                entity.getCandidate(),
                entity.getCompany(),
                entity.getPosition(),
                entity.getJobId()
        );
        return jobApplicationMapper.toDto(saved);
    }

    @Override
    @CacheEvict(value = "jobs-applications", key = "#dto.jobId",  allEntries = true)
    public JobApplicationDto applyRejected(JobApplicationDto dto) {
        JobApplication entity = jobApplicationMapper.toEntity(dto);
        JobApplication saved = jobApplicationService.applyRejected(
                entity.getSourceLink(),
                entity.getWebsiteSource(),
                entity.getDescription(),
                entity.getCandidate(),
                entity.getCompany(),
                entity.getPosition(),
                entity.getJobId()
        );
        return jobApplicationMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "jobs-applications", key = "#id")
    public Optional<JobApplicationDto> findById(Long id) {
        return jobApplicationService.findById(id).map(jobApplicationMapper::toDto);
    }

    @Override
    @Cacheable(value = "jobs-applications", key = "'all'")
    public List<JobApplicationDto> findAll() {
        return jobApplicationService.listAll()
                .stream()
                .map(jobApplicationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompletableFuture<List<JobApplicationDto>> findAllAsync() {
        // No cache aquí: Spring Cache no maneja bien CompletableFuture
        return jobApplicationService.listAllAsync()
                .thenApply(list -> list.stream()
                .map(jobApplicationMapper::toDto)
                .collect(Collectors.toList()));
    }

    @Override
    @CacheEvict(value = "jobs-applications", key = "#id",  allEntries = true)
    public void deleteById(Long id) {
        jobApplicationService.deleteById(id);
    }

    @Override
    @CacheEvict(value = "jobs-applications", key = "#id",  allEntries = true)
    public JobApplicationDto updateStatus(Long id, String newStatus) {
        JobApplication updated = jobApplicationService.updateStatus(id, Status.valueOf(newStatus));
        return jobApplicationMapper.toDto(updated);
    }

    @Override
    @Cacheable(value = "jobs-applications", key = "'status:' + #status")
    public List<JobApplicationDto> findByStatus(String status) {
        return jobApplicationService.listByStatus(Status.valueOf(status))
                .stream()
                .map(jobApplicationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "jobs-applications", key = "'company:' + #companyName")
    public List<JobApplicationDto> findByCompanyName(String companyName) {
        return jobApplicationService.listByCompanyName(companyName)
                .stream()
                .map(jobApplicationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "jobs-applications", key = "'candidate:' + #fullName")
    public List<JobApplicationDto> findByCandidateFullName(String fullName) {
        return jobApplicationService.listByCandidateFullName(fullName)
                .stream()
                .map(jobApplicationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "jobs-applications", key = "'position:' + #title")
    public List<JobApplicationDto> findByPositionTitle(String title) {
        return jobApplicationService.listByPositionTitle(title)
                .stream()
                .map(jobApplicationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "jobs-applications", key = "#jobId")
    public JobApplicationDto findByJobId(String jobId) {
        JobApplication application = jobApplicationService.findByJobId(jobId);
        return jobApplicationMapper.toDto(application);
    }

    @Override
    //@Cacheable(value = "jobs-applications", key = "'page:' + #pageable.pageNumber")
    public Page<JobApplicationDto> findAll(Pageable pageable) {
        Page<JobApplication> page = jobApplicationService.listAll(pageable);

         List<JobApplicationDto> dtos = page.getContent()
                .stream()
                .map(jobApplicationMapper::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    
    
}
