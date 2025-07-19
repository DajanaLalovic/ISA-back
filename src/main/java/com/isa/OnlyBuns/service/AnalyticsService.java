package com.isa.OnlyBuns.service;

import com.isa.OnlyBuns.irepository.ICommentRepository;
import com.isa.OnlyBuns.irepository.IPostRepository;
import com.isa.OnlyBuns.irepository.IUserRepository;
import com.isa.OnlyBuns.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.isa.OnlyBuns.model.User;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {
    @Autowired
    private IPostRepository postRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ICommentRepository commentRepository;

    public Map<String, Long> getAnalytics() {
        LocalDateTime now = LocalDateTime.now();

        List<Post> allPosts = postRepository.findAll();

        //postovi
        long weeklyPosts = allPosts.stream()
                .filter(post -> post.getCreatedAt().isAfter(now.minusWeeks(1)))
                .count();

        long monthlyPosts = allPosts.stream()
                .filter(post -> post.getCreatedAt().isAfter(now.minusMonths(1)))
                .count();

        long yearlyPosts = allPosts.stream()
                .filter(post -> post.getCreatedAt().isAfter(now.minusYears(1)))
                .count();


        //komentari
        long weeklyComments = allPosts.stream()
                .flatMap(post -> postRepository.findAllCommentsByPostId((long) post.getId()).stream())
                .filter(comment -> comment.getCreatedAt().isAfter(now.minusWeeks(1)))
                .count();

        long monthlyComments = allPosts.stream()
                .flatMap(post -> postRepository.findAllCommentsByPostId((long) post.getId()).stream())
                .filter(comment -> comment.getCreatedAt().isAfter(now.minusMonths(1)))
                .count();

        long yearlyComments = allPosts.stream()
                .flatMap(post -> postRepository.findAllCommentsByPostId((long) post.getId()).stream())
                .filter(comment -> comment.getCreatedAt().isAfter(now.minusYears(1)))
                .count();

        Map<String, Long> analytics = new HashMap<>();
        analytics.put("weeklyPosts", weeklyPosts);
        analytics.put("monthlyPosts", monthlyPosts);
        analytics.put("yearlyPosts", yearlyPosts);

        analytics.put("weeklyComments", weeklyComments);
        analytics.put("monthlyComments", monthlyComments);
        analytics.put("yearlyComments", yearlyComments);

        return analytics;
    }

//    public Map<String,Double> getUserActivityStatistics(){
//        long totalUsers=userRepository.findAll().size();
//
//        long usersWithPosts=postRepository.findDistinctUserIds().size();
//        long usersWithComments = commentRepository.findDistinctUserIds().size();
//
//        long usersWithoutActivity = totalUsers - (usersWithPosts + usersWithComments);
//        Map<String, Double> statistics = new HashMap<>();
//        statistics.put("usersWithPosts", (double) usersWithPosts / totalUsers * 100);
//        statistics.put("usersWithComments", (double) usersWithComments / totalUsers * 100);
//        statistics.put("usersWithoutActivity", (double) usersWithoutActivity / totalUsers * 100);
//        return statistics;
//    }
public Map<String, Double> getUserActivityStatistics() {
    List<Long> allUserIds = userRepository.findAll()
            .stream()
            .map(User::getId)
            .toList();

    Set<Long> postUserIds = new HashSet<>(postRepository.findDistinctUserIds());
    Set<Long> commentUserIds = new HashSet<>(commentRepository.findDistinctUserIds());

    System.out.println("All Users: " + allUserIds.size());
    System.out.println("User IDs with Posts: " + postUserIds);
    System.out.println("User IDs with Comments: " + commentUserIds);

    Set<Long> onlyCommenters = new HashSet<>(commentUserIds);
    onlyCommenters.removeAll(postUserIds);

    System.out.println("Only Commenters: " + onlyCommenters);

    Set<Long> noActivity = allUserIds.stream()
            .filter(id -> !postUserIds.contains(id) && !commentUserIds.contains(id))
            .collect(Collectors.toSet());

    System.out.println("Users without any activity: " + noActivity);

    int totalUsers = allUserIds.size();

    Map<String, Double> stats = new HashMap<>();
    stats.put("usersWithPosts", postUserIds.size() * 100.0 / totalUsers);
    stats.put("usersWithOnlyComments", onlyCommenters.size() * 100.0 / totalUsers);
    stats.put("usersWithoutActivity", noActivity.size() * 100.0 / totalUsers);

    System.out.println("Final stats: " + stats);

    return stats;
}










}
