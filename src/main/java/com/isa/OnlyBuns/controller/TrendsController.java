package com.isa.OnlyBuns.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import com.isa.OnlyBuns.model.Post;
import com.isa.OnlyBuns.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Primer Spring Boot kontrolera
@RestController
@RequestMapping("/api/trends")
public class TrendsController {

    @Autowired
    private PostService postService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getTrends() {
        Map<String, Object> trends = new HashMap<>();

        long totalPosts = postService.getTotalPosts();
        //long postsLast30Days = postService.getPostsLast30Days();
       // List<Post> top5Last7Days = postService.getTop5PostsLast7Days();
       // List<Post> top10AllTime = postService.getTop10PostsAllTime();
        long postsLast30Days = postService.countPostsLastMonth();
        List<Post> top5Last7Days = postService.getTop5MostLikedPostsLast7Days();
        List<Post> top10AllTime =postService.getTop10MostLikedPosts();

        trends.put("totalPosts", totalPosts);
        trends.put("postsLast30Days", postsLast30Days);
        trends.put("top5Last7Days", top5Last7Days);
        trends.put("top10AllTime", top10AllTime);

        return ResponseEntity.ok(trends);
    }
}
