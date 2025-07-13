package com.isa.OnlyBuns.model;
//
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.stereotype.Component;
//
//@Component
//public class Consumer {
//
//    private static final Logger log = LoggerFactory.getLogger(Consumer.class);
//    /*
//     * @RabbitListener anotira metode za kreiranje handlera za bilo koju poruku koja pristize,
//     * sto znaci da ce se kreirati listener koji je konektovan na RabbitQM queue i koji ce
//     * prosledjivati poruke metodi. Listener ce konvertovati poruku u odgovorajuci tip koristeci
//     * odgovarajuci konvertor poruka (implementacija org.springframework.amqp.support.converter.MessageConverter interfejsa).
//     */
//    @RabbitListener(queues="${myqueue2}")
//    public void handler(String message){
//        log.info("Consumer> " + message);
//    }
//}

import com.isa.OnlyBuns.dto.LocationMessageDTO;
import com.isa.OnlyBuns.service.LocationMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class Consumer {

    @Autowired
    ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(Consumer.class);

    @Autowired
    LocationMessageService locationMessageService;

    @RabbitListener(queues = "${myqueue2}")
    public void handler(LocationMessageDTO locationMessage) {
        try {
            log.info("CONSUMER");
            log.info("Received LocationMessage: " + locationMessage.getName());
            // You can now process the LocationMessageDTO (e.g., save it to the database)
            locationMessageService.save(locationMessage);
        } catch (Exception e) {
            log.error("Error while processing message: ", e);
        }
    }
}
//    @RabbitListener(queues="${myqueue2}")
//    public void handler(byte[] message) {
//        try {
//           // ObjectMapper objectMapper = new ObjectMapper();
//            LocationMessageDTO locationMessage = objectMapper.readValue(message, LocationMessageDTO.class);
//            log.info("Received LocationMessage: " + locationMessage.getName());
//            // Ovde možeš dodati kod za čuvanje u bazu
//        } catch (Exception e) {
//            log.error("Error while processing message: ", e);
//        }
//    }

