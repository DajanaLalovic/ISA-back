package com.isa.OnlyBuns.model;


import com.isa.OnlyBuns.dto.LocationMessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
//
//@Component
//public class Producer {
//
//    private static final Logger log = LoggerFactory.getLogger(Producer.class);
//
//    /*
//     * RabbitTemplate je pomocna klasa koja uproscava sinhronizovani
//     * pristup RabbitMQ za slanje i primanje poruka.
//     */
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    /*
//     * U ovom slucaju routingKey ce biti ime queue.
//     * Poruka se salje u exchange (default exchange u ovom primeru) i
//     * exchange ce rutirati poruke u pravi queue.
//     */
//    public void sendTo(String routingkey, String message){
//        log.info("Sending> ... Message=[ " + message + " ] RoutingKey=[" + routingkey + "]");
//        this.rabbitTemplate.convertAndSend(routingkey, message);
//    }
//
//
//    /*
//     * U ovom slucaju routingKey ce biti ime queue.
//     * Poruka se salje u exchange ciji je naziv prosledjen kao prvi parametar i
//     * exchange ce rutirati poruke u pravi queue.
//     */
//    public void sendToExchange(String exchange, String routingkey, String message){
//        log.info("Sending> ... Message=[ " + message + " ] Exchange=[" + exchange + "] RoutingKey=[" + routingkey + "]");
//        this.rabbitTemplate.convertAndSend(exchange, routingkey, message);
//    }
//}

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class Producer {

    private static final Logger log = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    ObjectMapper objectMapper;

    public void sendTo(String routingkey, LocationMessageDTO message){
        try {
            // ObjectMapper objectMapper = new ObjectMapper();
            //  String messageJson = objectMapper.writeValueAsString(message);
            String messageJson = objectMapper.writeValueAsString(message);
            log.info("Sending> ... Message=[ " + messageJson + " ] RoutingKey=[" + routingkey + "]");
            this.rabbitTemplate.convertAndSend(routingkey, messageJson);
        } catch (Exception e) {
            log.error("Error while sending message: ", e);
        }
    }
}

