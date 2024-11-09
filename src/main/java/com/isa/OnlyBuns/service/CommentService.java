package com.isa.OnlyBuns.service;

import com.isa.OnlyBuns.irepository.ICommentRepository;
import com.isa.OnlyBuns.model.Comment;
import com.isa.OnlyBuns.model.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private ICommentRepository commentRepository;

    public Comment findOne(Integer id){return commentRepository.findById(id).orElseGet(null);}
    public Comment save(Comment comment){return commentRepository.save(comment);}
}
