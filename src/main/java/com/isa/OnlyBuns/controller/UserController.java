package com.isa.OnlyBuns.controller;


import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isa.OnlyBuns.enums.UserRole;
import com.isa.OnlyBuns.model.User;
import com.isa.OnlyBuns.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;


import com.isa.OnlyBuns.iservice.IUserService;


// Primer kontrolera cijim metodama mogu pristupiti samo autorizovani korisnici
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)

public class UserController {


    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PostService postService;

    @Autowired
    private IUserService userService;

    // Za pristup ovoj metodi neophodno je da ulogovani korisnik ima ADMIN ulogu
    // Ukoliko nema, server ce vratiti gresku 403 Forbidden
    // Korisnik jeste autentifikovan, ali nije autorizovan da pristupi resursu
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")

    public User loadById(@PathVariable Long userId) {
        return this.userService.findById(userId);
    }
    @GetMapping("/profile/{userId}")
    public User loadByIdProfile(@PathVariable Long userId) {
        return this.userService.findById1(userId);
    }

    @GetMapping("/user/all")
    //@PreAuthorize("hasRole('ADMIN')")
    @PreAuthorize("isAuthenticated()")
    public List<User> loadAll() {
        return this.userService.findAll();
    }

    @GetMapping("/whoami")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public User user(Principal user) {
        return this.userService.findByUsername(user.getName());
    }

    @GetMapping("/foo")
    public Map<String, String> getFoo() {
        Map<String, String> fooObj = new HashMap<>();
        fooObj.put("foo", "bar");
        return fooObj;
    }
    @GetMapping("/getOneUser/{id}")
    public User loadOneById(@PathVariable Long id) {
        return this.userService.findById(id);
    }

    @GetMapping("/user-by-username/{username}")
    @PreAuthorize("isAuthenticated()")
    public User findByUsername(@PathVariable String username) {return this.userService.findByUsername(username);}

    @GetMapping("/check-by-username/{username}")
    public ResponseEntity<User> checkUsername(@PathVariable String username) {
        User user = this.userService.findByUsername(username);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/user-whoami")
    @PreAuthorize("isAuthenticated()")
    public User getCurrentUser(Principal principal) {
        return userService.findByUsername(principal.getName());
    }

    @GetMapping("/user/postCount/{userId}")
    @PreAuthorize("isAuthenticated()")
    public Long getUserPostCount(@PathVariable Long userId) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return postService.countByUserId(user.getId());
    }
    @GetMapping("/user/{userId}/role")
    @PreAuthorize("isAuthenticated()")
    public UserRole getUserRoleById(@PathVariable Long userId) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user.getRole();
    }
//    @GetMapping("/user/search")
//    @PreAuthorize("isAuthenticated()")
//    public List<User> searchUsers(
//            @RequestParam(value = "name", required = false) String name,
//            @RequestParam(value = "surname", required = false) String surname,
//            @RequestParam(value = "email", required = false) String email,
//            @RequestParam(value = "minPostCount", required = false) Long minPostCount,
//            @RequestParam(value = "maxPostCount", required = false) Long maxPostCount
//    ) {
//        return userService.searchUsers(name, surname, email, minPostCount, maxPostCount);
//    }
    @GetMapping("/user/search")
    @PreAuthorize("isAuthenticated()")
    public List<User> searchUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long minPostCount,
            @RequestParam(required = false) Long maxPostCount,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder
    ) {
        return userService.searchUsers(name, surname, email, minPostCount, maxPostCount, sortBy, sortOrder);
    }

    @PutMapping("/profile/{userId}/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updatePassword(@PathVariable Long userId, @RequestBody Map<String, String> request, Principal principal) {
        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("New password is required");
        }

        userService.updatePassword(userId, newPassword);
        return ResponseEntity.ok("Password updated successfully");
    }



}
