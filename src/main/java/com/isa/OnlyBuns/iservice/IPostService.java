package com.isa.OnlyBuns.iservice;

import com.isa.OnlyBuns.dto.PostDTO;
import com.isa.OnlyBuns.model.Post;

public interface IPostService {
    void remove(Integer id);
    void deleteLogically(Integer id);
    Post likePost(Integer postId, Integer userId);
    int getLikesCount(Integer postId);
}
