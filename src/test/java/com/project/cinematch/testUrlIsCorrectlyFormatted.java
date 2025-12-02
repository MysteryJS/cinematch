package com.project.cinematch;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.client.RestTemplate;

import com.project.cinematch.Service.QuizService;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class QuizServiceUrlTest {

    @Test
    void testUrlIsCorrectlyFormatted() {

        // Mock RestTemplate
        RestTemplate mockRestTemplate = mock(RestTemplate.class);

        // Make the API return a minimal valid response
        when(mockRestTemplate.getForObject(anyString(), eq(Map.class)))
                .thenReturn(Map.of("results", List.of()));

        // Inject mock into service
        QuizService quizService = new QuizService(mockRestTemplate);

        // Call method
        quizService.fetchQuiz(10, "medium");

        // Capture the URL sent to RestTemplate
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);

        verify(mockRestTemplate).getForObject(urlCaptor.capture(), eq(Map.class));

        String usedUrl = urlCaptor.getValue();

        // Assert URL correctness
        assertEquals(
                "https://opentdb.com/api.php?amount=10&category=11&difficulty=medium",
                usedUrl
        );
    }
}
