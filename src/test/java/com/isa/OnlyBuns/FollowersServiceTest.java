package com.isa.OnlyBuns;

import com.isa.OnlyBuns.model.User;
import com.isa.OnlyBuns.irepository.IUserRepository;
import com.isa.OnlyBuns.service.UserService;
import org.junit.jupiter.api.AfterEach;
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
    private User follower1;
    private User follower2;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll(); // pre testova brisi bazu
        userRepository.flush();
        //korisnikkog ce zapratiti
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

        //dva korisnika koja ce vrsiti zapracivanje
        follower1 = new User();
        follower1.setUsername("Follower1");
        follower1.setPassword("password");
        follower1.setEmail("follower1@example.com");
        follower1.setName("FollowerName1");
        follower1.setSurname("FollowerSurname1");
        follower1.setIsActive(true);
        follower1.setPostCount(0L);
        follower1.setFollowingCount(0L);
        follower1.setFollowersCount(0L);
        userRepository.save(follower1);

        follower2 = new User();
        follower2.setUsername("Follower2");
        follower2.setPassword("password");
        follower2.setEmail("follower2@example.com");
        follower2.setName("FollowerName2");
        follower2.setSurname("FollowerSurname2");
        follower2.setIsActive(true);
        follower2.setPostCount(0L);
        follower2.setFollowingCount(0L);
        follower2.setFollowersCount(0L);
        userRepository.save(follower2);
    }
    //za brisanje posle testa
    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        userRepository.flush();
    }


    @Test
    public void testConcurrentFollowUserWithSleep() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<?> thread1 = executor.submit(() -> {
            try {
                System.out.println("Thread 1 starting...");
                userService.followUser(userToFollow.getId(), follower1.getUsername());
                Thread.sleep(2000); // Simulacija kašnjenja
            } catch (Exception e) {
                System.err.println("Error in Thread 1: " + e.getMessage());
            }
        });

        Future<?> thread2 = executor.submit(() -> {
            try {
                System.out.println("Thread 2 starting...");
                userService.followUser(userToFollow.getId(), follower2.getUsername());
            } catch (Exception e) {
                System.err.println("Error in Thread 2: " + e.getMessage());
            }
        });

        thread1.get(); // Sačekaj završetak prve niti
        thread2.get(); // Sačekaj završetak druge niti

        // Proveri broj pratilaca korisnika koji je praćen (userToFollow)
        User updatedUser = userRepository.findById(userToFollow.getId()).orElse(null);
        assertEquals(2, updatedUser.getFollowersCount());

    }
}
