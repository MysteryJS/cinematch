package com.project.cinematch.Repository;

import com.project.cinematch.Model.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {

    @Query("""
        select distinct p
        from ForumPost p
        left join fetch p.media
        order by p.createdAt desc
    """)
    List<ForumPost> findAllWithMedia();

    @Query("""
        select distinct p
        from ForumPost p
        left join fetch p.media
        where p.category = :category
        order by p.createdAt desc
    """)
    List<ForumPost> findByCategoryWithMedia(@Param("category") String category);

    @Query("""
        select p
        from ForumPost p
        left join fetch p.media
        where p.id = :id
    """)
    Optional<ForumPost> findByIdWithMedia(@Param("id") Long id);
}
