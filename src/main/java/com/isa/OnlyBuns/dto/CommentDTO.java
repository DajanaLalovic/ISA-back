package com.isa.OnlyBuns.dto;
import com.isa.OnlyBuns.model.Comment;
import java.time.LocalDateTime;
public class CommentDTO {

    private int id;
    private String text;
    private long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer postId; // ID posta za koji je komentar vezan

    public CommentDTO() {
    }

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.userId = comment.getUserId();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.postId = (comment.getPost() != null) ? comment.getPost().getId() : null;
    }

    public CommentDTO(int id, String text, long userId, LocalDateTime createdAt, LocalDateTime updatedAt, Integer postId) {
        this.id = id;
        this.text = text;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.postId = postId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "CommentDTO{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", postId=" + postId +
                '}';
    }
}
