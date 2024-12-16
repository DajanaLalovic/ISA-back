package com.isa.OnlyBuns.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
//@Table(name = "like_time")
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer postId;
    private Integer userId;
    private LocalDateTime likedAt;

    public PostLike(Integer postId, Integer userId, LocalDateTime likedAt) {
        this.postId = postId;
        this.userId = userId;
        this.likedAt = likedAt;
    }

    public PostLike() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getLikedAt() {
        return likedAt;
    }

    public void setLikedAt(LocalDateTime likedAt) {
        this.likedAt = likedAt;
    }
}


