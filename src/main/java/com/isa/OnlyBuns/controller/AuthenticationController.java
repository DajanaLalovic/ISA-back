package com.isa.OnlyBuns.controller;

import com.isa.OnlyBuns.service.EmailService;
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
import java.util.Map;
import java.util.UUID;


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

    // Prvi endpoint koji pogadja korisnik kada se loguje.
    // Tada zna samo svoje korisnicko ime i lozinku i to prosledjuje na backend.
  /*  @PostMapping("/login")
    public ResponseEntity<UserTokenState> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response) {
        // Ukoliko kredencijali nisu ispravni, logovanje nece biti uspesno, desice se
        // AuthenticationException
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(), authenticationRequest.getPassword()));

        // Ukoliko je autentifikacija uspesna, ubaci korisnika u trenutni security
        // kontekst
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Kreiraj token za tog korisnika
        User user = (User) authentication.getPrincipal();
        String jwt = tokenUtils.generateToken(user.getUsername());
        int expiresIn = tokenUtils.getExpiredIn();

        // Vrati token kao odgovor na uspesnu autentifikaciju
        return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));
    }
*/
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest, HttpServletResponse response) {
        try {
            // Prvo proveravamo da li korisnik postoji
            User user = userService.findByUsername(authenticationRequest.getUsername());

            // Ako korisnik ne postoji
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.BAD_REQUEST);
            }

            // Proveravamo da li je korisnik aktiviran
            if (!user.getIsActive()) {
                return new ResponseEntity<>("Your account isn't activated yet. Check your mail.", HttpStatus.FORBIDDEN);
            }

            // Ako je sve u redu sa korisničkim nalogom, pokušavamo autentifikaciju
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            user.setLastLogin(LocalDateTime.now());

            // Konvertuj User u UserDTO i sačuvaj
            UserDTO userDTO = new UserDTO(user);
            userService.save(userDTO);
            // Kreiranje JWT tokena
            String jwt = tokenUtils.generateToken(user.getUsername());
            int expiresIn = tokenUtils.getExpiredIn();

            return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));

        } catch (Exception e) {
            return new ResponseEntity<>("Login failed", HttpStatus.UNAUTHORIZED);
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
        // Sačuvaj novog korisnika bez potrebe za prethodnom autentifikacijom
        User user = this.userService.save(userRequest);

        ////////////////////////


        //this.userService.save(userRequest );

        String activationLink = "http://localhost:8080/auth/activate?token=" + activationToken;

        emailService.sendActivationEmail(user.getEmail(), activationLink);


        /////////////////////



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
    }/*
    @PostMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam("token") String token) {

    public ResponseEntity<String> activateAccount(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        User user = userService.findByActivationToken(token);

        if (user == null) {
            return new ResponseEntity<>("Invalid activation token", HttpStatus.BAD_REQUEST);
        }

        user.setIsActive(true);
        user.setActivationToken(null); // Očistite token nakon aktivacije
        userService.updateUser(user);


        return new ResponseEntity<>("Account activated successfully", HttpStatus.OK);
    }*/

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
