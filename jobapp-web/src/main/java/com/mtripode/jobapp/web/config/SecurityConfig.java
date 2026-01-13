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
            .cors().and() // usa tu WebConfig para CORS
            .csrf().disable() // desactiva CSRF para APIs REST
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/companies/**").permitAll() // permite acceso libre a /companies
                .requestMatchers("/candidates/**").permitAll() // permite acceso libre a /candidates
                .requestMatchers("/interview/**").permitAll() // permite acceso libre a /companies
                .requestMatchers("/applications/**").permitAll() // permite acceso libre a /candidates
                .requestMatchers("/job-offers/**").permitAll() // permite acceso libre a /companies
                .requestMatchers("/positions/**").permitAll() // permite acceso libre a /candidates
                .requestMatchers("/api/**").permitAll()       // permite acceso libre a tus endpoints REST
                .anyRequest().authenticated()                 // el resto requiere autenticación
            );
        return http.build();
    }
}