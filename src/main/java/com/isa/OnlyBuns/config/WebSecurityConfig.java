package com.isa.OnlyBuns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Isključuje CSRF zaštitu koristeći lambda izraz za Spring Security 6.1+
                .authorizeRequests(auth -> auth.anyRequest().permitAll()); // Dozvoljava pristup svim zahtevima bez autentifikacije

        return http.build();
    }
}
