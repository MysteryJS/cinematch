package com.project.cinematch;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Εδώ λέμε στην Java ακριβώς που είναι ο κώδικας σου
import com.project.cinematch.Repository.QuizHistoryRepository; 
import com.project.cinematch.Service.LeaderboardService;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class LeaderboardServiceTest {

    @Mock
    private QuizHistoryRepository quizHistoryRepository; 

    @InjectMocks
    private LeaderboardService leaderboardService; 

    @Test
    public void testGetGlobalLeaderboard() {
        // Setup
        Object[] row = {"User1", 500L, 10L}; 
        List<Object[]> fakeData = Arrays.asList(new Object[][]{row});

        when(quizHistoryRepository.findGlobalLeaderboard()).thenReturn(fakeData);

        // Execution
        List<Map<String, Object>> result = leaderboardService.getGlobalLeaderboard();

        // Verification
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("User1", result.get(0).get("username"));
    }
}
