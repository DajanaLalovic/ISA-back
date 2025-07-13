package com.isa.OnlyBuns;
import com.isa.OnlyBuns.enums.UserRole;
import com.isa.OnlyBuns.iservice.IUserService;
import com.isa.OnlyBuns.model.Address;
import com.isa.OnlyBuns.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private IUserService userService;

    @Test
    public void testConcurrentUserRegistration() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        Address address1 = new Address(
                null, // ID (ostavi null jer će ga generisati baza)
                "123 Main Street", // Ulica
                "45A", // Broj
                "Springfield", // Grad
                "11000", // Poštanski broj
                "USA" // Država
        );

        Address address2 = new Address(
                null,
                "456 Elm Street",
                "78B",
                "Shelbyville",
                "22000",
                "USA"
        );
        User user1 = new User(
                null, // ID (ostavi null jer će ga generisati baza)
                "John", // Ime
                "Doe", // Prezime
                "john.doe@example.com", // Email
                "testuser", // Korisničko ime
                true, // Aktivnost
                "password123", // Lozinka
                UUID.randomUUID().toString(), // Aktivacioni token
                UserRole.USER, // Uloga
                address1, // Adresa
                0L, // Broj postova
                0L ,// Broj pratilaca
                0L //br following
        );

        User user2 = new User(
                null, // ID
                "Jane", // Ime
                "Smith", // Prezime
                "jane.smith@example.com", // Email
                "testuser", // Korisničko ime
                true, // Aktivnost
                "password456", // Lozinka
                UUID.randomUUID().toString(), // Aktivacioni token
                UserRole.USER, // Uloga
                address2, // Adresa
                0L, // Broj postova
                0L ,// Broj pratilaca
                0L //br following

        );

        AtomicInteger failedAttempts = new AtomicInteger(0);

        Runnable task1 = () -> {
            try {
                Thread.sleep(100); // Simulacija konkurencije
                userService.registerUser(user1);
            } catch (Exception e) {
                failedAttempts.incrementAndGet();
            } finally {
                latch.countDown();
            }
        };

        Runnable task2 = () -> {
            try {
                Thread.sleep(200); // Simulacija konkurencije
                userService.registerUser(user2);
            } catch (Exception e) {
                failedAttempts.incrementAndGet();
            } finally {
                latch.countDown();
            }
        };

        executor.execute(task1);
        executor.execute(task2);

        latch.await();
        executor.shutdown();

        // Proveri da li je kreiran samo jedan korisnik
        User user = userService.findByUsername("testuser");
        assertNotNull(user, "User should be created");

        // Proveri da je jedan pokušaj neuspešan
        assertEquals(1, failedAttempts.get(), "One registration should fail due to conflict");
    }
    @AfterEach
    public void cleanup() {
        User user = userService.findByUsername("testuser");
        if (user != null) {
            userService.deleteUserById(user.getId());
        }
    }
}

