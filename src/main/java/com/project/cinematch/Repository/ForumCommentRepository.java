package com.project.cinematch.Repository;

import com.project.cinematch.Model.ForumComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumCommentRepository extends JpaRepository<ForumComment, Long>{
}
