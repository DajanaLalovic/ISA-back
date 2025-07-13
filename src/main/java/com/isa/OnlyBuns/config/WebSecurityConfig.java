package com.isa.OnlyBuns.config;
import com.isa.OnlyBuns.security.auth.RestAuthenticationEntryPoint;
import com.isa.OnlyBuns.security.auth.TokenAuthenticationFilter;
import com.isa.OnlyBuns.service.CustomUserDetailsService;
import com.isa.OnlyBuns.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class WebSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Autowired
    private TokenUtils tokenUtils;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(restAuthenticationEntryPoint))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.GET, "/images/**").permitAll() // Dopuštamo pristup slikama
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/socket/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/getOneUser/**").permitAll()
                        .requestMatchers("/api/locations/**").permitAll()
                        .anyRequest().authenticated() // Sve ostale zahteve traži autentifikacija
                )
                .addFilterBefore(new TokenAuthenticationFilter(tokenUtils, userDetailsService()), BasicAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider());

        return http.build();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(HttpMethod.GET, "/images/**") // Dopuštamo pristup slikama
                .requestMatchers(HttpMethod.GET, "/api/posts/likesCount/**")
                .requestMatchers(HttpMethod.POST, "/auth/login")
                .requestMatchers(HttpMethod.POST, "/auth/signup")
                .requestMatchers(HttpMethod.GET, "/auth/activate")
                .requestMatchers(HttpMethod.GET, "/api/profile/**")
                .requestMatchers(HttpMethod.GET, "/api/getOneUser/{id}")
                .requestMatchers(HttpMethod.GET, "/api/posts/all")// Allow POST requests for signup
                .requestMatchers(HttpMethod.GET, "api/getOneUser/{id}")
                .requestMatchers(HttpMethod.GET, "api/check-by-username/{username}")
                .requestMatchers("/socket/**")


                .requestMatchers(HttpMethod.GET, "/", "/webjars/**", "/*.html", "favicon.ico",
                        "/**.html", "/**.css", "/**.js");
    }
}
