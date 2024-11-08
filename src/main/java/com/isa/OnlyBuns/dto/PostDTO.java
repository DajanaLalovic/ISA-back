package com.isa.OnlyBuns.dto;

import com.isa.OnlyBuns.model.Post;

import java.time.LocalDateTime;

public class PostDTO {


    private Integer id;
    private String description;
    private String imagePath;
    private double latitude;
    private double longitude;
    private LocalDateTime createdAt;
    private Integer userId;

    public PostDTO() {
    }

    public PostDTO(Post post) {
        this(post.getId(), post.getDescription(), post.getImagePath(), post.getLatitude(),post.getLongitude(),post.getCreatedAt(), post.getUserId());
    }


    public PostDTO(Integer id, String description,String imagePath, double latitude, double longitude, LocalDateTime createdAt, Integer userId) {
        this.id = id;
        this.description = description;
        this.latitude = latitude;
        this.imagePath = imagePath;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.userId = userId;
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

    public Integer getUserId() {
        return userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
