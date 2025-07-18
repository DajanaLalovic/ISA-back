package com.isa.OnlyBuns.customMessaging;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.isa.OnlyBuns.dto.LocationMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CustomProducer {

    private static final Logger log = LoggerFactory.getLogger(CustomProducer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void send(String queueName, LocationMessageDTO message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            MessageQueueManager.sendMessage(queueName, json);
            log.info("CustomProducer sent message to " + queueName + ": " + json);
        } catch (Exception e) {
            log.error("Failed to serialize message", e);
        }
    }
}
