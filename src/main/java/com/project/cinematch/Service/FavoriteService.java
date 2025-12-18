package com.project.cinematch.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class FavoriteService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void addWatched(Long userId, String movieName) {
        String sql = "INSERT INTO favorites(user_id, movie_name) VALUES (?, ?) ON CONFLICT DO NOTHING";
        jdbcTemplate.update(sql, userId, movieName);
    }

    public void removeWatched(Long userId, String movieName) {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND movie_name = ?";
        jdbcTemplate.update(sql, userId, movieName);
    }
}