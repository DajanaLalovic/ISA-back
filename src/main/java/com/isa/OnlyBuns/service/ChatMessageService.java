package com.isa.OnlyBuns.service;

import com.isa.OnlyBuns.irepository.IChatMessageRepository;
import com.isa.OnlyBuns.irepository.IGroupRepository;
import com.isa.OnlyBuns.model.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {

    @Autowired
    private IChatMessageRepository chatMessageRepository;

    public void saveMessage(ChatMessage chatMessage) {
        chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getLastMessages(Integer groupId) {
        return chatMessageRepository.findTop10ByGroupIdOrderByTimestampDesc(groupId);
    }

//    public List<ChatMessage>  getAllForPrivateChat(Integer senderId,Integer receiverId){
//
//    }
    public List<ChatMessage> getAllPrivateMessages(Integer senderId, Integer receiverId) {
        return chatMessageRepository.findAllBySenderAndReceiverId(senderId, receiverId);
    }


}
