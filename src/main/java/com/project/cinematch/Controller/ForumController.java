package com.project.cinematch.Controller;

import com.project.cinematch.DTO.ForumPostDTO;
import com.project.cinematch.Model.ForumMedia;
import com.project.cinematch.Model.ForumPost;
import com.project.cinematch.Model.User;
import com.project.cinematch.Repository.ForumPostRepository;
import com.project.cinematch.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ForumPostRepository postRepo;

    @GetMapping("/posts")
    public List<ForumPostDTO> getPosts(@RequestParam(required = false) String category) {
        List<ForumPost> posts = (category == null || category.isBlank())
                ? postRepo.findAllWithMedia()
                : postRepo.findByCategoryWithMedia(category);

        return posts.stream()
                .map(post -> {
                    String username = "unknown";

                    if (post.getUserId() != null) {
                        username = userRepository.findById(post.getUserId())
                                .map(User::getUsername)
                                .orElse("unknown");
                    }

                    List<String> mediaUrls = post.getMedia() == null
                            ? null
                            : post.getMedia().stream()
                            .map(ForumMedia::getUrl)
                            .collect(Collectors.toList());

                    return new ForumPostDTO(
                            post.getId(),
                            post.getTitle(),
                            post.getContent(),
                            username,
                            post.getCreatedAt(),
                            mediaUrls
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

        if (post.getCategory() == null || post.getCategory().isBlank()) {
            post.setCategory("General");
        }

        return postRepo.save(post);
    }
}
