package com.isa.OnlyBuns.iservice;

import com.isa.OnlyBuns.dto.PostDTO;
import com.isa.OnlyBuns.model.Post;

import java.io.IOException;

public interface IPostService {
    void remove(Integer id);
    Post addNewPost(PostDTO postDTO,String username) throws IOException;
    void deleteLogically(Integer id);
    Post likePost(Integer postId, Integer userId);
    int getLikesCount(Integer postId);
    Long countByUserId(Long userId);
    void removeCache();
}
