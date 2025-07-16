package com.isa.OnlyBuns.service;

import com.isa.OnlyBuns.irepository.IChatMessageRepository;
import com.isa.OnlyBuns.irepository.IGroupMembershipRepository;
import com.isa.OnlyBuns.irepository.IGroupRepository;
import com.isa.OnlyBuns.model.ChatMessage;
import com.isa.OnlyBuns.model.GroupMembership;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMessageService {

    @Autowired
    private IChatMessageRepository chatMessageRepository;

    @Autowired
    private IGroupMembershipRepository groupMembershipRepository;

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

    public List<ChatMessage> getAllMessagesForGroup(Integer groupId) {
        return chatMessageRepository.findAllByGroupIdOrderByTimestampAsc(groupId);
    }


    public List<ChatMessage> getRelevantMessages(Integer groupId, Long userId) {
        GroupMembership membership = groupMembershipRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new RuntimeException("Membership not found"));

        LocalDateTime joinedAt = membership.getJoinedAt();

        List<ChatMessage> all = chatMessageRepository.findAllByGroupIdOrderByTimestampAsc(groupId);

        List<ChatMessage> before = all.stream()
                .filter(msg -> msg.getTimestamp().isBefore(joinedAt))
                .sorted((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp()))
                .limit(10)
                .collect(Collectors.toList());

        List<ChatMessage> after = all.stream()
                .filter(msg -> !msg.getTimestamp().isBefore(joinedAt))
                .collect(Collectors.toList());

        before.addAll(after);
        return before.stream()
                .sorted(Comparator.comparing(ChatMessage::getTimestamp))
                .collect(Collectors.toList());
    }


}
