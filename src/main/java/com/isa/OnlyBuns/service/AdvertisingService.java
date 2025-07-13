package com.isa.OnlyBuns.service;

import com.isa.OnlyBuns.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdvertisingService {

    private final RabbitTemplate rabbitTemplate;
    private final FanoutExchange fanoutExchange;

    public void sendAdPost(Post post) {
        Map<String, Object> message = new HashMap<>();
        message.put("description", post.getDescription());
        message.put("publishTime", post.getCreatedAt());
       // message.put("username", post.getUser().getUsername());

        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "", message);
    }
}
