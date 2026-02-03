package com.mtripode.jobapp.service.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mtripode.jobapp.service.service.CronJobApplicationService;

@Service
public class JobApplicationCleanupJob {

    private final CronJobApplicationService cronJobApplicationService;

    public JobApplicationCleanupJob(CronJobApplicationService cronJobApplicationService) {
        this.cronJobApplicationService = cronJobApplicationService;
    }

    @Scheduled(cron = "0 0 2 * * ?") // todos los días a las 2 AM
    public void rejectOldApplications() {
        cronJobApplicationService.cleanOldApplicationsAndOffers();
    }

}