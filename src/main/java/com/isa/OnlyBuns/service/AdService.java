package com.isa.OnlyBuns.service;


import com.isa.OnlyBuns.customMessaging.CustomProducer;
import com.isa.OnlyBuns.dto.AdPostDTO;
import com.isa.OnlyBuns.iservice.IAdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdService implements IAdService {

    @Autowired
    private CustomProducer customProducer;

    public void sendAdPost(String description, String createdAt, String username) {
        AdPostDTO adPost = new AdPostDTO(description, createdAt, username);
        customProducer.send("adQueue", adPost);
    }
}
