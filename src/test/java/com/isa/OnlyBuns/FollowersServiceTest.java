package com.isa.OnlyBuns;

import com.isa.OnlyBuns.irepository.IGroupMembershipRepository;
import com.isa.OnlyBuns.model.User;
import com.isa.OnlyBuns.irepository.IUserRepository;
import com.isa.OnlyBuns.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
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

    @Autowired
    private IGroupMembershipRepository groupMembershipRepository;

    private User userToFollow;
    private User follower1;
    private User follower2;

    @BeforeEach
    public void setUp() {
//        userRepository.findByUsername("UserToFollow").ifPresent(existingUser ->
//                userRepository.deleteById(existingUser.getId()));
//        userRepository.findByUsername("Follower1").ifPresent(existingUser ->
//                userRepository.deleteById(existingUser.getId()));
//        userRepository.findByUsername("Follower2").ifPresent(existingUser ->
//                userRepository.deleteById(existingUser.getId()));
        userRepository.flush();

//        groupMembershipRepository.deleteAll();
//        userRepository.deleteAll();
//        userRepository.flush();
        userRepository.flush();

        userToFollow = new User();

        String suffix = UUID.randomUUID().toString().substring(0, 8);
        userToFollow.setUsername("UserToFollow_" + suffix);
        userToFollow.setEmail("usertofollow_" + suffix + "@example.com");
//        userToFollow.setUsername("UserToFollow");
        userToFollow.setPassword("password");
//        userToFollow.setEmail("usertofollow@example.com");
        userToFollow.setName("John");
        userToFollow.setSurname("Doe");
        userToFollow.setIsActive(true);
        userToFollow.setPostCount(0L);
        userToFollow.setFollowingCount(0L);
        userToFollow.setFollowersCount(0L);
        userRepository.save(userToFollow);

        //dva korisnika koja ce vrsiti zapracivanje
        follower1 = new User();
        follower1.setUsername("Follower1"+suffix);
        follower1.setPassword("password");
        follower1.setEmail("follower1"+suffix+"@example.com");
        follower1.setName("FollowerName1");
        follower1.setSurname("FollowerSurname1");
        follower1.setIsActive(true);
        follower1.setPostCount(0L);
        follower1.setFollowingCount(0L);
        follower1.setFollowersCount(0L);
        userRepository.save(follower1);

        follower2 = new User();
        follower2.setUsername("Follower2"+suffix);
        follower2.setPassword("password");
        follower2.setEmail("follower2"+suffix+"@example.com");
        follower2.setName("FollowerName2");
        follower2.setSurname("FollowerSurname2");
        follower2.setIsActive(true);
        follower2.setPostCount(0L);
        follower2.setFollowingCount(0L);
        follower2.setFollowersCount(0L);
        userRepository.save(follower2);
    }
    @AfterEach
    public void tearDown() {
//        groupMembershipRepository.deleteAll();
//        userRepository.deleteAll();
        //relacije pracenja
        userRepository.deleteFollowRelationsForUser(userToFollow.getId());
        userRepository.deleteFollowRelationsForUser(follower1.getId());
        userRepository.deleteFollowRelationsForUser(follower2.getId());

        //grupe
        groupMembershipRepository.deleteByUserId(userToFollow.getId());
        groupMembershipRepository.deleteByUserId(follower1.getId());
        groupMembershipRepository.deleteByUserId(follower2.getId());

        // Zatim obriši same korisnike
        userRepository.deleteById(userToFollow.getId());
        userRepository.deleteById(follower1.getId());
        userRepository.deleteById(follower2.getId());
        userRepository.flush();
    }


    @Test
    public void testConcurrentFollowUserWithSleep() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<?> thread1 = executor.submit(() -> {
            try {
                System.out.println("Thread 1 starting");
                userService.followUser(userToFollow.getId(), follower1.getUsername());
                Thread.sleep(2000); //kasnjenje
            } catch (Exception e) {
                System.err.println("Error in Thread 1: " + e.getMessage());
            }
        });

        Future<?> thread2 = executor.submit(() -> {
            try {
                System.out.println("Thread 2 starting");
                userService.followUser(userToFollow.getId(), follower2.getUsername());
            } catch (Exception e) {
                System.err.println("Error in Thread 2: " + e.getMessage());
            }
        });

        thread1.get();
        thread2.get();

        User updatedUser = userRepository.findById(userToFollow.getId()).orElse(null);
        assertEquals(2, updatedUser.getFollowersCount(),"Followers count should be 2 after both users follow");

    }
}
