package com.isa.OnlyBuns.controller;

import com.isa.OnlyBuns.customMessaging.CustomProducer;
import com.isa.OnlyBuns.dto.LocationMessageDTO;
import com.isa.OnlyBuns.model.LocationMessage;
import com.isa.OnlyBuns.service.LocationMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class LocationMessageController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private LocationMessageService locationMessageService;

    @Autowired
    private CustomProducer customProducer;

    @GetMapping("/locationMessage/all")
    //@PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("isAuthenticated()")
    public List<LocationMessage> loadAll() {
        return this.locationMessageService.findAll();
    }


    @PostMapping("/send")
    public ResponseEntity<String> sendLocation(@RequestBody LocationMessageDTO dto) {
        customProducer.send("customQueue", dto);
        return ResponseEntity.ok("Poruka poslata u red.");
    }
}
