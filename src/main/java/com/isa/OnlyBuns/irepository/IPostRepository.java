package com.isa.OnlyBuns.irepository;

import com.isa.OnlyBuns.model.Post;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface IPostRepository extends JpaRepository<Post, Integer> {


    public Post findByDescription(String description);

    public Page<Post> findAll(Pageable pageable);

    public List<Post> findPostByDescription(String description);

    Long countByUserId(Long userId);

    List<Post> findByUserIdInOrderByCreatedAtDesc(List<Long> userIds);


    long count();
    int countByCreatedAtAfter(LocalDateTime date); //arijanina

    @Query("SELECT p FROM Post p WHERE p.createdAt > :sevenDaysAgo ORDER BY SIZE(p.likes) DESC")
    List<Post> findTop5MostLikedPostsLast7Days(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo, Pageable pageable);

    @Query("SELECT p FROM Post p ORDER BY SIZE(p.likes) DESC")
    List<Post> findTop10MostLikedPosts(Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Post p WHERE p.id = :id")
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value ="0")})
    Post findPostForUpdate(@Param("id") Integer id);
}
