package com.isa.OnlyBuns.dto;

import com.isa.OnlyBuns.model.Post;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {


    private Integer id;
    private String description;
    private String imagePath;
    private double latitude;
    private double longitude;
    private LocalDateTime createdAt;
    private long userId;
    private List<CommentDTO> comments;
    private List<Integer> likes;

    public PostDTO() {
    }

    public PostDTO(Post post) {
        this(post.getId(), post.getDescription(), post.getImagePath(), post.getLatitude(),post.getLongitude(),post.getCreatedAt(), post.getUserId() ,null, null);
    }


    public PostDTO(Integer id, String description,String imagePath, double latitude, double longitude, LocalDateTime createdAt, long userId, List<CommentDTO> comments, List<Integer> likes) {
        this.id = id;
        this.description = description;
        this.latitude = latitude;
        this.imagePath = imagePath;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.userId = userId;
        this.comments = comments;
        this.likes = likes;
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


    public long getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public List<Integer> getLikes() {
        return likes;
    }

    public void setLikes(List<Integer> likes) {
        this.likes = likes;
    }
}
