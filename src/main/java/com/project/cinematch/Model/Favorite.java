package com.project.cinematch.Model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity
public class Favorite {

    @EmbeddedId
    private FavoriteId id;

    private String movieTitle; // store title for display

    public FavoriteId getId() { return id; }
    public void setId(FavoriteId id) { this.id = id; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
}


