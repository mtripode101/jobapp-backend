package com.mtripode.jobapp.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mtripode.jobapp.facade.dto.PositionDto;
import com.mtripode.jobapp.facade.facade.PositionFacade;
import com.mtripode.jobapp.facade.facade.impl.PositionFacadeImpl;

@RestController
@RequestMapping("/positions")
@CrossOrigin(origins = "http://localhost:3000") // habilita CORS solo para este controlador
public class PositionController {

    private final PositionFacade positionFacade;

    public PositionController(PositionFacadeImpl positionFacade) {
        this.positionFacade = positionFacade;
    }

    @PostMapping
    public ResponseEntity<PositionDto> createPosition(@RequestBody PositionDto dto) {
        return ResponseEntity.ok(positionFacade.savePosition(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PositionDto> getPositionById(@PathVariable Long id) {
        Optional<PositionDto> position = positionFacade.findById(id);
        return position.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<PositionDto> updatePosition(
            @PathVariable Long id,
            @RequestBody PositionDto dto) {
        PositionDto updated = positionFacade.updatePosition(id, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<PositionDto>> getAllPositions() {
        return ResponseEntity.ok(positionFacade.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePosition(@PathVariable Long id) {
        positionFacade.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/title")
    public ResponseEntity<List<PositionDto>> getByTitle(@RequestParam String title) {
        return ResponseEntity.ok(positionFacade.findByTitle(title));
    }

    @GetMapping("/title/contains")
    public ResponseEntity<List<PositionDto>> getByTitleContaining(@RequestParam String keyword) {
        return ResponseEntity.ok(positionFacade.findByTitleContaining(keyword));
    }

    @GetMapping("/location")
    public ResponseEntity<List<PositionDto>> getByLocation(@RequestParam String location) {
        return ResponseEntity.ok(positionFacade.findByLocation(location));
    }

    @GetMapping("/company")
    public ResponseEntity<List<PositionDto>> getByCompanyName(@RequestParam String companyName) {
        return ResponseEntity.ok(positionFacade.findByCompanyName(companyName));
    }

    @GetMapping("/with-applications")
    public ResponseEntity<List<PositionDto>> getWithApplications() {
        return ResponseEntity.ok(positionFacade.findWithApplications());
    }
}
