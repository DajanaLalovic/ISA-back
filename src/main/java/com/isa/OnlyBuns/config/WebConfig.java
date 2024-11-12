package com.isa.OnlyBuns.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Value("${upload.path}") // Preuzima vrednost upload.path iz application.properties
    private String uploadDir;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:8081") // Dodaj adresu svog frontend servera
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Koristi uploadDir kao putanju za dinamičko učitavanje slika
                registry.addResourceHandler("/images/**")
                        .addResourceLocations("file:" + uploadDir + "/") // koristi konfigurabilnu vrednost
                        .setCachePeriod(0); // Onemogućava keširanje kako bi slike bile odmah dostupne
            }
        };
    }
}
