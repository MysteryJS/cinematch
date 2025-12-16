package com.project.cinematch.Controller;

import com.project.cinematch.Model.Movie;
import com.project.cinematch.Model.User;
import com.project.cinematch.Service.OmdbService;
import com.project.cinematch.Service.SearchHistoryService;
import com.project.cinematch.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movie")
public class MovieController {

    private final OmdbService omdbService;
    private final SearchHistoryService searchHistoryService;
    private final UserService userService;

    public MovieController(OmdbService omdbService,
            SearchHistoryService searchHistoryService,
            UserService userService) {
        this.omdbService = omdbService;
        this.searchHistoryService = searchHistoryService;
        this.userService = userService;
    }

    @GetMapping("/search")
    public ResponseEntity<String> searchMovieByTitle(
            @RequestParam String title,
            Authentication authentication) {
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("{\"Response\":\"False\",\"Error\":\"Title cannot be empty\"}");
        }

        String response = omdbService.getMovieByTitle(title);

        if (response.contains("\"Response\":\"False\""))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie not found");

        String imdbId = null;
        int idxId = response.indexOf("\"imdbID\":\"");
        if (idxId != -1) {
            int start = idxId + 10;
            int end = response.indexOf("\"", start);
            if (end != -1)
                imdbId = response.substring(start, end);
        }

        String movieTitle = null;
        int idxTitle = response.indexOf("\"Title\":\"");
        if (idxTitle != -1) {
            int start = idxTitle + 9;
            int end = response.indexOf("\"", start);
            if (end != -1)
                movieTitle = response.substring(start, end);
        }

        if (imdbId != null
                && movieTitle != null
                && !movieTitle.isEmpty()
                && authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElse(null);
            if (user != null) {
                searchHistoryService.addHistory(user.getId(), movieTitle);
            }
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/id")
    public ResponseEntity<Movie> getMovieById(@RequestParam String imdbId) {

        if (imdbId == null || imdbId.trim().isEmpty())
            return ResponseEntity.badRequest().build();

        Movie movie = omdbService.getMovieById(imdbId);
        if (movie == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.ok(movie);

    }
}
