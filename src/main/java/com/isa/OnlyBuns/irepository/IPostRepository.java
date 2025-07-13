package com.isa.OnlyBuns.irepository;

import com.isa.OnlyBuns.model.Comment;
import com.isa.OnlyBuns.model.Post;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

public interface IPostRepository extends JpaRepository<Post, Integer> {


    public Page<Post> findAll(Pageable pageable);
    Long countByUserId(Long userId);
    int countByCreatedAtAfter(LocalDateTime startTime);
    List<Post> findByUserIdInOrderByCreatedAtDesc(List<Long> userIds);
    long count();

    @Query("SELECT p FROM Post p WHERE p.createdAt > :sevenDaysAgo ORDER BY SIZE(p.likes) DESC")
    List<Post> findTop5MostLikedPostsLast7Days(@Param("sevenDaysAgo") LocalDateTime sevenDaysAgo, Pageable pageable);

    @Query("SELECT p FROM Post p ORDER BY SIZE(p.likes) DESC")
    List<Post> findTop10MostLikedPosts(Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Post p WHERE p.id = :id")
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value ="0")})
    Post findPostForUpdate(@Param("id") Integer id);

    List<Post> getAllByUserId(Long id);
    @Query("SELECT p.comments FROM Post p WHERE p.id = :postId")
    List<Comment> findAllCommentsByPostId(@Param("postId") Long postId);
    @Query("SELECT DISTINCT p.userId FROM Post p")
    List<Long> findDistinctUserIds();
    @Query("SELECT COUNT(l) FROM Post p JOIN p.likes l WHERE p.userId IN :followedUserIds AND p.createdAt > :since")
    int countNewLikesForFollowedUsers(@Param("followedUserIds") Set<Long> followedUserIds, @Param("since") LocalDateTime since);

    }
