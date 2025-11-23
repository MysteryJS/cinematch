package com.project.cinematch.Controller;

import org.springframework.web.bind.annotation.*;
import com.project.cinematch.Service.TraktService;
import com.project.cinematch.Service.Movie;

import java.util.List;

@RestController
@RequestMapping("/api/trending")
public class TraktController {

    private final TraktService traktService;

    public TraktController(TraktService traktService) {
        this.traktService = traktService;
    }

    @GetMapping
    public List<Movie> getTrendingMovies() throws Exception {
        return traktService.getTrendingMovies();
    }

}