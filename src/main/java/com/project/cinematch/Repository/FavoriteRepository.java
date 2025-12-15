package com.project.cinematch.Repository;

import com.project.cinematch.Model.Favorite;
import com.project.cinematch.Model.FavoriteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    List<Favorite> findByIdUserId(Integer userId);
}
