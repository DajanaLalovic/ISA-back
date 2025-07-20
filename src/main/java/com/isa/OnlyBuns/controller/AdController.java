package com.isa.OnlyBuns.controller;


import com.isa.OnlyBuns.dto.AdPostDTO;
import com.isa.OnlyBuns.iservice.IAdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AdController {

    @Autowired
    private IAdService adService;

    @PostMapping("/ads/send")
    public ResponseEntity<?> sendAd(@RequestBody AdPostDTO adPostDTO) {
        adService.sendAdPost(adPostDTO.getDescription(), adPostDTO.getCreatedAt(), adPostDTO.getUsername());
        return ResponseEntity.ok("Ad sent.");
    }

}

