package com.mtripode.jobapp.service.service;

public interface CronJobApplicationService {

    void cleanOldApplicationsAndOffers();

    void cleanJobOffersWithAppRejected();
}
