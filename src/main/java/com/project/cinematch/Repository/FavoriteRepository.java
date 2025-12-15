package com.project.cinematch.Repository;

import com.project.cinematch.Model.Favorite;
import com.project.cinematch.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUser(User user);
    boolean existsByUserAndMovieId(User user, String movieId);
}

