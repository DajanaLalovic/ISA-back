package com.isa.OnlyBuns.irepository;

import com.isa.OnlyBuns.model.Comment;
import com.isa.OnlyBuns.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ICommentRepository extends JpaRepository<Comment, Integer> {

    public Comment findByPost(Post post);

    Page<Comment> findAllBy(Pageable pageable);

    public Comment findById(int id);
    List<Comment> findByPostIdOrderByCreatedAtDesc(Integer postId);

    int countByUserIdAndCreatedAtAfter(long userId, LocalDateTime startTime);
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.userId IN :followedUserIds AND c.createdAt > :since")
    int countNewCommentsForFollowedUsers(@Param("followedUserIds") Set<Long> followedUserIds, @Param("since") LocalDateTime since);
    @Query("SELECT DISTINCT c.userId FROM Comment c")
    List<Long> findDistinctUserIds();

    @Query(value = "SELECT COUNT(*) FROM comment WHERE user_id = :userId", nativeQuery = true)
    Long countByUserId(@Param("userId") Long userId);
}
