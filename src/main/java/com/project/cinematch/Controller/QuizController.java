package com.project.cinematch.Controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class QuizController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/quiz")
    public List<String> getFavoritesForCurrentUser(Principal principal) {
        Long userId = jdbcTemplate.queryForObject(
            "SELECT id FROM users WHERE username = ?",
            Long.class,
            principal.getName()
        );

        return jdbcTemplate.queryForList(
            "SELECT movie_name FROM favorites WHERE user_id = ?",
            String.class,
            userId
        );
    }
}