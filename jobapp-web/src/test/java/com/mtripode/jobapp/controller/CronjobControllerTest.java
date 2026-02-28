package com.mtripode.jobapp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mtripode.jobapp.service.service.CronJobApplicationService;

@ExtendWith(MockitoExtension.class)
class CronjobControllerTest {

    @Mock
    private CronJobApplicationService service;

    private CronjobController controller;

    @BeforeEach
    void setUp() {
        controller = new CronjobController(service);
    }

    @Test
    void cleanOldApplicationsAndOffersShouldReturnOk() {
        ResponseEntity<String> response = controller.cleanOldApplicationsAndOffers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(service).cleanOldApplicationsAndOffers();
    }

    @Test
    void cleanOldApplicationsAndOffersShouldReturnInternalServerError() {
        doThrow(new RuntimeException("boom")).when(service).cleanOldApplicationsAndOffers();

        ResponseEntity<String> response = controller.cleanOldApplicationsAndOffers();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("boom");
    }

    @Test
    void cleanJobOffersWithAppRejectedShouldReturnOkAndError() {
        ResponseEntity<String> ok = controller.cleanJobOffersWithAppRejected();
        assertThat(ok.getStatusCode()).isEqualTo(HttpStatus.OK);

        doThrow(new RuntimeException("error")).when(service).cleanJobOffersWithAppRejected();
        ResponseEntity<String> error = controller.cleanJobOffersWithAppRejected();
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(error.getBody()).contains("error");
    }
}
