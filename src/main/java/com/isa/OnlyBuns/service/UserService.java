package com.isa.OnlyBuns.service;


import com.isa.OnlyBuns.dto.UserDTO;
import com.isa.OnlyBuns.enums.UserRole;
import com.isa.OnlyBuns.irepository.IUserRepository;
import com.isa.OnlyBuns.iservice.IPostService;
import com.isa.OnlyBuns.iservice.IRoleService;
import com.isa.OnlyBuns.iservice.IUserService;
import com.isa.OnlyBuns.model.Address;
import com.isa.OnlyBuns.model.User;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService {
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    @Lazy
    private IPostService postService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IRoleService roleService;

    @Autowired
    private BloomFilterService bloomFilterService;


    public String generateActivationToken() {
        return UUID.randomUUID().toString();
    }
    private final Map<String, RateLimiter> userFollowLimits = new ConcurrentHashMap<>();

    private static final int FOLLOW_LIMIT = 3;
    //    private static final int FOLLOW_LIMIT = 50;-inace
    private static class RateLimiter {
        private AtomicInteger followCount = new AtomicInteger(0);
        private long lastResetTime = System.currentTimeMillis();

        public boolean canFollow() {
            long currentTime = System.currentTimeMillis();

            if (currentTime - lastResetTime > 60000) {
                followCount.set(0);
                lastResetTime = currentTime;
            }
           // return followCount.incrementAndGet() <= 50;
            return followCount.incrementAndGet() <= 3; // maksimalno 3 pracenja po minuti-inicijalno treba 50
        }
    }
    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public User findById(Long id) throws AccessDeniedException {
        return userRepository.findById(id).orElseGet(null);
    }

    public User findById1(Long id) throws AccessDeniedException {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> findAll() throws AccessDeniedException {
        return userRepository.findAll();
    }
/*
    @Override
    public User save(UserDTO userRequest) {
        User u = new User();
        u.setUsername(userRequest.getUsername());

        // pre nego sto postavimo lozinku u atribut hesiramo je kako bi se u bazi nalazila hesirana lozinka
        // treba voditi racuna da se koristi isi password encoder bean koji je postavljen u AUthenticationManager-u kako bi koristili isti algoritam
        u.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        u.setName(userRequest.getName());
        u.setSurname(userRequest.getSurname());
        u.setIsActive(userRequest.getIsActive());
        if (u.getIsActive() == null) {
            u.setIsActive(false);  // Ili true, zavisno od tvoje logike
        }

        u.setEmail(userRequest.getEmail());
        u.setActivationToken(userRequest.getActivationToken());
        u.setRole(UserRole.USER);
        // u primeru se registruju samo obicni korisnici i u skladu sa tim im se i dodeljuje samo rola USER

        Address address = new Address();
        address.setStreet(userRequest.getStreet());
        address.setNumber(userRequest.getNumber());
        address.setCity(userRequest.getCity());
        address.setPostalCode(userRequest.getPostalCode());
        address.setCountry(userRequest.getCountry());
        u.setActivationSentAt(userRequest.getActivationSentAt());
        u.setFollowersCount(userRequest.getFollowersCount());
        u.setAddress(address);

        return this.userRepository.save(u);
    }
*/
@Override
public User save(UserDTO userRequest) {
  /*  // Provera korisničkog imena pomoću Bloom filtera
    if (bloomFilterService.mightContain(userRequest.getUsername())) {
        // Dodatna provera u bazi u slučaju false positive
        throw new IllegalArgumentException("Username already exists in bloom.");

    }else{
        if (userRepository.findByUsername(userRequest.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists.");
        }

    }*/
        boolean mightContain = bloomFilterService.mightContain(userRequest.getUsername());

        if (mightContain) {
            throw new IllegalArgumentException("Username already exists in bloom.");
        } else {
            boolean userExists = userRepository.findByUsername(userRequest.getUsername()) != null;

            if (userExists) {
                throw new IllegalArgumentException("Username already exists.");
            }
        }
    // Kreiranje novog korisnika
    User u = new User();
    u.setUsername(userRequest.getUsername());

    // Hesiraj lozinku pre čuvanja
    u.setPassword(passwordEncoder.encode(userRequest.getPassword()));

    u.setName(userRequest.getName());
    u.setSurname(userRequest.getSurname());
    u.setIsActive(userRequest.getIsActive());
    if (u.getIsActive() == null) {
        u.setIsActive(false);  // Podrazumevana logika za aktivaciju
    }

    u.setEmail(userRequest.getEmail());
    u.setActivationToken(userRequest.getActivationToken());
    u.setRole(UserRole.USER);  // Postavljanje korisničke role
    u.setFollowersCount(0L);
    u.setFollowingCount(0L);
    u.setLastLogin(null);

    Address address = new Address();
    address.setStreet(userRequest.getStreet());
    address.setNumber(userRequest.getNumber());
    address.setCity(userRequest.getCity());
    address.setPostalCode(userRequest.getPostalCode());
    address.setCountry(userRequest.getCountry());
    u.setActivationSentAt(userRequest.getActivationSentAt());

    u.setAddress(address);

    // Dodaj korisničko ime u Bloom filter
    bloomFilterService.addUsername(userRequest.getUsername());

    // Sačuvaj korisnika u bazi
    return this.userRepository.save(u);
}


    public User save(User user) {return userRepository.save(user);}


    public User convertToUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setPassword(userDTO.getPassword());
        user.setIsActive(userDTO.getIsActive());
       user.setActivationToken(userDTO.getActivationToken());
        return user;
    }
    public UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setSurname(user.getSurname());
        userDTO.setPassword(user.getPassword());
        userDTO.setIsActive(user.getIsActive());
        userDTO.setActivationToken(user.getActivationToken());
        userDTO.setFollowingCount(user.getFollowingCount());
       // userDTO.setPostCount(user.getPostCount());
        userDTO.setActivationSentAt(user.getActivationSentAt());
        return userDTO;
    }
    public User findByActivationToken(String activationToken) {
        return userRepository.findByActivationToken(activationToken);
    }
    public void updateUser(User user) {
        userRepository.save(user);
    }


    public List<User> searchUsers(String name, String surname, String email, Long minPostCount, Long maxPostCount, String sortBy, String sortOrder,int page,int size) {
        List<User> users = userRepository.findAll();

        List<User> filteredUsers = users.stream()
                .filter(user -> name == null || user.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(user -> surname == null || user.getSurname().toLowerCase().contains(surname.toLowerCase()))
                .filter(user -> email == null || user.getEmail().toLowerCase().contains(email.toLowerCase()))
                .filter(user -> {
                    Long postCount = postService.countByUserId(user.getId());
                    user.setPostCount(postCount);
                    return (minPostCount == null || postCount >= minPostCount) &&
                            (maxPostCount == null || postCount <= maxPostCount);
                })
                .collect(Collectors.toList());

        if ("followingCount".equals(sortBy)) {
            if ("desc".equals(sortOrder)) {
                filteredUsers.sort(Comparator.comparing(User::getFollowingCount).reversed());
            } else {
                filteredUsers.sort(Comparator.comparing(User::getFollowingCount));
            }
        } else if ("email".equals(sortBy)) {
            if ("desc".equals(sortOrder)) {
                filteredUsers.sort(Comparator.comparing(User::getEmail).reversed());
            } else {
                filteredUsers.sort(Comparator.comparing(User::getEmail));
            }
        }
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, filteredUsers.size());

        if (fromIndex > filteredUsers.size()) {
            return new ArrayList<>();
        }

//        return filteredUsers.subList(fromIndex, toIndex);
        List<User> paginatedUsers = filteredUsers.subList(fromIndex, toIndex);

        for (User user : paginatedUsers) {
            user.setPassword(null);
            user.setFollowers(new HashSet<>());
            user.setFollowing(new HashSet<>());
            user.setGroups(new HashSet<>());
            user.setAddress(null);
        }

        return paginatedUsers;
        // return filteredUsers;
    }

    @Override
    public List<User> findUsersByLastLoginBefore(LocalDateTime date) {
        return userRepository.findByLastLoginBefore(date);
    }

