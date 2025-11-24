package com.project.cinematch.Service;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QuizServiceIntegrationTest {

    @Test
    void testFetchQuizReturnsData() {

        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        QuizService quizService = new QuizService(mockRestTemplate);

        Map<String, Object> result = quizService.fetchQuiz(3, "easy");

        assertNotNull(result);
        assertTrue(result.containsKey("questions"));

        List<Map<String, Object>> questions =
                (List<Map<String, Object>>) result.get("questions");

        assertNotNull(questions, "Questions should not be null");
        assertFalse(questions.isEmpty(), "Questions should not be empty");

        Map<String, Object> q = questions.get(0);

        assertTrue(q.containsKey("question"));
        assertTrue(q.containsKey("answers"));
        assertTrue(q.containsKey("correctIndex"));

        List<String> answers = (List<String>) q.get("answers");
        assertFalse(answers.isEmpty(), "Answers should not be empty");

    }
}
