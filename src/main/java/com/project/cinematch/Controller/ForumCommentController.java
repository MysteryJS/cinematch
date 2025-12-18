package com.project.cinematch.Controller;

import com.project.cinematch.Model.ForumComment;
import com.project.cinematch.Model.User;
import com.project.cinematch.Repository.ForumCommentRepository;
import com.project.cinematch.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum/comments")
public class ForumCommentController {

    @Autowired
    private ForumCommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{postId}")
    public List<ForumComment> getComments(@PathVariable Long postId) {
        return commentRepository.findByPostId(postId);
    }

    @PostMapping("/{postId}")
    public ForumComment addComment(
            @PathVariable Long postId,
            @RequestBody ForumComment comment,
            Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        comment.setPostId(postId);
        comment.setUserId(user.getId());

        return commentRepository.save(comment);
    }
}
//forum