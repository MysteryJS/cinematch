package com.project.cinematch.DTO;

import java.time.LocalDateTime;
import java.util.List;

public class ForumPostDTO {

    private Long id;
    private String title;
    private String content;
    private String username;
    private LocalDateTime createdAt;
    private List<String> mediaUrls;

    public ForumPostDTO(Long id, String title, String content, String username, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.username = username;
        this.createdAt = createdAt;
        this.mediaUrls = null;
    }

    public ForumPostDTO(Long id, String title, String content, String username, LocalDateTime createdAt, List<String> mediaUrls) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.username = username;
        this.createdAt = createdAt;
        this.mediaUrls = mediaUrls;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getUsername() { return username; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<String> getMediaUrls() { return mediaUrls; }
}
