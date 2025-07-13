package com.isa.OnlyBuns;

import com.isa.OnlyBuns.irepository.IPostRepository;
import com.isa.OnlyBuns.model.Location;
import com.isa.OnlyBuns.model.Post;
import com.isa.OnlyBuns.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.PessimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
@SpringBootTest
public class PostTests {


    @Autowired
    private PostService postService;

    @Autowired
    private IPostRepository postRepository;
    @Test
    void contextLoads() {
    }

    @Test
    void testConcurrentLikesWithPessimisticLock() throws Throwable {
        Post post = new Post();
        post.setDescription("Test post");
        post.setLocation(new Location(45.2671, 19.8335));
        post.setCreatedAt(LocalDateTime.now());
        post.setUserId(1L);
        post.setImagePath("test-image.jpg");
        post.setIsRemoved(false);
        post.setComments(new ArrayList<>());
        post.setLikes(new ArrayList<>());
        postRepository.save(post);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<?> future1 = executor.submit(() -> {
            System.out.println("Pokreće se Thread 1");
            postService.likePost(post.getId(), 101);
            try {
                Thread.sleep(500); // Produženje trajanja transakcije
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Future<?> future2 = executor.submit(() -> postService.likePost(post.getId(), 102));

        ExecutionException exception = assertThrows(ExecutionException.class, future2::get);
        assertEquals(PessimisticLockingFailureException.class, exception.getCause().getClass());

        executor.shutdown();

        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertEquals(1, updatedPost.getLikes().size(), "Expected only one like due to locking.");
    }
    @Test
    void testConcurrentLikesShouldResultInTwoLikesWithPessimisticLock() throws InterruptedException {
        // Kreiraj testnu objavu
        Post post = new Post();
        post.setDescription("Test post");
        post.setLocation(new Location(45.2671, 19.8335));
        post.setCreatedAt(LocalDateTime.now());
        post.setUserId(1L);
        post.setImagePath("test-image.jpg");
        post.setIsRemoved(false);
        post.setComments(new ArrayList<>());
        post.setLikes(new ArrayList<>());
        postRepository.save(post);

        // Simulacija konkurentnih zahteva
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Dodaj 2 različita korisnika koji lajkuju istu objavu
        Runnable task1 = () -> {
            try {
                System.out.println("Pokreće se Thread 1");
                Thread.sleep(500); // Produžava trajanje transakcije
                postService.likePost(post.getId(), 101);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        Runnable task2 = () -> postService.likePost(post.getId(), 102);

        executor.execute(task1);
        executor.execute(task2);

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        // Provera broja lajkova nakon konkurentnog pristupa
        Post updatedPost = postRepository.findById(post.getId()).orElseThrow();
        assertEquals(2, updatedPost.getLikes().size(), "Broj lajkova treba biti 2");
    }
}
