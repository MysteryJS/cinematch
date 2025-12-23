package com.project.cinematch.Model;

import jakarta.persistence.*;

@Entity
public class ForumMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @Enumerated(EnumType.STRING)
    private MediaType type;

    private Long size;

    private Integer duration;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private ForumPost post;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public MediaType getType() { return type; }
    public void setType(MediaType type) { this.type = type; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public ForumPost getPost() { return post; }
    public void setPost(ForumPost post) { this.post = post; }
}
