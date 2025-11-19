package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
public class QuizController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/quiz")
    public Map<String, Object> getQuiz(@RequestParam(defaultValue = "5") int amount) {

        // 1. Φτιάχνω URL για το Open Trivia DB
        String url = "https://opentdb.com/api.php?amount=10&category=11&difficulty=easy";

        // 2. Κάνω κλήση και παίρνω JSON ως Map
        Map<String, Object> apiResponse = restTemplate.getForObject(url, Map.class);

        // 3. Παίρνω τη λίστα με ερωτήσεις
        List<Map<String, Object>> results =
                (List<Map<String, Object>>) apiResponse.get("results");

        // 4. Μετατρέπω το αποτέλεσμα σε δικό μου format
        List<Map<String, Object>> questions = new ArrayList<>();

        int id = 1;

        for (Map<String, Object> q : results) {
            String question = (String) q.get("question");
            String correct = (String) q.get("correct_answer");
            List<String> incorrect = (List<String>) q.get("incorrect_answers");

            List<String> answers = new ArrayList<>();
            answers.addAll(incorrect);
            answers.add(correct);

            // Shuffle
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
