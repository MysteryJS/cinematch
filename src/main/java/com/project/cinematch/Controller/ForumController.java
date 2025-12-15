package com.project.cinematch.Controller;

import com.project.cinematch.Model.ForumPost;
import com.project.cinematch.Repository.ForumPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Autowired
    private ForumPostRepository postRepo;

    @GetMapping("/posts")
    public List<ForumPost> getPosts() {
        return postRepo.findAll();
    }

    @PostMapping("/post")
    public ForumPost createPost(@RequestBody ForumPost post) {
        return postRepo.save(post);
    }
}