//    @PostConstruct -testiranje brisanja naloga starijih od 30 dana
//    public void cleanUpInactiveAccountsOnStartup() {
//        int retentionDays = 30;
//        deleteInactiveAccounts(retentionDays);
//        System.out.println("Deleted inactive accounts on application startup.");
//    }
    public List<User> findInactiveAccountsOlderThan() {
//        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(days);

        return userRepository.findAll().stream()
                .filter(user -> !Boolean.TRUE.equals(user.getIsActive()) &&
                        user.getActivationSentAt() != null)
//                        user.getActivationSentAt().isBefore(cutoffDate))
                .collect(Collectors.toList());
    }

    public void deleteInactiveAccounts() {
        List<User> inactiveUsers = findInactiveAccountsOlderThan();

        for (User user : inactiveUsers) {
            userRepository.deleteById(user.getId());
        }

        System.out.println("Deleted inactive accounts older than  days.");
    }

    @Scheduled(cron = "0 0 0 L * ?") //brisanje poslednjeg dana u mesecu-u ponoc
//   @Scheduled(cron = "0 */2 * * * ?") // radi provere-brisanje svake dve minute
   public void scheduledCleanUp() {
        deleteInactiveAccounts();
    }

    //pracenje
    @Transactional
    public void followUser(Long userId, String currentUsername) {
        User currentUser = findByUsername(currentUsername);
        User userToFollow = userRepository.findByIdWithLock(userId); // zakljucavanje-za konkurentno

        if (currentUser.equals(userToFollow)) {
            throw new IllegalArgumentException("You cannot follow yourself.");
        }
        if (currentUser.getFollowing().contains(userToFollow)) {
            throw new IllegalArgumentException("You are already following this user.");
        }

        // limiter na 3 pracenja po minuti
        userFollowLimits.putIfAbsent(currentUsername, new RateLimiter());
        if (!userFollowLimits.get(currentUsername).canFollow()) {
            throw new IllegalArgumentException("Follow limit exceeded. Please wait a minute.");
        }

        currentUser.getFollowing().add(userToFollow);
        userToFollow.getFollowers().add(currentUser);

        currentUser.setFollowingCount((long) currentUser.getFollowing().size());
        userToFollow.setFollowersCount((long) userToFollow.getFollowers().size());

        save(currentUser);
        save(userToFollow);
    }

    //otpracivanje
    public void unfollowUser(Long userId, String currentUsername) {
        User currentUser = findByUsername(currentUsername);
        User userToUnfollow = findById(userId);

        if (!currentUser.getFollowing().contains(userToUnfollow)) {
            throw new IllegalArgumentException("You are not following this user.");
        }

        currentUser.getFollowing().remove(userToUnfollow);
        userToUnfollow.getFollowers().remove(currentUser);

        currentUser.setFollowingCount((long) currentUser.getFollowing().size());
        userToUnfollow.setFollowersCount((long) userToUnfollow.getFollowers().size());

        save(currentUser);
        save(userToUnfollow);
    }
    public boolean isFollowing(Long targetUserId, String username) {
        User currentUser = userRepository.findByUsername(username);
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found"));
        return targetUser.getFollowers().contains(currentUser);
    }


    public void updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

   @Transactional
   public User registerUser(User user) {
       try {
           System.out.println("Saving user: " + user.getUsername());
           User savedUser = userRepository.saveAndFlush(user);
           System.out.println("User saved: " + savedUser.getUsername());
           return savedUser;
       } catch (DataIntegrityViolationException e) {
           System.out.println("Failed to save user: " + user.getUsername() + " - Username already exists.");
           throw new IllegalArgumentException("Username already exists.");
       }
   }


    @Transactional
    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public List<User> getFollowers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        for (User follower : user.getFollowers()) {
            follower.setFollowers(new HashSet<>());
            follower.setFollowing(new HashSet<>());
        }

        return new ArrayList<>(user.getFollowers());
    }

    public List<User> getFollowing(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        for (User followedUser : user.getFollowing()) {
            followedUser.setFollowers(new HashSet<>());
            followedUser.setFollowing(new HashSet<>());
        }

        return new ArrayList<>(user.getFollowing());
    }

    public List<Map<String, Object>> getAllBasicUserInfo() {
        return userRepository.findAll().stream().map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("username", user.getUsername());
            map.put("name", user.getName());
            map.put("surname", user.getSurname());
            map.put("email", user.getEmail());
            map.put("isActive", user.getIsActive());
            map.put("role",user.getRole());
            return map;
        }).toList();
    }

}



