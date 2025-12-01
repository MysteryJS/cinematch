package com.project.cinematch.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.cinematch.Model.User;
import com.project.cinematch.Repository.QuizHistoryRepository;
import com.project.cinematch.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizHistoryController.class)
class QuizHistoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuizHistoryRepository quizHistoryRepository;

    @MockBean
    private UserRepository userRepository;

    // ----------------------------
    // 1. Missing score -> no save
    // ----------------------------
    @Test
    @WithMockUser(username = "john")
    void testMissingScore() throws Exception {
        String json = "{}"; // No score field

        mockMvc.perform(post("/api/user/quiz-history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk()); // Controller returns void (200)

        verify(quizHistoryRepository, never()).save(any());
    }

    // ----------------------------
    // 2. Missing Principal (Unauthorized)
    // ----------------------------
    @Test
    void testMissingPrincipal() throws Exception {
        String json = "{\"score\": 10}";

        mockMvc.perform(post("/api/user/quiz-history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());

        verify(quizHistoryRepository, never()).save(any());
    }

    // ----------------------------
    // 3. User Not Found -> no save
    // ----------------------------
    @Test
    @WithMockUser(username = "ghost")
    void testUserNotFound() throws Exception {
        Mockito.when(userRepository.findByUsername("ghost"))
                .thenReturn(Optional.empty());

        String json = "{\"score\": 10}";

        mockMvc.perform(post("/api/user/quiz-history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk()); // Controller returns void

        verify(quizHistoryRepository, never()).save(any());
    }

    // ----------------------------
    // 4. Successful save
    // ----------------------------
    @Test
    @WithMockUser(username = "john")
    void testSuccessfulSave() throws Exception {
        User user = new User();
        user.setUsername("john");

        Mockito.when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        String json = "{\"score\": 12}";

        mockMvc.perform(post("/api/user/quiz-history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(quizHistoryRepository).save(any());
    }
}


