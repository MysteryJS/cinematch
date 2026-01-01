package com.project.cinematch;

import com.project.cinematch.Controller.QuizHistoryController;
import com.project.cinematch.Model.User;
import com.project.cinematch.Repository.QuizHistoryRepository;
import com.project.cinematch.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(QuizHistoryController.class)
class quiztests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private QuizHistoryRepository quizHistoryRepository;

    @Mock
    private UserRepository userRepository;

    private Principal mockPrincipal(String username) {
        return () -> username;
    }

    @Test
    void testMissingScore() throws Exception {
        Principal principal = mockPrincipal("john");

        String json = "{}";

        mockMvc.perform(post("/api/user/quiz-history")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(quizHistoryRepository, never()).save(any());
    }

    @Test
    void testMissingPrincipal() throws Exception {
        String json = "{\"score\": 5}";

        mockMvc.perform(post("/api/user/quiz-history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(quizHistoryRepository, never()).save(any());
    }

    @Test
    void testUserNotFound() throws Exception {
        Principal principal = mockPrincipal("ghost");

        Mockito.when(userRepository.findByUsername("ghost"))
                .thenReturn(Optional.empty());

        String json = "{\"score\": 10}";

        mockMvc.perform(post("/api/user/quiz-history")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(quizHistoryRepository, never()).save(any());
    }

    @Test
    void testSuccessfulSave() throws Exception {
        Principal principal = mockPrincipal("john");

        User user = new User();
        user.setUsername("john");

        Mockito.when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        String json = "{\"score\": 12}";

        mockMvc.perform(post("/api/user/quiz-history")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(quizHistoryRepository).save(any());
    }
    
}



