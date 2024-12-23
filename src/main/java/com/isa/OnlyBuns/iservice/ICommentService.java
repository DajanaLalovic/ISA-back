package com.isa.OnlyBuns.iservice;

import com.isa.OnlyBuns.dto.CommentDTO;
import com.isa.OnlyBuns.model.Comment;
import java.io.IOException;
import java.util.List;

public interface ICommentService {

    void remove(Integer id);
    Comment addNewComment(CommentDTO commentDTO, String username) throws IOException;
    List<Comment> getCommentsForPost(Integer postId);
}
