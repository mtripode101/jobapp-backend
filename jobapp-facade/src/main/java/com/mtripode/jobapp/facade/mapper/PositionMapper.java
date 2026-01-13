package com.mtripode.jobapp.facade.mapper;

import org.springframework.stereotype.Component;

import com.mtripode.jobapp.facade.dto.PositionDto;
import com.mtripode.jobapp.service.model.Position;
import com.mtripode.jobapp.service.service.CompanyService;

@Component
public class PositionMapper {

    private final CompanyService companyService;

    public PositionMapper(CompanyService companyService) {
        this.companyService = companyService;
    }

    public PositionDto toDto(Position position) {
        if (position == null) return null;
        return new PositionDto(
                position.getId(),
                position.getTitle(),
                position.getLocation(),
                position.getDescription(),
                position.getCompany() != null ? position.getCompany().getName() : null
        );
    }

    public Position toEntity(PositionDto dto) {
        if (dto == null) return null;
        Position position = new Position();
        position.setId(dto.getId());
        position.setTitle(dto.getTitle());
        position.setLocation(dto.getLocation());
        position.setDescription(dto.getDescription());

        // Resolver la compañía a partir del nombre
        if (dto.getCompanyName() != null) {
            companyService.getCompanyByName(dto.getCompanyName())
                          .ifPresent(position::setCompany);
        }

        return position;
    }
}