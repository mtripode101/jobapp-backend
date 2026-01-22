package com.mtripode.jobapp.metrics;

import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Contadores de negocio para CandidateController.
 */
@Component
public class CandidateMetrics {

    private final Counter createCounter;
    private final Counter searchCounter;
    private final Counter updateCounter;
    private final Counter deleteCounter;

    public CandidateMetrics(MeterRegistry registry) {
        this.createCounter = Counter.builder("jobapp.candidate.create.count")
            .description("Number of candidate creations")
            .register(registry);

        this.searchCounter = Counter.builder("jobapp.candidate.search.count")
            .description("Number of candidate searches")
            .register(registry);

        this.updateCounter = Counter.builder("jobapp.candidate.update.count")
            .description("Number of candidate updates")
            .register(registry);

        this.deleteCounter = Counter.builder("jobapp.candidate.delete.count")
            .description("Number of candidate deletions")
            .register(registry);
    }

    public void incrementCreate() { createCounter.increment(); }
    public void incrementSearch() { searchCounter.increment(); }
    public void incrementUpdate() { updateCounter.increment(); }
    public void incrementDelete() { deleteCounter.increment(); }
}