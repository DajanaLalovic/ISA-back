package com.isa.OnlyBuns.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = true)
    private String imagePath;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "user_id", nullable = false)
    private long userId;


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_likes")
    @Column(name = "user_id")
    private List<Integer> likes= new ArrayList<>();

    @Column(name = "is_removed")
    private Boolean isRemoved;


    public Post() {
        this.comments = new ArrayList<>();
        this.likes = new ArrayList<>();
    }

    public Post(Integer id,
                String description,
                String imagePath,
                double latitude,
                double longitude,
                LocalDateTime createdAt,
                long userId,
                List<Comment> comments,
                List<Integer> likes,
                boolean isRemoved) {


        this.id = id;
        this.description = description;
        this.imagePath = imagePath;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.userId = userId;
        this.comments = comments;
        this.likes = likes;
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

    public long getUserId() {
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

    public void setUserId(long userId) {
        this.userId = userId;
    }
    public void setIsRemoved (boolean isRemoved) {this.isRemoved = isRemoved;}
    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<Integer> getLikes() {
        return likes;
    }

    public void setLikes(List<Integer> likes) {
        this.likes = likes;
    }

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
                ", comments=" + comments +
                ", likes=" + likes +
                '}';
    }
}
