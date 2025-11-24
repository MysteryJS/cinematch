package com.project.cinematch.Service;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QuizServiceIntegrationTest {

    @Test
    void testFetchQuizReturnsData() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        QuizService quizService = new QuizService(restTemplate);

        Map<String, Object> mockQuestion = Map.of(
                "question", "Test Q?",
                "correct_answer", "Correct",
                "incorrect_answers", List.of("A", "B", "C")
        );

        Map<String, Object> mockResponse = Map.of(
                "results", List.of(mockQuestion)
        );

        when(restTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(mockResponse);

        Map<String, Object> result = quizService.fetchQuiz(1, "easy");

        assertNotNull(result);
        assertTrue(((List<?>) result.get("questions")).size() > 0);

    }
}
