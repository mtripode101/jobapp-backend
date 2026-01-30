package com.mtripode.jobapp.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos de la API
                .requestMatchers("/companies/**").permitAll()
                .requestMatchers("/candidates/**").permitAll()
                .requestMatchers("/interview/**").permitAll()
                .requestMatchers("/applications/**").permitAll()
                .requestMatchers("/job-offers/**").permitAll()
                .requestMatchers("/positions/**").permitAll()
                .requestMatchers("/api/**").permitAll()
                .requestMatchers("/excelimport/**").permitAll() 

                // Actuator: permitir health e info públicamente en dev
                //.requestMatchers("/actuator/health", "/actuator/info").permitAll()

                // Si quieres exponer todos los actuator en dev (menos recomendado en prod)
                 .requestMatchers("/actuator/**").permitAll()

                // El resto requiere autenticación
                .anyRequest().authenticated()
            );
        return http.build();
    }
}