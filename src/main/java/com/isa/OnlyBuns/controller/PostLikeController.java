package com.isa.OnlyBuns.controller;

import com.isa.OnlyBuns.service.PostLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostLikeController {

    @Autowired
    private PostLikeService postLikeService;

    @GetMapping("/top-users")
    @PreAuthorize("isAuthenticated()")
    public List<Integer> getTopUsers() {
        return postLikeService.getTopUsersWithMostLikes();
    }
}

