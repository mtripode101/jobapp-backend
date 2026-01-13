package com.mtripode.jobapp.facade.mapper;

import org.springframework.stereotype.Component;

import com.mtripode.jobapp.facade.dto.CandidateDto;
import com.mtripode.jobapp.service.model.Candidate;
import com.mtripode.jobapp.service.model.ContactInfo;

@Component
public class CandidateMapper {

    public CandidateDto toDto(Candidate candidate) {
        if (candidate == null) {
            return null;
        }

        CandidateDto dto = new CandidateDto();
        dto.setId(candidate.getId());
        dto.setFullName(candidate.getFullName());

        if (candidate.getContactInfo() != null) {
            dto.setEmail(candidate.getContactInfo().getEmail());
            dto.setPhone(candidate.getContactInfo().getPhone());
            dto.setLinkedIn(candidate.getContactInfo().getLinkedIn());
            dto.setGithub(candidate.getContactInfo().getGithub());
        }

        return dto;
    }

    public Candidate toEntity(CandidateDto dto) {
        if (dto == null) {
            return null;
        }

        Candidate candidate = new Candidate();
        candidate.setId(dto.getId());
        candidate.setFullName(dto.getFullName());

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setEmail(dto.getEmail());
        contactInfo.setPhone(dto.getPhone());
        contactInfo.setLinkedIn(dto.getLinkedIn());
        contactInfo.setGithub(dto.getGithub());

        candidate.setContactInfo(contactInfo);

        return candidate;
    }
}
