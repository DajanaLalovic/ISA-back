package com.isa.OnlyBuns.service;

import com.isa.OnlyBuns.model.Post;
import com.isa.OnlyBuns.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdvertisingService {

    private final RabbitTemplate rabbitTemplate;
    private final FanoutExchange fanoutExchange;
    private final UserService userService;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public void sendAdPost(Post post) {
        Map<String, Object> message = new HashMap<>();
        User user = userService.findById(post.getUserId());
        String fullName = user != null ? user.getName() + " " + user.getSurname() : "Unknown User";

        message.put("description", post.getDescription());
        message.put("publishTime", post.getCreatedAt().format(formatter));
        message.put("username", fullName);

        rabbitTemplate.convertAndSend(fanoutExchange.getName(), "", message);
    }
}
