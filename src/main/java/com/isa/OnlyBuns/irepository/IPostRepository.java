package com.isa.OnlyBuns.irepository;

import com.isa.OnlyBuns.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface IPostRepository extends JpaRepository<Post, Integer> {


    public Post findByDescription(String description);

    public Page<Post> findAll(Pageable pageable);

    public List<Post> findPostByDescription(String description);

    Long countByUserId(Long userId);

}
