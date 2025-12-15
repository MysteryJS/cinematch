package com.project.cinematch.Service;

import com.project.cinematch.Model.Favorite;
import com.project.cinematch.Model.FavoriteId;
import com.project.cinematch.Repository.FavoriteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository repo;

    public FavoriteService(FavoriteRepository repo) {
        this.repo = repo;
    }

    public void addFavorite(Integer userId, String movieId) {
        FavoriteId id = new FavoriteId(userId, movieId);
        if (!repo.existsById(id)) {
            repo.save(new Favorite(id));
        }
    }

    public void removeFavorite(Integer userId, String movieId) {
        repo.deleteById(new FavoriteId(userId, movieId));
    }

    public List<String> getUserFavorites(Integer userId) {
        return repo.findByIdUserId(userId)
                .stream()
                .map(f -> f.getId().getMovieId())
                .toList();
    }
}
