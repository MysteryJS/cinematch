package com.project.cinematch.Repository;

import java.util.List;

import com.project.cinematch.Model.QuizHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuizHistoryRepository extends JpaRepository<QuizHistory, Long> {
    @Query("SELECT u.username, MAX(q.score), COUNT(q.id) " +
            "FROM QuizHistory q JOIN q.user u " +
            "GROUP BY u.username " +
            "ORDER BY MAX(q.score) DESC")
    List<Object[]> findGlobalLeaderboard();
    @Query("SELECT AVG(q.score) FROM QuizHistory q")
    Double findAverageScore();
}