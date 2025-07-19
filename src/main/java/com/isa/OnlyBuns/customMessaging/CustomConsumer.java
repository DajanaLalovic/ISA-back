package com.isa.OnlyBuns.customMessaging;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.isa.OnlyBuns.dto.LocationMessageDTO;
import com.isa.OnlyBuns.service.LocationMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class CustomConsumer {

    private static final Logger log = LoggerFactory.getLogger(CustomConsumer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final LocationMessageService locationMessageService;
    private final String queueName = "customQueue";

    public CustomConsumer(LocationMessageService locationMessageService) {
        this.locationMessageService = locationMessageService;
    }

    @PostConstruct
    public void startConsumerThread() {
        MessageQueueManager.createQueue(queueName);

        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    String json = MessageQueueManager.receiveMessage(queueName);
                    LocationMessageDTO message = objectMapper.readValue(json, LocationMessageDTO.class);
                    log.info("CustomConsumer received message: " + message.getName());
                    locationMessageService.save(message);
                } catch (Exception e) {
                    log.error("Error processing message", e);
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}