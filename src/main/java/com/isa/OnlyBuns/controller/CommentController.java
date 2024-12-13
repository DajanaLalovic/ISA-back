package com.isa.OnlyBuns.controller;

import com.isa.OnlyBuns.dto.CommentDTO;
import com.isa.OnlyBuns.dto.PostDTO;
import com.isa.OnlyBuns.model.Comment;
import com.isa.OnlyBuns.model.Post;
import com.isa.OnlyBuns.service.CommentService;
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
import java.util.List;

@RestController
@RequestMapping(value="api/comments",  produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;


    @PostMapping(consumes = "application/json")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentDTO> addNewComment(@RequestBody CommentDTO commentDTO, Principal principal) {
        try {
            Comment savedComment = commentService.addNewComment(commentDTO, principal.getName());
            return new ResponseEntity<>(new CommentDTO(savedComment), HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
    @GetMapping("/{postId}/comments")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CommentDTO>> getCommentsForPost(@PathVariable Integer postId) {
        try {
            // Pronađi sve komentare za dati post
            List<Comment> comments = commentService.getCommentsForPost(postId);

            // Mapiraj komentare u DTO
            List<CommentDTO> commentDTOs = comments.stream()
                    .map(CommentDTO::new)
                    .toList();

            return new ResponseEntity<>(commentDTOs, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
