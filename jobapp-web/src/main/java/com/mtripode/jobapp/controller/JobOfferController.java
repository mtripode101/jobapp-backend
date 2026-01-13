package com.mtripode.jobapp.controller;

import java.time.LocalDate;
import java.util.List;

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

import com.mtripode.jobapp.facade.dto.JobOfferDTO;
import com.mtripode.jobapp.facade.facade.JobOfferFacade;
import com.mtripode.jobapp.facade.facade.impl.JobOfferFacadeImpl;
import com.mtripode.jobapp.service.model.JobOfferStatus;

@RestController
@RequestMapping("/job-offers")
@CrossOrigin(origins = "http://localhost:3000") // habilita CORS solo para este controlador
public class JobOfferController {

    private final JobOfferFacade jobOfferFacade;

    public JobOfferController(JobOfferFacadeImpl jobOfferFacade) {
        this.jobOfferFacade = jobOfferFacade;
    }

    @GetMapping
    public List<JobOfferDTO> getAllOffers() {
        return jobOfferFacade.getAllOffers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobOfferDTO> getOfferById(@PathVariable Long id) {
        return jobOfferFacade.getOfferById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public JobOfferDTO createOffer(@RequestParam Long applicationId,
            @RequestParam LocalDate offeredAt,
            @RequestParam JobOfferStatus status) {
        return jobOfferFacade.createOffer(applicationId, offeredAt, status);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobOfferDTO> updateOffer(@PathVariable Long id, @RequestBody JobOfferDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(jobOfferFacade.updateOffer(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        jobOfferFacade.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public List<JobOfferDTO> getOffersByStatus(@PathVariable JobOfferStatus status) {
        return jobOfferFacade.getOffersByStatus(status);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<JobOfferDTO> acceptOffer(@PathVariable Long id) {
        return ResponseEntity.ok(jobOfferFacade.acceptOffer(id));
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<JobOfferDTO> rejectOffer(@PathVariable Long id) {
        return ResponseEntity.ok(jobOfferFacade.rejectOffer(id));
    }
}
