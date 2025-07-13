package com.isa.OnlyBuns.irepository;

import com.isa.OnlyBuns.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface  IChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findTop10ByGroupIdOrderByTimestampDesc(Integer groupId); //uzimanje poslednjih 10 poruka
    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.senderId = :user1 AND m.receiverId = :user2) OR " +
            "(m.senderId = :user2 AND m.receiverId = :user1) " +
            "ORDER BY m.timestamp ASC")
    List<ChatMessage> findAllBySenderAndReceiverId(@Param("user1") Integer user1, @Param("user2") Integer user2);
}
