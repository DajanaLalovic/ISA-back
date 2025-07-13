package com.isa.OnlyBuns.service;

import com.isa.OnlyBuns.irepository.ICommentRepository;
import com.isa.OnlyBuns.irepository.IPostRepository;
import com.isa.OnlyBuns.irepository.IUserRepository;
import com.isa.OnlyBuns.model.Post;
import com.isa.OnlyBuns.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private IPostRepository postRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ICommentRepository commentRepository;


   // @Scheduled(cron = "0 0 9 * * ?")
    @Transactional(readOnly = true)
    @Scheduled(cron = "0 */1 * * * ?")
    public void sendWeeklyNotifications() {
        System.out.println(">> sendWeeklyNotifications() triggered!");
        final LocalDateTime sevenDaysAgo = LocalDateTime.now().minusMinutes(2);

        List<User> inactiveUsers = userRepository.findByLastLoginBefore(sevenDaysAgo);
        System.out.println("Found inactive users: " + inactiveUsers.size());

        for (User user : inactiveUsers) {
            final LocalDateTime lastLogin = (user.getLastLogin() != null) ? user.getLastLogin() : sevenDaysAgo;

            user = userRepository.findById(user.getId()).orElse(null);
            if (user == null) {
                continue;
            }

            final Set<Long> followedUserIds = user.getFollowing().stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());

            List<Post> newPosts = postRepository.findByUserIdInOrderByCreatedAtDesc(
                            List.copyOf(followedUserIds))
                    .stream()
                    .filter(post -> post.getCreatedAt().isAfter(lastLogin))
                    .collect(Collectors.toList());

            final int newPostsCount = newPosts.size();

            int newLikesCount = postRepository.countNewLikesForFollowedUsers(followedUserIds, lastLogin);
            int newCommentsCount = commentRepository.countNewCommentsForFollowedUsers(followedUserIds, lastLogin);

            System.out.println("User: " + user.getUsername());
            System.out.println("Last login: " + lastLogin);
            System.out.println("New posts since last login from followed users: " + newPostsCount);
            System.out.println("New likes on followed users' posts: " + newLikesCount);
            System.out.println("New comments on followed users' posts: " + newCommentsCount);

           if (newPostsCount > 0) {
                emailService.sendWeeklyStatsEmail(user.getEmail(), user.getUsername(), newPostsCount, newLikesCount, newCommentsCount);
            }
        }
    }

}