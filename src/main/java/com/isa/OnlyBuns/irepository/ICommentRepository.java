package com.isa.OnlyBuns.irepository;

import com.isa.OnlyBuns.model.Comment;
import com.isa.OnlyBuns.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICommentRepository extends JpaRepository<Comment, Integer> {

}
