package com.project.cinematch.Controller;

import com.project.cinematch.Model.*;
import com.project.cinematch.Repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/forum/media")
public class ForumMediaController {

    private final ForumPostRepository forumPostRepository;
    private final ForumMediaRepository forumMediaRepository;

    public ForumMediaController(
            ForumPostRepository forumPostRepository,
            ForumMediaRepository forumMediaRepository
    ) {
        this.forumPostRepository = forumPostRepository;
        this.forumMediaRepository = forumMediaRepository;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<?> upload(
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            ForumPost post = forumPostRepository.findById(postId).orElse(null);
            if (post == null) {
                return ResponseEntity.status(404).body("Post not found");
            }

            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("Missing file");
            }

            String contentType = file.getContentType();
            if (contentType == null) {
                return ResponseEntity.badRequest().body("Missing content type");
            }

            Path projectRoot = Paths.get(System.getProperty("user.dir"));
            Path uploadsRoot = projectRoot.resolve("uploads");

            MediaType mediaType;
            Integer duration = null;
            Path folder;
            String baseUrl;

            if (contentType.startsWith("image/")) {
                mediaType = MediaType.IMAGE;
                folder = uploadsRoot.resolve("images");
                baseUrl = "/uploads/images/";
            } else if (contentType.startsWith("video/")) {
                mediaType = MediaType.VIDEO;
                folder = uploadsRoot.resolve("videos");
                baseUrl = "/uploads/videos/";
                duration = 0;
            } else {
                return ResponseEntity.badRequest().body("Invalid file type: " + contentType);
            }

            Files.createDirectories(folder);

            String original = file.getOriginalFilename();
            if (original == null) original = "upload";
            original = original.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");

            String filename = System.currentTimeMillis() + "_" + original;
            Path target = folder.resolve(filename);
            file.transferTo(target.toFile());

            ForumMedia media = new ForumMedia();
            media.setPost(post);
            media.setType(mediaType);
            media.setSize(file.getSize());
            media.setDuration(duration);
            media.setUrl(baseUrl + filename);

            forumMediaRepository.save(media);

            return ResponseEntity.ok(media.getUrl());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
