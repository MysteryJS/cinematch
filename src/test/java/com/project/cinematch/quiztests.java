package com.project.cinematch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.cinematch.Controller.QuizHistoryController;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(QuizHistoryController.class)
public class QuizHistoryControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private QuizHistoryRepository quizHistoryRepository;

    @MockBean
    private UserRepository userRepository;

    // ----------------------------
    // 1. Missing Score
    // ----------------------------
    @Test
    @WithMockUser(username = "john")
    void testMissingScore() throws Exception {
        String json = "{}"; // no score

        mockMvc.perform(post("/api/user/quiz-history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest()); // validation should fail
    }

    // ----------------------------
    // 2. Missing Principal (Unauthorized)
    // ----------------------------
    @Test
    void testMissingPrincipal() throws Exception {
        String json = "{\"score\": 5}";

        mockMvc.perform(post("/api/user/quiz-history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }

    // ----------------------------
    // 3. User Not Found
    // ----------------------------
    @Test
    @WithMockUser(username = "ghostUser")
    void testUserNotFound() throws Exception {
        Mockito.when(userRepository.findByUsername("ghostUser"))
                .thenReturn(Optional.empty());

        String json = "{\"score\": 5}";

        mockMvc.perform(post("/api/user/quiz-history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    // ----------------------------
    // 4. Successful Save
    // ----------------------------
    @Test
    @WithMockUser(username = "john")
    void testSuccessfulSave() throws Exception {
        User user = new User();
        user.setUsername("john");

        Mockito.when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(user));

        Mockito.when(quizHistoryRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0)); // return same object

        String json = "{\"score\": 8}";

        mockMvc.perform(post("/api/user/quiz-history")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}

