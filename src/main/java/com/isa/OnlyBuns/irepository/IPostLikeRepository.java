package com.isa.OnlyBuns.irepository;

import com.isa.OnlyBuns.model.Post;
import com.isa.OnlyBuns.model.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface IPostLikeRepository extends JpaRepository<PostLike, Integer> {
    @Query("SELECT p.userId, COUNT(p.userId) AS likeCount " +
            "FROM PostLike p " +
            "WHERE p.likedAt >= :sevenDaysAgo " +
            "GROUP BY p.userId " +
            "ORDER BY likeCount DESC")
    List<Object[]> findTopUsersByLikesInLast7Days(LocalDateTime sevenDaysAgo);

}
