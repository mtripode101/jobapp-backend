package com.mtripode.jobapp.service.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mtripode.jobapp.service.cache.CacheUtilService;
import com.mtripode.jobapp.service.config.JobConfigProperties;
import com.mtripode.jobapp.service.model.JobApplication;
import com.mtripode.jobapp.service.model.JobOffer;
import com.mtripode.jobapp.service.model.JobOfferStatus;
import com.mtripode.jobapp.service.model.Status;
import com.mtripode.jobapp.service.repository.JobApplicationRepository;
import com.mtripode.jobapp.service.repository.JobOfferRepository;
import com.mtripode.jobapp.service.service.CronJobApplicationService;
import com.mtripode.jobapp.service.service.JobOfferService;

import jakarta.transaction.Transactional;

@Service
public class CronJobApplicationServiceImpl implements CronJobApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(CronJobApplicationServiceImpl.class);

    private final JobOfferService jobOfferService;

    private final JobApplicationRepository jobApplicationRepository;
    private final JobOfferRepository jobOfferRepository;

    private final JobConfigProperties jobConfigProperties;
    private final CacheUtilService cacheUtilService;

    public CronJobApplicationServiceImpl(JobOfferService jobOfferService, JobApplicationRepository jobApplicationRepository,
            JobConfigProperties jobConfigProperties, CacheUtilService cacheUtilService, JobOfferRepository jobOfferRepository
    ) {
        this.jobOfferService = jobOfferService;
        this.jobApplicationRepository = jobApplicationRepository;
        this.jobConfigProperties = jobConfigProperties;
        this.cacheUtilService = cacheUtilService;
        this.jobOfferRepository = jobOfferRepository;
    }

    @Override
    @Transactional
    public void cleanOldApplicationsAndOffers() {
        int threshold = jobConfigProperties.getRejectThresholdDays();
        LocalDate cutoffDate = LocalDate.now().minusDays(threshold);

        List<JobApplication> oldApplications
                = jobApplicationRepository.findByStatusAndDateAppliedBefore(Status.APPLIED, cutoffDate);

        logger.info("Starttng cleanOldApplicationsAndOffers with " + oldApplications.size() + " elements from date older than " + cutoffDate.toString());
        int jobOffers = 0;
        for (JobApplication app : oldApplications) {
            app.setStatus(Status.REJECTED);
            app.setDateRejected(LocalDate.now());

            List<JobOffer> offers = jobOfferService.findByApplicationId(app.getId());
            for (JobOffer offer : offers) {
                offer.setStatus(JobOfferStatus.REJECTED);
                jobOfferService.saveJobOffer(offer);
                jobOffers++;
                logger.info("rejecting joboffer " + offer.getId() + " from application " + app.getJobId());
            }

            cacheUtilService.clearCache("job-offers");
            jobApplicationRepository.save(app);
        }
        logger.info("cleanOldApplicationsAndOffers ends succesfully change " + jobOffers + " offers to REJECTED");
    }

    @Override
    public void cleanJobOffersWithAppRejected() {
        List<JobOffer> pendingOffers = this.jobOfferRepository.findByStatus(JobOfferStatus.PENDING);

        List<JobOffer> pendingAndAppRejected = pendingOffers.stream().filter(o -> o.getApplication().getStatus().equals(Status.REJECTED)).collect(Collectors.toList());

        logger.info("cleanJobOffersWithAppRejected started with "+pendingAndAppRejected.size()+" joboffers with app rejected and current status is PENDING");
        for (JobOffer offer : pendingAndAppRejected) {
            offer.setStatus(JobOfferStatus.REJECTED);
            jobOfferService.saveJobOffer(offer);
            logger.info("cleanJobOffersWithAppRejected joboffer " + offer.getId() + " from application " + offer.getApplication().getJobId());
        }
        logger.info("cleanJobOffersWithAppRejected ends");
    }

}
