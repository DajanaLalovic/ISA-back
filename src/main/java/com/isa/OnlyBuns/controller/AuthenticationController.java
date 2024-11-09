package com.isa.OnlyBuns.controller;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.isa.OnlyBuns.dto.JwtAuthenticationRequest;
import com.isa.OnlyBuns.dto.UserDTO;
import com.isa.OnlyBuns.dto.UserTokenState;
import com.isa.OnlyBuns.model.User;
import com.isa.OnlyBuns.iservice.IUserService;
import com.isa.OnlyBuns.util.TokenUtils;


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

    // Prvi endpoint koji pogadja korisnik kada se loguje.
    // Tada zna samo svoje korisnicko ime i lozinku i to prosledjuje na backend.
    @PostMapping("/login")
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

    @PostMapping("/signup")
    public ResponseEntity<UserTokenState> addUser(@RequestBody UserDTO userRequest, UriComponentsBuilder ucBuilder) {
        User existUser = this.userService.findByUsername(userRequest.getUsername());

        if (existUser != null) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Sačuvaj novog korisnika bez potrebe za prethodnom autentifikacijom
        User user = this.userService.save(userRequest);

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

}
