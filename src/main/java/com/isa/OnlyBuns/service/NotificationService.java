package com.isa.OnlyBuns.service;

import com.isa.OnlyBuns.irepository.IPostRepository;
import com.isa.OnlyBuns.irepository.IUserRepository;
import com.isa.OnlyBuns.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private IPostRepository postRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 9 * * ?") // Svakog dana u 9:00
    public void sendWeeklyNotifications() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        // Prebroj nove objave u poslednjih 7 dana
        int newPostsCount = postRepository.countByCreatedAtAfter(sevenDaysAgo);
        // Pronađi sve korisnike koji nisu pristupili aplikaciji poslednjih 7 dana
        List<User> inactiveUsers = userRepository.findByLastLoginBefore(sevenDaysAgo);
        // Pošalji e-mail svakom neaktivnom korisniku
        for (User user : inactiveUsers) {
            emailService.sendWeeklyStatsEmail(user.getEmail(), user.getUsername(), newPostsCount);
        }
    }
}
