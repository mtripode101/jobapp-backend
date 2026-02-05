package com.mtripode.jobapp.facade.facade.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.mtripode.jobapp.facade.dto.PositionDto;
import com.mtripode.jobapp.facade.facade.PositionFacade;
import com.mtripode.jobapp.facade.mapper.PositionMapper;
import com.mtripode.jobapp.service.model.Position;
import com.mtripode.jobapp.service.service.PositionService;

@Component
public class PositionFacadeImpl implements PositionFacade {

    private final PositionService positionService;
    private final PositionMapper positionMapper;

    public PositionFacadeImpl(PositionService positionService, PositionMapper positionMapper) {
        this.positionService = positionService;
        this.positionMapper = positionMapper;
    }

    @Override
    @CacheEvict(value = "positions", key = "#result.id")
    public PositionDto savePosition(PositionDto dto) {
        Position position = positionMapper.toEntity(dto);
        Position saved = positionService.savePosition(position);
        return positionMapper.toDto(saved);
    }

    @Override
    @Cacheable(value = "positions", key = "#id")
    public Optional<PositionDto> findById(Long id) {
        return positionService.findById(id).map(positionMapper::toDto);
    }

    @Override
    @Cacheable(value = "positions", key = "'all'")
    public List<PositionDto> findAll() {
        return positionService.findAll()
                .stream()
                .map(positionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = "positions", key = "#id")
    public void deleteById(Long id) {
        positionService.deleteById(id);
    }

    @Override
    @Cacheable(value = "positions", key = "'title:' + #title")
    public List<PositionDto> findByTitle(String title) {
        return positionService.findByTitle(title)
                .stream()
                .map(positionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "positions", key = "'titleContains:' + #keyword")
    public List<PositionDto> findByTitleContaining(String keyword) {
        return positionService.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(positionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "positions", key = "'location:' + #location")
    public List<PositionDto> findByLocation(String location) {
        return positionService.findByLocation(location)
                .stream()
                .map(positionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "positions", key = "'company:' + #companyName")
    public List<PositionDto> findByCompanyName(String companyName) {
        return positionService.findByCompanyName(companyName)
                .stream()
                .map(positionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "positions", key = "'withApplications'")
    public List<PositionDto> findWithApplications() {
        return positionService.findWithApplications()
                .stream()
                .map(positionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = {"positions", "jobs-applications"}, key = "#id")
    public PositionDto updatePosition(Long id, PositionDto dto) {
        Position position = positionMapper.toEntity(dto);
        Position updated = positionService.updatePosition(id, position);
        return positionMapper.toDto(updated);
    }
}
