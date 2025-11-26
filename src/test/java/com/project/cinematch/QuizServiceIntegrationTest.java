package com.project.cinematch;

import com.project.cinematch.Service.QuizService;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QuizServiceIntegrationTest {

    @Test
    void testFetchQuizReturnsData() {

        RestTemplate mockRestTemplate = mock(RestTemplate.class);

        // Mock API JSON response
        Map<String, Object> fakeApiResponse = Map.of(
                "results", List.of(
                        Map.of(
                                "question", "What is 2+2?",
                                "correct_answer", "4",
                                "incorrect_answers", List.of("1", "2", "3")
                        )
                )
        );

        // Make mockRestTemplate return the fake response
        when(mockRestTemplate.getForObject(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.eq(Map.class)
        )).thenReturn(fakeApiResponse);

        // Inject mock into service
        QuizService quizService = new QuizService(mockRestTemplate);

        Map<String, Object> result = quizService.fetchQuiz(3, "easy");

        assertNotNull(result);
        assertTrue(result.containsKey("questions"));

        List<Map<String, Object>> questions =
                (List<Map<String, Object>>) result.get("questions");

        assertNotNull(questions);
        assertFalse(questions.isEmpty());

        Map<String, Object> q = questions.get(0);

        assertTrue(q.containsKey("question"));
        assertTrue(q.containsKey("answers"));
        assertTrue(q.containsKey("correctIndex"));

        List<String> answers = (List<String>) q.get("answers");
        assertFalse(answers.isEmpty());
    }
}
