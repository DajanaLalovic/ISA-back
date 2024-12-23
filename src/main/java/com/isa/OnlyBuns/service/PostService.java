package com.isa.OnlyBuns.service;
import com.isa.OnlyBuns.dto.PostDTO;
import com.isa.OnlyBuns.irepository.IPostLikeRepository;
import com.isa.OnlyBuns.irepository.IPostRepository;
import com.isa.OnlyBuns.irepository.IUserRepository;
import com.isa.OnlyBuns.iservice.IPostService;
import com.isa.OnlyBuns.model.PostLike;
import com.isa.OnlyBuns.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.isa.OnlyBuns.model.Post;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class PostService implements IPostService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IPostRepository postRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    private IPostLikeRepository postLikeRepository;

    @Autowired
    @Lazy
    private UserService userService;

    public Post findOne(Integer id){return postRepository.findById(id).orElseGet(null);}

    public List<Post>  findAll(){return postRepository.findAll();}

    public Page<Post> findAll(Pageable pageable){return postRepository.findAll(pageable);}

    @CacheEvict(value = {"totalPosts", "postsLast30Days", "top5Last7Days", "top10AllTime"}, allEntries = true)
    public Post save(Post post){return postRepository.save(post);}

    public void delete(Integer id){postRepository.deleteById(id);}

    public Post updatePost(Integer id, PostDTO postDTO) {
        Post existingPost = postRepository.findById(id).orElse(null);
        if (existingPost == null) {
            return null;
        }

        existingPost.setDescription(postDTO.getDescription());
        existingPost.setImagePath(postDTO.getImagePath());
        existingPost.setLatitude(postDTO.getLatitude());
        existingPost.setLongitude(postDTO.getLongitude());
        existingPost.setCreatedAt(postDTO.getCreatedAt());
        existingPost.setUserId(postDTO.getUserId());

        return postRepository.save(existingPost);
    }
    public void remove(Integer id) {
        postRepository.deleteById(id);
    }

    public void deleteLogically(Integer id) {
        Post post = postRepository.findById(id).orElse(null);
        if (post != null) {
            post.setIsRemoved(true);
            postRepository.save(post);
        }
    }
    @Transactional(readOnly = false)
    public Post likePost(Integer postId, Integer userId) {
        logger.info("> Trying to like post with ID: {}", postId);

        // Pesimističko zaključavanje
        Post post = postRepository.findPostForUpdate(postId);

        if (!post.getLikes().contains(userId)) {
            post.getLikes().add(userId);

            PostLike postLike = new PostLike(postId,userId, LocalDateTime.now());
            postLikeRepository.save(postLike);
            postRepository.save(post);
            logger.info("User {} liked post {}", userId, postId);
        } else {
            logger.info("User {} already liked post {}", userId, postId);
        }

        return post;
    }

    public int getLikesCount(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        return post.getLikes().size();
    }
    public Long countByUserId(Long userId) {
        return postRepository.countByUserId(userId);
    }


    public Post addNewPost(PostDTO postDTO, String username) throws IOException {
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new IllegalArgumentException("Korisnik nije autentifikovan");
        }

        Post post = new Post();
        post.setDescription(postDTO.getDescription());
        post.setLatitude(postDTO.getLatitude());
        post.setLongitude(postDTO.getLongitude());
        post.setCreatedAt(LocalDateTime.now());
        post.setUserId(currentUser.getId());

        // Validacija i čuvanje slike ako je dostupna
        if (postDTO.getImageBase64() != null && !postDTO.getImageBase64().isEmpty()) {
            String imageUrl = imageService.saveImage(postDTO.getImageBase64());
            post.setImagePath(imageUrl);
        } else {
            post.setImagePath(postDTO.getImagePath());
        }

        post.setIsRemoved(false);
        post.setComments(new ArrayList<>());
        post.setLikes(new ArrayList<>());

        return postRepository.save(post);
    }

    public List<Post> getPostsByFollowedUsers(String username) {
        User currentUser = userService.findByUsername(username);
        List<Long> followedUserIds = currentUser.getFollowing()
                .stream()
                .map(User::getId)
                .toList();

        // Pretraga objava po ID-evima korisnika
        return postRepository.findByUserIdInOrderByCreatedAtDesc(followedUserIds);
    }

    @Cacheable("totalPosts")
    public long getTotalPosts() {
        return postRepository.count();
    }
    @Cacheable("postsLast30Days")
    public long countPostsLastMonth() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minus(1, ChronoUnit.MONTHS);
        return Long.valueOf(postRepository.countByCreatedAtAfter(oneMonthAgo));
    }
    @Cacheable("top5Last7Days")
    public List<Post> getTop5MostLikedPostsLast7Days() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        Pageable pageable = PageRequest.of(0, 5); // Ograničavamo na 5 objava
        return postRepository.findTop5MostLikedPostsLast7Days(sevenDaysAgo, pageable);
    }
    @Cacheable("top10AllTime")
    public List<Post> getTop10MostLikedPosts() {
        Pageable pageable = PageRequest.of(0, 10); // Ograničavamo na 10 objava
        return postRepository.findTop10MostLikedPosts(pageable);
    }


}
