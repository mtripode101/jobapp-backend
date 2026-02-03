package com.mtripode.jobapp.facade.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.mtripode.jobapp.facade.dto.CandidateDto;
import com.mtripode.jobapp.facade.dto.CompanyDto;
import com.mtripode.jobapp.facade.dto.InterviewDto;
import com.mtripode.jobapp.facade.dto.JobApplicationDto;
import com.mtripode.jobapp.facade.dto.JobOfferDTO;
import com.mtripode.jobapp.facade.dto.PositionDto;
import com.mtripode.jobapp.service.model.Candidate;
import com.mtripode.jobapp.service.model.Company;
import com.mtripode.jobapp.service.model.ContactInfo;
import com.mtripode.jobapp.service.model.Interview;
import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.JobOffer;
import com.mtripode.jobapp.service.model.Position;
import com.mtripode.jobapp.service.model.Status;

@Component
public class JobApplicationMapper {

    public JobApplicationDto toDto(JobApplication entity) {
        if (entity == null) {
            return null;
        }

        JobApplicationDto dto = new JobApplicationDto();
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setSourceLink(entity.getSourceLink());
        dto.setWebsiteSource(entity.getWebsiteSource());
        dto.setDescription(entity.getDescription());
        dto.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        dto.setJobId(entity.getJobId());
        dto.setDateApplied(entity.getDateApplied());
        List<JobOffer> entityOffers = entity.getOffers();
        List<JobOfferDTO> dtoOffers = entityOffers.stream().map(JobOfferMapper::toDTO).collect(Collectors.toList());
        dto.setOffers(dtoOffers);

        List<Interview> entityInterviews = entity.getInterviews();
        List<InterviewDto> dtoInterviews = entityInterviews.stream().map(InterviewMapper::toDto).collect(Collectors.toList());
        dto.setInterviews(dtoInterviews);

        // Candidate mapping
        if (entity.getCandidate() != null) {
            Candidate candidate = entity.getCandidate();
            CandidateDto candidateDto = new CandidateDto();
            candidateDto.setId(candidate.getId());
            candidateDto.setFullName(candidate.getFullName());

            if (candidate.getContactInfo() != null) {
                candidateDto.setEmail(candidate.getContactInfo().getEmail());
                candidateDto.setPhone(candidate.getContactInfo().getPhone());
                candidateDto.setLinkedIn(candidate.getContactInfo().getLinkedIn());
                candidateDto.setGithub(candidate.getContactInfo().getGithub());
            }

            dto.setCandidate(candidateDto);
        }

        // Company mapping
        if (entity.getCompany() != null) {
            Company company = entity.getCompany();
            CompanyDto companyDto = new CompanyDto();
            companyDto.setId(company.getId());
            companyDto.setName(company.getName());
            companyDto.setWebsite(company.getWebsite());
            companyDto.setDescription(company.getDescription());
            dto.setCompany(companyDto);
        }

        // Position mapping
        if (entity.getPosition() != null) {
            Position position = entity.getPosition();
            PositionDto positionDto = new PositionDto();
            positionDto.setId(position.getId());
            positionDto.setTitle(position.getTitle());
            positionDto.setLocation(position.getLocation());
            positionDto.setDescription(position.getDescription());
            positionDto.setCompanyName(position.getCompany() != null ? position.getCompany().getName() : null);
            dto.setPosition(positionDto);
        }

        return dto;
    }

    public JobApplication toEntity(JobApplicationDto dto) {
        if (dto == null) {
            return null;
        }

        JobApplication entity = new JobApplication();
        entity.setId(dto.getId());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setSourceLink(dto.getSourceLink());
        entity.setWebsiteSource(dto.getWebsiteSource());
        entity.setDescription(dto.getDescription());
        entity.setJobId(dto.getJobId());
        entity.setDateApplied(dto.getDateApplied());

        List<JobOfferDTO> offerDTOs = dto.getOffers();
        List<JobOffer> entityOffers = offerDTOs.stream().map(JobOfferMapper::toEntity).collect(Collectors.toList());
        entity.setOffers(entityOffers);

        List<InterviewDto> interviewDtos = dto.getInterviews();
        List<Interview> entityInterviews = interviewDtos.stream().map(InterviewMapper::toEntity).collect(Collectors.toList());
        entity.setInterviews(entityInterviews);


        if (dto.getStatus() != null) {
            entity.setStatus(Status.fromDisplayName(dto.getStatus()));
        }

        // Candidate mapping
        if (dto.getCandidate() != null) {
            Candidate candidate = new Candidate();
            candidate.setId(dto.getCandidate().getId());
            candidate.setFullName(dto.getCandidate().getFullName());

            ContactInfo contactInfo = new ContactInfo();
            contactInfo.setEmail(dto.getCandidate().getEmail());
            contactInfo.setPhone(dto.getCandidate().getPhone());
            contactInfo.setLinkedIn(dto.getCandidate().getLinkedIn());
            contactInfo.setGithub(dto.getCandidate().getGithub());

            candidate.setContactInfo(contactInfo);
            entity.setCandidate(candidate);
        }

        // Company mapping
        if (dto.getCompany() != null) {
            Company company = new Company();
            company.setId(dto.getCompany().getId());
            company.setName(dto.getCompany().getName());
            company.setWebsite(dto.getCompany().getWebsite());
            company.setDescription(dto.getCompany().getDescription());
            entity.setCompany(company);
        }

        // Position mapping
        if (dto.getPosition() != null) {
            Position position = new Position();
            position.setId(dto.getPosition().getId());
            position.setTitle(dto.getPosition().getTitle());
            position.setLocation(dto.getPosition().getLocation());
            position.setDescription(dto.getPosition().getDescription());
            entity.setPosition(position);
        }

        return entity;
    }
}
