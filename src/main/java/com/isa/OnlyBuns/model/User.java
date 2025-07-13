package com.isa.OnlyBuns.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.isa.OnlyBuns.enums.UserRole;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "USERS")
public class User implements UserDetails, Serializable {

    //private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "name", nullable = false)
    private String name;

    @Column(name= "surname", nullable = false)
    private String surname;

    // @Column(nullable = false)
    // private String address;

    @Column(name= "email", nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Boolean active = false;

    @Column(name = "activation_token")
    private String activationToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @Column(name = "post_count", nullable = false)
    private Long postCount;

    @Column(name = "following_count", nullable = false)
    private Long followingCount ;
    @Column(name = "followers_count", nullable = false)
    private Long followersCount ;


    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "activation_sent_at")
    private LocalDateTime activationSentAt; // Vreme kada je poslat aktivacioni mejl

    //grupe za cetovanje
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_groups",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id")
    )
    private Set<Group> groups = new HashSet<>(); //grupe gde je korisnik clan -ne nzam da li ce mi trebati to?
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH})
    @JoinTable(
            name = "user_following",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "following_id", referencedColumnName = "id")
    )
    private Set<User> following = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH})
    @JoinTable(
            name = "user_followers",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id", referencedColumnName = "id")
    )
    private Set<User> followers = new HashSet<>();

    public User() {
        this.postCount = 0L;
        this.followingCount = 0L;}

    public User( String username, String password) {

        this.username = username;
        this.password = password;
        this.active = false;
    }

    public User(Long id, String name, String surname, String email, String username, Boolean active, String password, String activationToken, UserRole role, Address address, Long postCount, Long followingCount) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.username = username;
        this.active = active;
        this.password = password;
        this.activationToken = activationToken;
        this.role = role;
        this.address = address;
        this.postCount = postCount;
        this.followingCount = followingCount;
    }
    public User(Long id, String name, String surname, String email, String username, Boolean active, String password, String activationToken, UserRole role, Address address, Long postCount, Long followingCount,Long followersCount) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.username = username;
        this.active = active;
        this.password = password;
        this.activationToken = activationToken;
        this.role = role;
        this.address = address;
        this.postCount = postCount;
        this.followingCount = followingCount;
        this.followersCount=followersCount;
    }

    // Getteri i setteri
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsActive() {
        return active;
    }

    public void setIsActive(Boolean active) {
        this.active = active;
    }

    public String getActivationToken() {
        return activationToken;
    }

    public void setActivationToken(String activationToken) {
        this.activationToken = activationToken;
    }


    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
    /*@JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }
*/
    public long getPostCount() {return postCount;}
    public void setPostCount(Long postCount) {this.postCount = postCount; }

    public long getFollowingCount() {return followingCount;}
    public void setFollowingCount(Long followingCount) {this.followingCount = followingCount; }

    public LocalDateTime getActivationSentAt() { return activationSentAt;  }
    public void setActivationSentAt(LocalDateTime activationSentAt) { this.activationSentAt = activationSentAt; }

    public Set<User> getFollowing() { return following; }
    public void setFollowing(Set<User> following) { this.following = following; }
    public Set<User> getFollowers() { return followers; }

    public void setFollowers(Set<User> followers) { this.followers = followers;}
    public Long getFollowersCount() { return followersCount; }
    public void setFollowersCount(Long followersCount) {this.followersCount = followersCount; }

    public Set<Group> getGroups(){return groups;}
    public void setGroups(Set<Group> groups){this.groups = groups;}
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return  Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash( username);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", active=" + active + '\''+
                ", postCount=" + postCount +'\''+
                ", followingCount=" + followingCount +'\''+
                ", activationSentAt=" + activationSentAt +'\''+
                ", followersCount=" + followersCount +'\''+
                '}';
    }
}

