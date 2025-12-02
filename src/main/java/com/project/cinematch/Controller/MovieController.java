package com.project.cinematch.Controller;

import com.project.cinematch.Model.Movie;
import com.project.cinematch.Service.OmdbService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movie")
public class MovieController {

    private final OmdbService omdbService;

    public MovieController(OmdbService omdbService) {
        this.omdbService = omdbService;
    }

    // Search movie by title (returns raw JSON from OMDB)
    @GetMapping("/search")
    public ResponseEntity<String> searchMovieByTitle(@RequestParam String title) {
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Title cannot be empty");
        }

        String response = omdbService.getMovieByTitle(title);

        // OMDB returns "Response":"False" inside JSON if movie not found
        if (response.contains("\"Response\":\"False\"")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie not found");
        }

        return ResponseEntity.ok(response);
    }

    // Search movie by IMDb ID (returns Movie object)
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
