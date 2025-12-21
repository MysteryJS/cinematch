package com.project.cinematch.DTO;

import java.time.LocalDateTime;

public class ForumPostDTO {

    private Long id;
    private String title;
    private String content;
    private String username;
    private LocalDateTime createdAt;

    public ForumPostDTO(Long id, String title, String content, String username, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.username = username;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getUsername() { return username; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
//forum