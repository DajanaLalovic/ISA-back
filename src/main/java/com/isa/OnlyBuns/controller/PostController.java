package com.isa.OnlyBuns.controller;

import com.isa.OnlyBuns.dto.PostDTO;
import com.isa.OnlyBuns.model.Post;
import com.isa.OnlyBuns.model.User;
import com.isa.OnlyBuns.service.ImageService;
import com.isa.OnlyBuns.service.PostService;
import com.isa.OnlyBuns.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
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

    @GetMapping(value= "/all")
    public ResponseEntity<List<PostDTO>> getAllPosts(){

        List<Post> posts=postService.findAll();
        List<PostDTO> postsDTO=new ArrayList<>();
        for(Post p:posts){
            postsDTO.add(new PostDTO(p));
        }
        return new ResponseEntity<>(postsDTO, HttpStatus.OK);
    }

    @PostMapping(consumes = "application/json")
    @PreAuthorize("isAuthenticated()")  // Ova anotacija dozvoljava pristup samo autentifikovanim korisnicima
    public ResponseEntity<PostDTO> addNewPost(@RequestBody PostDTO postDTO, Principal principal) {
        try {
            // Dohvati trenutno autentifikovanog korisnika iz konteksta
            User currentUser = userService.findByUsername(principal.getName());
            if (currentUser == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            Post post = new Post();
            post.setDescription(postDTO.getDescription());
            post.setLatitude(postDTO.getLatitude());
            post.setLongitude(postDTO.getLongitude());
            post.setCreatedAt(LocalDateTime.now());
            post.setUserId(currentUser.getId());  // Postavi ID trenutnog korisnika

            // Proveri da li postoji imageBase64 i sačuvaj sliku
            if (postDTO.getImageBase64() != null && !postDTO.getImageBase64().isEmpty()) {
                String imageUrl = imageService.saveImage(postDTO.getImageBase64());
                post.setImagePath(imageUrl);
            } else {
                post.setImagePath(postDTO.getImagePath());
            }

            post.setComments(new ArrayList<>());
            post.setLikes(new ArrayList<>());

            Post savedPost = postService.save(post);
            return new ResponseEntity<>(new PostDTO(savedPost), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
