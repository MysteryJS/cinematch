package com.project.cinematch.Controller;

import com.project.cinematch.Model.Movie;
import com.project.cinematch.Service.OmdbService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.project.cinematch.Service.SearchHistoryService;

@RestController
@RequestMapping("/api/movie")
public class MovieController {

    private final OmdbService omdbService;
    private final SearchHistoryService searchHistoryService;

    public MovieController(OmdbService omdbService, SearchHistoryService searchHistoryService) {
        this.omdbService = omdbService;
        this.searchHistoryService = searchHistoryService;
    }

    @GetMapping("/search")
    public ResponseEntity<String> searchMovieByTitle(@RequestParam String title) {

        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.ok("{\"Response\":\"False\",\"Error\":\"Title cannot be empty\"}");
        }

        String response = omdbService.getMovieByTitle(title);

        if (response.contains("\"Response\":\"False\"")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie not found");
        }

        String imdbId = null;
        int idx = response.indexOf("\"imdbID\":\"");
        if (idx != -1) {
            int start = idx + 10;
            int end = response.indexOf("\"", start);
            if (end != -1) {
                imdbId = response.substring(start, end);
            }
        }

        if (imdbId != null) {
            searchHistoryService.addHistory(1L, imdbId);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/id")
    public ResponseEntity<Movie> getMovieById(@RequestParam String imdbId) {
        if (imdbId == null || imdbId.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Movie movie = omdbService.getMovieById(imdbId);

        if (movie == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(movie);
    }
}
