package com.isa.OnlyBuns.service;


import com.isa.OnlyBuns.dto.UserDTO;
import com.isa.OnlyBuns.enums.UserRole;
import com.isa.OnlyBuns.irepository.IUserRepository;
import com.isa.OnlyBuns.iservice.IPostService;
import com.isa.OnlyBuns.iservice.IRoleService;
import com.isa.OnlyBuns.iservice.IUserService;
import com.isa.OnlyBuns.model.Address;
import com.isa.OnlyBuns.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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


    //private BloomFilter<String> usernameBloomFilter;

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
     /*   u.setRoles(Collections.singleton(UserRole.USER));
        if (u.getRoles() == null || u.getRoles().isEmpty()) {
            u.setRoles(Set.of(UserRole.USER)); // Primer za dodeljivanje jedne podrazumevane uloge

        u.setRoles(new HashSet<>(Arrays.asList(UserRole.USER)));

        }*/
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


    public User save(User user) {return userRepository.save(user);}


    public User convertToUser(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setPassword(userDTO.getPassword()); // Razmislite o enkripciji lozinke pre nego što je postavite
        user.setIsActive(userDTO.getIsActive());  // Ako želite da korisnik bude inaktiviran pri registraciji
       user.setActivationToken(userDTO.getActivationToken());
        return user;
    }
    public UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setSurname(user.getSurname());
        // Ako želite, možete vratiti lozinku ili je sakriti
        userDTO.setPassword(user.getPassword());  // Ipak, preporučuje se da lozinku ne šaljete u DTO
        userDTO.setIsActive(user.getIsActive());  // Ako je ovo potrebno u DTO
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

        return filteredUsers.subList(fromIndex, toIndex);
        // return filteredUsers;
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

   // @Scheduled(cron = "0 0 0 L * ?") //brisanje poslednjeg dana u mesecu-u ponoc
   @Scheduled(cron = "0 */2 * * * ?") // radi provere-brisanje svake dve minute
   public void scheduledCleanUp() {
//        int retentionDays = 30;
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
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists.");
        }
        System.out.println("Saving user: " + user.getUsername());
        User savedUser = userRepository.save(user);
        userRepository.flush();
        System.out.println("User saved: " + savedUser.getUsername());
        return savedUser;
    }
    @Transactional
    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}



