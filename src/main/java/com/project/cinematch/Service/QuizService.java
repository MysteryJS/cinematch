package com.project.cinematch.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class QuizService {

    private final RestTemplate restTemplate;


    // ✅ Constructor injection for RestTemplate
    public QuizService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> fetchQuiz(int amount, String difficulty) {
        String url = String.format(
            "https://opentdb.com/api.php?amount=%d&category=11&difficulty=%s",
            amount, 
            difficulty
        );
        Map<String, Object> apiResponse = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> results = (List<Map<String, Object>>) apiResponse.get("results");

        List<Map<String, Object>> questions = new ArrayList<>();
        int id = 1;
        for (Map<String, Object> q : results) {
            String question = (String) q.get("question");
            String correct = (String) q.get("correct_answer");
            List<String> incorrect = (List<String>) q.get("incorrect_answers");

            List<String> answers = new ArrayList<>(incorrect);
            answers.add(correct);
            Collections.shuffle(answers);
            int correctIndex = answers.indexOf(correct);

            Map<String, Object> newQuestion = new HashMap<>();
            newQuestion.put("id", id++);
            newQuestion.put("question", question);
            newQuestion.put("answers", answers);
            newQuestion.put("correctIndex", correctIndex);

            questions.add(newQuestion);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("amount", questions.size());
        response.put("questions", questions);

        return response;
    }
}