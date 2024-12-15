package com.isa.OnlyBuns;

import com.isa.OnlyBuns.model.User;
import com.isa.OnlyBuns.irepository.IUserRepository;
import com.isa.OnlyBuns.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FollowersServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private IUserRepository userRepository;

    private User userToFollow;
    private User follower;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll(); // Očisti sve korisnike iz baze pre testa
        userRepository.flush();
        // Kreiraj korisnika koji će biti praćen
        userToFollow = new User();
        userToFollow.setUsername("UserToFollow");
        userToFollow.setPassword("password");
        userToFollow.setEmail("usertofollow@example.com");
        userToFollow.setName("John");
        userToFollow.setSurname("Doe");
        userToFollow.setIsActive(true);
        userToFollow.setPostCount(0L);
        userToFollow.setFollowingCount(0L);
        userToFollow.setFollowersCount(0L);
        userRepository.save(userToFollow);

        // Kreiraj korisnika koji će pratiti
        follower = new User();
        follower.setUsername("Follower");
        follower.setPassword("password");
        follower.setEmail("follower@example.com");
        follower.setName("Jane");
        follower.setSurname("Smith");
        follower.setIsActive(true);
        follower.setPostCount(0L);
        follower.setFollowingCount(0L);
        follower.setFollowersCount(0L);
        userRepository.save(follower);
    }

    @Test
    public void testConcurrentFollowUserWithSleep() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<?> thread1 = executor.submit(() -> {
            try {
                System.out.println("Thread 1 starting...");
                userService.followUser(userToFollow.getId(), follower.getUsername());
                Thread.sleep(3000); // Uspavljivanje kako bi drugi thread pokušao da izvrši istu operaciju
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Future<?> thread2 = executor.submit(() -> {
            try {
                System.out.println("Thread 2 starting...");
                userService.followUser(userToFollow.getId(), follower.getUsername());
            } catch (Exception e) {
                System.out.println("Thread 2 encountered an exception: " + e.getMessage());
            }
        });

        thread1.get(); // Sačekaj završetak prve niti
        thread2.get(); // Sačekaj završetak druge niti

        User updatedUser = userRepository.findById(userToFollow.getId()).orElse(null);
        assertEquals(1, updatedUser.getFollowersCount()); // Proveri da broj pratilaca nije dupliran
    }

}
