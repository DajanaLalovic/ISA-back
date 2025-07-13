package com.isa.OnlyBuns.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = true)
    private String imagePath;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id", referencedColumnName = "id", nullable = false)
    private Location location;


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

    @Column(name = "ad_approved")
    private Boolean adApproved = false;

    public Post() {
        this.comments = new ArrayList<>();
        this.likes = new ArrayList<>();
    }

    public Post(Integer id,
                String description,
                String imagePath,
                Location location,
                LocalDateTime createdAt,
                long userId,
                List<Comment> comments,
                List<Integer> likes,
                boolean isRemoved) {


        this.id = id;
        this.description = description;
        this.imagePath = imagePath;
        this.location = location;
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

    public Location getLocation() {
        return location;
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

    public void setLocation(Location location) {
        this.location = location;
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

    public Boolean getAdApproved() {
        return adApproved;
    }

    public void setAdApproved(Boolean adApproved) {
        this.adApproved = adApproved;
    }



    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", location=" + location +
                ", createdAt=" + createdAt +
                ", userId=" + userId +
                ", isRemoved=" + isRemoved +
                ", comments=" + comments +
                ", likes=" + likes +
                '}';
    }
}
