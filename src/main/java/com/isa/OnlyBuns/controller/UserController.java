package com.isa.OnlyBuns.controller;


import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.isa.OnlyBuns.enums.UserRole;
import com.isa.OnlyBuns.model.User;
import com.isa.OnlyBuns.service.BloomFilterService;
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
    @Autowired
    private BloomFilterService bloomFilterService;

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
   public ResponseEntity<?> checkUsername(@PathVariable String username) {
       // Prvo proveri Bloom filter
       if (bloomFilterService.mightContain(username)) {
           // Ako filter kaže da možda postoji, uradi dodatnu proveru u bazi zbog mogućih false positive
           User user = this.userService.findByUsername(username);
           if (user != null) {
               return ResponseEntity.ok(user);
           } else {
               // Bloom filter je false positive, username nije u bazi
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
           }
       } else {
           // Filter kaže da sigurno ne postoji - ne proveravaj bazu
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

@GetMapping("/user/search")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<List<User>> searchUsers(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String surname,
        @RequestParam(required = false) String email,
        @RequestParam(required = false) Long minPostCount,
        @RequestParam(required = false) Long maxPostCount,
        @RequestParam(required = false) String sortBy,
        @RequestParam(required = false) String sortOrder,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
) {
    List<User> allFilteredUsers = userService.searchUsers(name, surname, email, minPostCount, maxPostCount, sortBy, sortOrder, page, size);
    int totalUsers = allFilteredUsers.size();

    List<User> filteredUsers = userService.searchUsers(name, surname, email, minPostCount, maxPostCount, sortBy, sortOrder, 0, Integer.MAX_VALUE);
    totalUsers = filteredUsers.size();

    int fromIndex = page * size;
    int toIndex = Math.min(fromIndex + size, totalUsers);
    List<User> paginatedUsers = filteredUsers.subList(fromIndex, toIndex);

    return ResponseEntity.ok()
            .header("X-Total-Count", String.valueOf(totalUsers))  
            .body(paginatedUsers);
}

    @PostMapping("/follow/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> followUser(@PathVariable Long id, Principal principal) {
        System.out.println("Principal: " + principal.getName());
        System.out.println("User ID to follow: " + id);
        try {
            userService.followUser(id, principal.getName());
            return ResponseEntity.ok("Successfully followed the user.");
        } catch (IllegalArgumentException e) {
            if ("Follow limit exceeded. Please wait a minute.".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/unfollow/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> unfollowUser(@PathVariable Long id, Principal principal) {
        try {
            userService.unfollowUser(id, principal.getName());
            return ResponseEntity.ok("Successfully unfollowed the user.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/follow/status/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Long id, Principal principal) {
        try {
            boolean isFollowing = userService.isFollowing(id, principal.getName());
            return ResponseEntity.ok(isFollowing);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
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




    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<User>> getFollowers(@PathVariable Long userId) {
        try {
            List<User> followers = userService.getFollowers(userId);
            return ResponseEntity.ok(followers);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @GetMapping("/{userId}/following")
    public ResponseEntity<List<User>> getFollowing(@PathVariable Long userId) {
        try {
            List<User> following = userService.getFollowing(userId);
            return ResponseEntity.ok(following);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Map<String, Object>>> getAllBasicUsers() {
        List<Map<String, Object>> basicUsers = userService.getAllBasicUserInfo();
        return ResponseEntity.ok(basicUsers);
    }



}
