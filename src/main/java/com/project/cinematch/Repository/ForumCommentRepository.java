package com.project.cinematch.Repository;

import com.project.cinematch.Model.ForumComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumCommentRepository extends JpaRepository<ForumComment, Long> {

    List<ForumComment> findByPostId(Long postId);
}
//forum