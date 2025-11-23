package com.project.cinematch.Controller;

import org.springframework.web.bind.annotation.*;
import com.project.cinematch.Service.TraktService;
import com.project.cinematch.Service.Movie;

import java.util.List;

@RestController
@RequestMapping("/api/trending")
public class TraktController {

    private final TraktService traktService = new TraktService();

    @GetMapping
    public List<Movie> getTrendingMovies() throws Exception {
        return traktService.getTrendingMovies();
    }

}