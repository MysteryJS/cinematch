package com.project.cinematch.Controller;

import com.project.cinematch.Model.Movie;
import com.project.cinematch.Service.FavoriteService;
import com.project.cinematch.Service.OmdbService;
import com.project.cinematch.Security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final OmdbService omdbService;

    public FavoriteController(FavoriteService favoriteService, OmdbService omdbService) {
        this.favoriteService = favoriteService;
        this.omdbService = omdbService;
    }

    @PostMapping("/{movieId}")
    public ResponseEntity<Void> addFavorite(@PathVariable String movieId, Authentication auth) {
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        favoriteService.addFavorite(user.getId(), movieId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable String movieId, Authentication auth) {
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        favoriteService.removeFavorite(user.getId(), movieId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<Movie> getFavorites(Authentication auth) {
        UserPrincipal user = (UserPrincipal) auth.getPrincipal();
        List<String> ids = favoriteService.getUserFavorites(user.getId());
        return ids.stream()
                .map(omdbService::getMovieById)
                .toList();
    }
}
