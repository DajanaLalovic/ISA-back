package com.isa.OnlyBuns.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "CHAT_MESSAGES")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false)
    private Integer senderId;

    @Column(nullable=false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = true)
    private Integer groupId;//nullable-jer moze i komunikacija van grupe

    @Column(nullable=true)
    private Integer receiverId ; //bice nullable za grupe

    @Column(nullable = true)
    private boolean isRead;

    public ChatMessage(){}


    public ChatMessage(Integer id, Integer senderId, String content, LocalDateTime timestamp, Integer groupId, boolean isRead,Integer receiverId) {
        this.id=id;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
        this.groupId = groupId;
        this.isRead = isRead;
        this.receiverId = receiverId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public Integer getReceiverId() {return receiverId;}
    public void setReceiverId(Integer receiverId) {this.receiverId = receiverId;}
    @Override
    public String toString() {
        return "ChatMessage{" +
                "id=" + id +
                ", senderId='" + senderId + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", groupId='" + groupId + '\'' +
                ", isRead=" + isRead +'\''+
                ", receiverId=" + receiverId +
                '}';
    }
}
