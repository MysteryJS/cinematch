package com.project.cinematch.Controller;

import com.project.cinematch.Model.Movie;
import com.project.cinematch.Service.OmdbService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/omdb")
public class OmdbController {

    private final OmdbService omdbService;

    public OmdbController(OmdbService omdbService) {
        this.omdbService = omdbService;
    }

    // GET movie by title
    @GetMapping("/title/{title}")
    public Movie getMovieByTitle(@PathVariable String title) {
        return omdbService.getMovieByTitle(title);
    }

    // GET movie by ID
    @GetMapping("/id/{movieId}")
    public Movie getMovieById(@PathVariable String movieId) {
        return omdbService.getMovieById(movieId);
    }
}
