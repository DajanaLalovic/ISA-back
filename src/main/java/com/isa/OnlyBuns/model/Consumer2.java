package com.isa.OnlyBuns.model;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.isa.OnlyBuns.dto.LocationMessageDTO;
import com.isa.OnlyBuns.service.LocationMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Consumer2 {

    private static final Logger log = LoggerFactory.getLogger(Consumer2.class);
    private final ObjectMapper objectMapper;

    @Autowired
    LocationMessageService locationMessageService;

    @Autowired
    public Consumer2(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    @RabbitListener(queues = "${myqueue2}")
    public void handler(String message) {
        try {
            log.info("Received message: " + message);
            LocationMessageDTO locationMessage = objectMapper.readValue(message, LocationMessageDTO.class);
            log.info("Deserialized LocationMessage: " + locationMessage.getName());
            log.info("Consumer2> " + message);
            locationMessageService.save(locationMessage);
        } catch (Exception e) {
            log.error("Error while processing message: ", e);
        }
    }
    /*
     * @RabbitListener anotira metode za kreiranje handlera za bilo koju poruku koja
     * pristize, sto znaci da ce se kreirati listener koji je konektovan na RabbitQM
     * queue i koji ce prosledjivati poruke metodi. Listener ce konvertovati poruku
     * u odgovorajuci tip koristeci odgovarajuci konvertor poruka (implementacija
     * org.springframework.amqp.support.converter.MessageConverter interfejsa).
     */
//	@RabbitListener(
//			bindings = @QueueBinding(value = @Queue(value = "${myqueue2}", durable = "false"),
//			exchange = @Exchange(value = "${queue.exchange}", type = ExchangeTypes.FANOUT, durable = "true")))

}