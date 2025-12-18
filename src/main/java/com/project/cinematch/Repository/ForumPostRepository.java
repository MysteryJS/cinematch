package com.project.cinematch.Repository;

import com.project.cinematch.Model.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {


    List<ForumPost> findAllByOrderByCreatedAtDesc();
    List<ForumPost> findByCategoryOrderByCreatedAtDesc(String category);
}
//forum