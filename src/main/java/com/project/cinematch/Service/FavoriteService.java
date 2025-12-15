package com.project.cinematch.Service;

import com.project.cinematch.Model.Favorite;
import com.project.cinematch.Model.FavoriteId;
import com.project.cinematch.Model.User;
import com.project.cinematch.Repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    public Favorite addFavorite(User user, String movieId, String movieTitle) {
        FavoriteId favoriteId = new FavoriteId(user.getId().intValue(), movieId);

        if (favoriteRepository.existsById(favoriteId)) {
            return null; // already favorited
        }

        Favorite favorite = new Favorite();
        favorite.setId(favoriteId);
        favorite.setMovieTitle(movieTitle);

        return favoriteRepository.save(favorite);
    }

    public List<Favorite> getFavoritesByUser(User user) {
        return favoriteRepository.findByIdUserId(user.getId().intValue());
    }

    public boolean removeFavorite(User user, String movieId) {
        FavoriteId favoriteId = new FavoriteId(user.getId().intValue(), movieId);

        if (!favoriteRepository.existsById(favoriteId)) {
            return false;
        }

        favoriteRepository.deleteById(favoriteId);
        return true;
    }

    public boolean isFavorite(User user, String movieId) {
        FavoriteId favoriteId = new FavoriteId(user.getId().intValue(), movieId);
        return favoriteRepository.existsById(favoriteId);
    }
}

