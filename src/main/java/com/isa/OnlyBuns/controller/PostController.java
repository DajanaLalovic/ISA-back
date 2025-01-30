package com.isa.OnlyBuns.controller;
import com.isa.OnlyBuns.dto.PostDTO;
import com.isa.OnlyBuns.model.Location;
import com.isa.OnlyBuns.model.Post;
import com.isa.OnlyBuns.model.User;
import com.isa.OnlyBuns.service.ImageService;
import com.isa.OnlyBuns.service.PostService;
import com.isa.OnlyBuns.service.UserService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value= "/api/posts", produces = MediaType.APPLICATION_JSON_VALUE)

public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private MeterRegistry meterRegistry;

    @GetMapping(value= "/all")
    public ResponseEntity<List<PostDTO>> getAllPosts(){

        List<Post> posts=postService.findAll();
        List<PostDTO> postsDTO=new ArrayList<>();
        for(Post p:posts){
            postsDTO.add(new PostDTO(p));
            System.out.println("Image path for post " + p.getId() + ": " + p.getImagePath());
        }
        return new ResponseEntity<>(postsDTO, HttpStatus.OK);
    }

    @PostMapping(consumes = "application/json")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDTO> addNewPost(@RequestBody PostDTO postDTO, Principal principal) {
        try {
            Timer.Sample sample = Timer.start(meterRegistry);
            Post savedPost = postService.addNewPost(postDTO, principal.getName());
            sample.stop(meterRegistry.timer("http_server_request","method","POST","endpoint","/api/posts"));
            return new ResponseEntity<>(new PostDTO(savedPost), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(value="/onePost/{id}")
    public ResponseEntity<PostDTO> getOnePost(@PathVariable Integer id) {
        Post post = postService.findOne(id);
        PostDTO postDTO = new PostDTO(post);

        return new ResponseEntity<>(postDTO, HttpStatus.OK);
    }
//    @PutMapping(value = "/updatePost/{id}", consumes = "application/json")
//    public ResponseEntity<PostDTO> updatePost(@PathVariable Integer id, @RequestBody PostDTO postDTO) {
//        Post updatedPost = postService.updatePost(id, postDTO);
//        PostDTO updatedPostDTO = new PostDTO(updatedPost);
//        return new ResponseEntity<>(updatedPostDTO, HttpStatus.OK);
//    }
@PutMapping(consumes = "application/json")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<PostDTO> updatePost(@RequestBody PostDTO postDTO, Principal principal) throws IOException {
    User currentUser = userService.findByUsername(principal.getName());
    if (currentUser == null) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    Post post = postService.findOne(postDTO.getId());
    if (post == null) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    if (post.getUserId()!=currentUser.getId()) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    // Ažuriraj polja opisa, latitude i longitude
    post.setDescription(postDTO.getDescription());

    post.setCreatedAt(postDTO.getCreatedAt());
    // Ažuriranje lokacije
    if (postDTO.getLocation() != null) {
        if (post.getLocation() == null) {
            post.setLocation(new Location(postDTO.getLocation().getLatitude(), postDTO.getLocation().getLongitude()));
        } else {
            post.getLocation().setLatitude(postDTO.getLocation().getLatitude());
            post.getLocation().setLongitude(postDTO.getLocation().getLongitude());
        }
    }

    // Proveri da li korisnik želi da zadrži staru sliku ili je uploadovao novu
    if (postDTO.getImageBase64() != null && !postDTO.getImageBase64().isEmpty()) {
        String imageUrl = imageService.saveImage(postDTO.getImageBase64());
        post.setImagePath(imageUrl);
    }
    // Ako je `imageBase64` prazan, koristi postojeći `imagePath`
    // Bez promene putanje do slike

    post.setIsRemoved(postDTO.getIsRemoved());
    post = postService.save(post);

    return new ResponseEntity<>(new PostDTO(post), HttpStatus.OK);
}


    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")  // Pristup dozvoljen samo autentifikovanim korisnicima
    public ResponseEntity<Void> deletePost(@PathVariable Integer id, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Post post = postService.findOne(id);
        if (post == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (post.getUserId()!=currentUser.getId()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        postService.remove(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/deleteLogically/{id}")
    @PreAuthorize("isAuthenticated()")  // Pristup dozvoljen samo autentifikovanim korisnicima
    public ResponseEntity<Void> deleteLogically(@PathVariable Integer id, Principal principal) {
        User currentUser = userService.findByUsername(principal.getName());
        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Post post = postService.findOne(id);
        if (post == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (post.getUserId()!=currentUser.getId()) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        postService.deleteLogically(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/like/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDTO> likePost(@PathVariable Integer postId, Principal principal) {
        try {
            // Prvo pronađi korisnika na osnovu username iz tokena
            User currentUser = userService.findByUsername(principal.getName());
            if (currentUser == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            // Pozovi postService sa ID korisnika iz tokena
            Post likedPost = postService.likePost(postId, currentUser.getId().intValue());
            return new ResponseEntity<>(new PostDTO(likedPost), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/likesCount/{postId}")
    public ResponseEntity<Integer> getLikesCount(@PathVariable Integer postId) {
        try {
            int likesCount = postService.getLikesCount(postId);
            return new ResponseEntity<>(likesCount, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/followed")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PostDTO>> getPostsByFollowedUsers(Principal principal) {
        List<Post> posts = postService.getPostsByFollowedUsers(principal.getName());
        // Konvertujemo entitete u DTO-ove kako bismo izbegli rekurziju
        List<PostDTO> postsDTO = posts.stream()
                .map(PostDTO::new) // Koristi PostDTO konstruktor
                .toList();
        return ResponseEntity.ok(postsDTO);
    }


}


