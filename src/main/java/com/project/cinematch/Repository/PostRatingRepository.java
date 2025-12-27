package com.project.cinematch.Repository;

import com.project.cinematch.Model.PostRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRatingRepository extends JpaRepository<PostRating, Long> {

    Optional<PostRating> findByPostIdAndUserId(Long postId, Long userId);

    interface Agg {
        Double getAvgRating();
        Long getCnt();
    }

    @Query("""
        select avg(pr.rating) as avgRating, count(pr.id) as cnt
        from PostRating pr
        where pr.postId = :postId
    """)
    Agg aggForPost(@Param("postId") Long postId);

    @Query("""
        select pr.rating
        from PostRating pr
        where pr.postId = :postId and pr.userId = :userId
    """)
    Integer myRating(@Param("postId") Long postId, @Param("userId") Long userId);
}
