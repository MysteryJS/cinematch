package com.project.cinematch.Controller;

import org.springframework.web.bind.annotation.*;

import com.project.cinematch.Service.OmdbService;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final OmdbService omdbService;

    public MovieController(OmdbService omdbService) {
        this.omdbService = omdbService;
    }

    @GetMapping("/{title}")
    public String getMovie(@PathVariable String title) {
        return omdbService.getMovieByTitle(title);
    }
}