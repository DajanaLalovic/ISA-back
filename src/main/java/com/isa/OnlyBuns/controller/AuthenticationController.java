package com.isa.OnlyBuns.controller;

import com.isa.OnlyBuns.config.ActiveUserMetricsConfig;
import com.isa.OnlyBuns.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.isa.OnlyBuns.dto.JwtAuthenticationRequest;
import com.isa.OnlyBuns.dto.UserDTO;
import com.isa.OnlyBuns.dto.UserTokenState;
import com.isa.OnlyBuns.model.User;
import com.isa.OnlyBuns.iservice.IUserService;
import com.isa.OnlyBuns.util.TokenUtils;

import java.time.LocalDateTime;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

//Kontroler zaduzen za autentifikaciju korisnika
@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private IUserService userService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private ActiveUserMetricsConfig activeUserMetricsConfig;

    private static final int MAX_ATTEMPTS = 5;
    private static final long TIME_WINDOW_MS = 60 * 1000; // 1 minute
    private final Map<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest,
                                                       HttpServletRequest request,
                                                       HttpServletResponse response) {
        String clientIp = request.getRemoteAddr();

        // Proveri broj pokušaja za IP adresu
        if (isBlocked(clientIp)) {
            return new ResponseEntity<>("Too many login attempts. Try again later.", HttpStatus.TOO_MANY_REQUESTS);
        }

        try {
            // Proveri da li korisnik postoji
            User user = userService.findByUsername(authenticationRequest.getUsername());

            if (user == null) {
                registerFailedAttempt(clientIp);
                return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            }

            // Proveri da li je korisnik aktiviran
            if (!user.getIsActive()) {
                return new ResponseEntity<>("Your account isn't activated yet. Check your mail.", HttpStatus.FORBIDDEN);
            }

            // Autentifikacija korisnika
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            activeUserMetricsConfig.userActivity(user.getId().intValue());
            // Uspešan login - resetuj pokušaje za IP
            resetAttempts(clientIp);

            // Generiši JWT token
            String jwt = tokenUtils.generateToken(user.getUsername());
            int expiresIn = tokenUtils.getExpiredIn();
            user.setLastLogin(LocalDateTime.now());
            userService.updateUser(user);
            return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));

        } catch (Exception e) {
            registerFailedAttempt(clientIp);
            return new ResponseEntity<>("Login failed", HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isBlocked(String ip) {
        LoginAttempt attempt = loginAttempts.get(ip);
        if (attempt == null) {
            return false;
        }
        if (attempt.getCount() >= MAX_ATTEMPTS && Instant.now().toEpochMilli() - attempt.getLastAttemptTime() < TIME_WINDOW_MS) {
            return true;
        }

        if (Instant.now().toEpochMilli() - attempt.getLastAttemptTime() >= TIME_WINDOW_MS) {
            resetAttempts(ip);
        }

        return false;
    }

    private void registerFailedAttempt(String ip) {
        loginAttempts.merge(ip, new LoginAttempt(1, Instant.now().toEpochMilli()), (oldAttempt, newAttempt) -> {
            if (Instant.now().toEpochMilli() - oldAttempt.getLastAttemptTime() < TIME_WINDOW_MS) {
                oldAttempt.increment();
                oldAttempt.setLastAttemptTime(Instant.now().toEpochMilli());
                return oldAttempt;
            } else {
                return newAttempt;
            }
        });
    }

    private void resetAttempts(String ip) {
        loginAttempts.remove(ip);
    }
    private static class LoginAttempt {
        private int count;
        private long lastAttemptTime;

        public LoginAttempt(int count, long lastAttemptTime) {
            this.count = count;
            this.lastAttemptTime = lastAttemptTime;
        }

        public int getCount() {
            return count;
        }

        public void increment() {
            this.count++;
        }

        public long getLastAttemptTime() {
            return lastAttemptTime;
        }

        public void setLastAttemptTime(long lastAttemptTime) {
            this.lastAttemptTime = lastAttemptTime;
        }
    }



    @PostMapping("/signup")
    public ResponseEntity<UserTokenState> addUser(@RequestBody UserDTO userRequest, UriComponentsBuilder ucBuilder) {
        User existUser = this.userService.findByUsername(userRequest.getUsername());

        if (existUser != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        String activationToken = UUID.randomUUID().toString();
        userRequest.setActivationToken(activationToken);
        userRequest.setPostCount(0L);
        userRequest.setFollowingCount(0L);
        userRequest.setActivationSentAt(LocalDateTime.now());
        userRequest.setFollowersCount(0L);
        // Sačuvaj novog korisnika bez potrebe za prethodnom autentifikacijom
        User user = this.userService.save(userRequest);


        String activationLink = "http://localhost:8080/auth/activate?token=" + activationToken;

        emailService.sendActivationEmail(user.getEmail(), activationLink);

        // Nakon registracije, autentifikuj novog korisnika kako bi generisao JWT token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword()));

        // Postavi autentifikaciju u sigurnosni kontekst
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generiši token za novoregistrovanog korisnika
        String jwt = tokenUtils.generateToken(user.getUsername());
        int expiresIn = tokenUtils.getExpiredIn();

        // Vrati token i informacije o korisniku
        return new ResponseEntity<>(new UserTokenState(jwt, expiresIn), HttpStatus.CREATED);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token) {
        User user = userService.findByActivationToken(token);

        if (user == null) {
            return new ResponseEntity<>("Invalid activation token", HttpStatus.BAD_REQUEST);
        }

        user.setIsActive(true);
        user.setActivationToken(null); // Očistite token nakon aktivacije
        userService.updateUser(user);

        return new ResponseEntity<>("Account activated successfully", HttpStatus.OK);
    }


}
