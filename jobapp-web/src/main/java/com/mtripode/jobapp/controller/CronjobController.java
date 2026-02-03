package com.mtripode.jobapp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mtripode.jobapp.service.service.CronJobApplicationService;

@RestController
@RequestMapping("/cronjob")
@CrossOrigin(origins = "http://localhost:3000") // habilita CORS solo para este controlador
public class CronjobController {

    private final CronJobApplicationService cronJobApplicationService;

    public CronjobController(CronJobApplicationService cronJobApplicationService) {
        this.cronJobApplicationService = cronJobApplicationService;
    }

    @PostMapping("/cleanOldApplicationsAndOffers")
    public ResponseEntity<String> cleanOldApplicationsAndOffers() {
        try {
            cronJobApplicationService.cleanOldApplicationsAndOffers();
            return ResponseEntity.ok("✅ Old applications and related offers cleaned successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("❌ Error while cleaning applications and offers: " + e.getMessage());
        }
    }

    @PostMapping("/cleanJobOffersWithAppRejected")
    public ResponseEntity<String> cleanJobOffersWithAppRejected() {
        try {
            cronJobApplicationService.cleanJobOffersWithAppRejected();
            return ResponseEntity.ok("✅ Old offers and related applicaton cleaned successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("❌ Error while cleaning applications and offers: " + e.getMessage());
        }
    }

}
