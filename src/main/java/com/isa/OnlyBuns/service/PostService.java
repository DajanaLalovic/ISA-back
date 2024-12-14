package com.isa.OnlyBuns.service;
import com.isa.OnlyBuns.dto.PostDTO;
import com.isa.OnlyBuns.irepository.IPostRepository;
import com.isa.OnlyBuns.irepository.IUserRepository;
import com.isa.OnlyBuns.iservice.IPostService;
import com.isa.OnlyBuns.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.isa.OnlyBuns.model.Post;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class PostService implements IPostService {

    @Autowired
    private IPostRepository postRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private ImageService imageService;

    @Autowired
    @Lazy
    private UserService userService;

    public Post findOne(Integer id){return postRepository.findById(id).orElseGet(null);}

    public List<Post>  findAll(){return postRepository.findAll();}

    public Page<Post> findAll(Pageable pageable){return postRepository.findAll(pageable);}

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
    public Post likePost(Integer postId, Integer userId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new IllegalArgumentException("Post not found");
        }
        //da ne sme user dva put isto lajkovati
        if (!post.getLikes().contains(userId)) {
            post.getLikes().add(userId);
            postRepository.save(post);
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
                .map(User::getId) // Mapiranje korisnika na njihove ID-eve
                .collect(Collectors.toList());

        // Pretraga objava po ID-evima korisnika
        return postRepository.findByUserIdInOrderByCreatedAtDesc(followedUserIds);
    }



}
