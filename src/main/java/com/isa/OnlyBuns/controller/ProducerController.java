package com.isa.OnlyBuns.controller;


import com.isa.OnlyBuns.dto.LocationMessageDTO;
import com.isa.OnlyBuns.model.LocationMessage;
import com.isa.OnlyBuns.model.Producer;
import com.isa.OnlyBuns.service.LocationMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api")
public class ProducerController {

    @Autowired
    private Producer producer;
    @Autowired
    private LocationMessageService locationMessageService;

    @PostMapping(value="/{queue}", consumes = "application/json")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> sendMessage(@PathVariable("queue") String queue, @RequestBody LocationMessageDTO message) {
        locationMessageService.save(message);
        producer.sendTo(queue, message);
        return ResponseEntity.ok().build();
    }

}
