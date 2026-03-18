package com.mtripode.jobapp.service.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private final KafkaTemplate<String, JobAppEvent> kafkaTemplate;
    private final String applicationEventsTopic;

    public KafkaEventPublisher(
            KafkaTemplate<String, JobAppEvent> kafkaTemplate,
            @Value("${jobapp.kafka.topic.application-events:job.application.events}") String applicationEventsTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.applicationEventsTopic = applicationEventsTopic;
    }

    public void publishApplicationEvent(JobAppEvent event) {
        String key = event.getApplicationId() != null ? String.valueOf(event.getApplicationId()) : event.getEventId();
        kafkaTemplate.send(applicationEventsTopic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event {} to topic {}: {}",
                                event.getEventId(),
                                applicationEventsTopic,
                                ex.getMessage(),
                                ex);
                        return;
                    }

                    if (result != null && result.getRecordMetadata() != null) {
                        log.info("Published event {} to {}-{}@{}",
                                event.getEventId(),
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
