package com.isa.OnlyBuns.service;

import com.isa.OnlyBuns.dto.CommentDTO;
import com.isa.OnlyBuns.irepository.ICommentRepository;
import com.isa.OnlyBuns.irepository.IPostRepository;
import com.isa.OnlyBuns.irepository.IUserRepository;
import com.isa.OnlyBuns.iservice.ICommentService;
import com.isa.OnlyBuns.model.Comment;
import com.isa.OnlyBuns.model.Post;
import com.isa.OnlyBuns.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService implements ICommentService {
    @Autowired
    private ICommentRepository commentRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IPostRepository postRepository;
    @Autowired
    private RateLimiterService rateLimiterService;


    public Comment findOne(Integer id){return commentRepository.findById(id).orElseGet(null);}
    public Comment save(Comment comment){return commentRepository.save(comment);}
    @Override
    public void remove(Integer id) {
        commentRepository.deleteById(id);
    }
    public List<Comment> findAll(){return commentRepository.findAll();}

    @Override
    public Comment addNewComment(CommentDTO commentDTO, String username) throws IOException {
        User currentUser = userRepository.findByUsername(username);
        if (currentUser == null) {
            throw new IllegalArgumentException("Korisnik nije autentifikovan");
        }
        // Provera da li je korisnik premašio limit od 5 komentara po minuti
        if (!rateLimiterService.allowRequest(currentUser.getId())) {
            throw new IllegalStateException("Prekoračili ste limit od 5 komentara u poslednjem minutu.");
        }

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        int commentCount = commentRepository.countByUserIdAndCreatedAtAfter(currentUser.getId(), oneHourAgo);
        if (commentCount >= 60) {
            throw new IllegalArgumentException("Prekoračili ste limit od 60 komentara u poslednjih sat vremena.");
        }

        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Objava nije pronađena"));

        User postOwner = userRepository.findById(post.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Vlasnik objave nije pronađen."));

        // Provera da li trenutni korisnik prati vlasnika objave
        if (!postOwner.getFollowers().contains(currentUser)) {
            throw new IllegalStateException("Komentarisanje je dozvoljeno samo na objavama naloga koje pratite.");
        }
        Comment comment = new Comment();
        comment.setText(commentDTO.getText());
        comment.setUserId(currentUser.getId());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);
        return savedComment;
    }

    public List<Comment> getCommentsForPost(Integer postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Objava nije pronađena"));

        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }



}
