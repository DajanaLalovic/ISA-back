package com.isa.OnlyBuns.service;

import com.isa.OnlyBuns.irepository.IPostRepository;
import com.isa.OnlyBuns.irepository.IUserRepository;
import com.isa.OnlyBuns.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private IPostRepository postRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private EmailService emailService;

    //@Scheduled(cron = "0 */1 * * * ?")
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendWeeklyNotifications() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now(ZoneOffset.UTC).minusDays(7).withNano(0);;
        // Prebroj nove objave u poslednjih 7 dana
        List<User> inactiveUsers = userRepository.findByLastLoginBefore(sevenDaysAgo);

        // Obradi svakog neaktivnog korisnika
        for (User user : inactiveUsers) {
            LocalDateTime lastLogin = user.getLastLogin();
            if (lastLogin == null) {
                // Ako korisnik nikada nije pristupio, možete odlučiti da pošaljete obaveštenje za sve postove
                lastLogin = sevenDaysAgo;
            }
            int newPostsCount = postRepository.countByCreatedAtAfter(lastLogin);

            System.out.println("User: " + user.getUsername());
            System.out.println("Last login: " + lastLogin);
            System.out.println("New posts since last login: " + newPostsCount);

            // Pošalji e-mail korisniku samo ako ima novih objava
            if (newPostsCount > 0) {
                emailService.sendWeeklyStatsEmail(user.getEmail(), user.getUsername(), newPostsCount);
            }
        }
    }
}
