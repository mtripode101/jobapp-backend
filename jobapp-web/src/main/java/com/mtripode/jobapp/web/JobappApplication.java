package com.mtripode.jobapp.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "com.mtripode.jobapp")
@EnableJpaRepositories(basePackages = "com.mtripode.jobapp.service.repository")
@EnableAsync
@EnableCaching
@EnableJpaAuditing
@EntityScan(basePackages = "com.mtripode.jobapp.service.model")
public class JobappApplication {
    public static void main(String[] args) {
        SpringApplication.run(JobappApplication.class, args);
    }
}