package com.mtripode.jobapp.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JobConfigProperties {
    
    @Value("${jobapp.reject.threshold.days}")
    private int rejectThresholdDays;

    public int getRejectThresholdDays() {
        return rejectThresholdDays;
    }
}
