package com.project.cinematch.Controller;

import com.project.cinematch.DTO.ForumPostDTO;
import java.util.stream.Collectors;
import com.project.cinematch.Repository.UserRepository;
import com.project.cinematch.Model.ForumPost;
import com.project.cinematch.Repository.ForumPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.project.cinematch.Model.User;


import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ForumPostRepository postRepo;

    @GetMapping("/posts")
    public List<ForumPostDTO> getPosts() {
        return postRepo.findAll()
                .stream()
                .map(post -> {
                    String username = "unknown";

                    if (post.getUserId() != null) {
                        username = userRepository.findById(post.getUserId())
                                .map(User::getUsername)
                                .orElse("unknown");
                    }

                    return new ForumPostDTO(
                            post.getId(),
                            post.getTitle(),
                            post.getContent(),
                            username,
                            post.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());
    }

    @PostMapping("/post")
    public ForumPost createPost(@RequestBody ForumPost post,
                                Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));


        post.setUserId(user.getId());

        return postRepo.save(post);
    }

}

