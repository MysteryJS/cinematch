package com.project.cinematch.Controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;
import com.project.cinematch.Service.TraktService;

@RestController
@RequestMapping("/api/trending")
public class TraktController {

    private final TraktService traktService;

    public TraktController(TraktService traktService) {
        this.traktService = traktService;
    }

    @GetMapping
    public List<String> getTrendingTitles() {
        return traktService.getTop10TrendingMovieTitles();
    }

}