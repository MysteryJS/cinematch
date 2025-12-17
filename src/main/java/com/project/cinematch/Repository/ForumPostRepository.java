package com.project.cinematch.Repository;

import com.project.cinematch.Model.ForumPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumPostRepository extends JpaRepository<ForumPost, Long> {

    // Επιστρέφει όλα τα posts με σειρά πιο πρόσφατο → πιο παλιό
    List<ForumPost> findAllByOrderByCreatedAtDesc();
}
//forum