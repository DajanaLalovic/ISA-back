package com.isa.OnlyBuns.service;

import com.isa.OnlyBuns.dto.PostDTO;
import com.isa.OnlyBuns.irepository.IPostRepository;
import com.isa.OnlyBuns.irepository.IUserRepository;
import com.isa.OnlyBuns.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.isa.OnlyBuns.model.Post;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


@Service
public class PostService {

    @Autowired
    private IPostRepository postRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private ImageService imageService;

    public Post findOne(Integer id){return postRepository.findById(id).orElseGet(null);}

    public List<Post>  findAll(){return postRepository.findAll();}

    public Page<Post> findAll(Pageable pageable){return postRepository.findAll(pageable);}

    public Post save(Post post){return postRepository.save(post);}

    public void delete(Integer id){postRepository.deleteById(id);}



}
