package com.project.cinematch.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.cinematch.Repository.QuizHistoryRepository;

@Service
public class LeaderboardService {
    @Autowired
    private QuizHistoryRepository quizHistoryRepository;

    public List<Map<String, Object>> getGlobalLeaderboard() {
        List<Object[]> data = quizHistoryRepository.findGlobalLeaderboard();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : data) {
            Map<String, Object> m = new HashMap<>();
            m.put("username", row[0]);
            m.put("score", row[1]);
            m.put("gamesPlayed", row[2]);
            result.add(m);
        }
        return result;
    }
}
