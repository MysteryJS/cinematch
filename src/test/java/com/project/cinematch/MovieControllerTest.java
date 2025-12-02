package com.project.cinematch;

import com.project.cinematch.Controller.MovieController;
import com.project.cinematch.Service.OmdbService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MovieControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OmdbService omdbService;

    @InjectMocks
    private MovieController movieController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(movieController).build();
    }

    @Test
    void testMovieFound() throws Exception {
        String mockJson = """
            {
              "Title": "Inception",
              "Year": "2010",
              "Response": "True"
            }
            """;

        when(omdbService.getMovieByTitle("Inception"))
                .thenReturn(mockJson);

        mockMvc.perform(get("/api/movie/search")
                        .param("title", "Inception"))
                .andExpect(status().isOk());
    }

    @Test
    void testMovieNotFound() throws Exception {
        String notFoundJson = """
            {
              "Response":"False",
              "Error":"Movie not found!"
            }
            """;

        when(omdbService.getMovieByTitle("wrong"))
                .thenReturn(notFoundJson);

        mockMvc.perform(get("/api/movie/search")
                        .param("title", "wrong"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEmptyTitle() throws Exception {
        mockMvc.perform(get("/api/movie/search")
                        .param("title", ""))
                .andExpect(status().isBadRequest());
    }
}

