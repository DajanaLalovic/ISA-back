package com.isa.OnlyBuns.customMessaging;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.isa.OnlyBuns.dto.AdPostDTO;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AdAgencySimulator {

    private static final Logger log = LoggerFactory.getLogger(AdAgencySimulator.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String queueName = "adQueue";

    @PostConstruct
    public void startAdConsumerThread() {
        MessageQueueManager.createQueue(queueName);

        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    String json = MessageQueueManager.receiveMessage(queueName);
                    AdPostDTO ad = objectMapper.readValue(json, AdPostDTO.class);
                    log.info("Agencija primila reklamu: " + ad.getDescription() +
                            ", postavio: " + ad.getUsername() +
                            ", vreme: " + ad.getCreatedAt());
                } catch (Exception e) {
                    log.error("Greška prilikom obrade reklame", e);
                }
            }
        });

        thread.setDaemon(true);
        thread.start();
    }
}
