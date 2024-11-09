package com.isa.OnlyBuns.controller;

import com.isa.OnlyBuns.dto.PostDTO;
import com.isa.OnlyBuns.model.Post;
import com.isa.OnlyBuns.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value= "api/posts")
public class PostController {

    @Autowired
    private PostService postService;

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
    public ResponseEntity<PostDTO> addNewPost(@RequestBody PostDTO postDTO){
        Post post = new Post();
        post.setDescription(postDTO.getDescription());
        post.setImagePath(postDTO.getImagePath());
        post.setLatitude(postDTO.getLatitude());
        post.setLongitude(postDTO.getLongitude());
        post.setCreatedAt(LocalDateTime.now());
        post.setUserId(postDTO.getUserId());

        Post savedPost = postService.save(post);
        return new ResponseEntity<>(new PostDTO(savedPost), HttpStatus.CREATED);
    }

    @GetMapping(value="/onePost/{id}")
    public ResponseEntity<PostDTO> getOnePost(@PathVariable Integer id) {
        Post post = postService.findOne(id);
        PostDTO postDTO = new PostDTO(post);

        return new ResponseEntity<>(postDTO, HttpStatus.OK);
    }
    



}
