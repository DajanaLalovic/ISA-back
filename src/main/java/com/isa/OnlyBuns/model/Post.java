package com.isa.OnlyBuns.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String imagePath;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "is_removed")
    private Boolean isRemoved;

    public Post() {
    }

    public Post(Integer id,
                String description,
                String imagePath,
                double latitude,
                double longitude,
                LocalDateTime createdAt,
                Integer userId,
                boolean isRemoved) {

        this.id = id;
        this.description = description;
        this.imagePath = imagePath;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.userId = userId;
        this.isRemoved = isRemoved;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Integer getUserId() {
        return userId;
    }
    public boolean getIsRemoved() {return isRemoved;}

    public void setId(Integer id) {this.id=id;}

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public void setIsRemoved (boolean isRemoved) {this.isRemoved = isRemoved;}

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", createdAt=" + createdAt +
                ", userId=" + userId +
                ", isRemoved=" + isRemoved +
                '}';
    }
}
