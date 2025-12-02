package com.project.cinematch;

import com.project.cinematch.Service.OmdbService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OmdbService omdbService;

    @Test
    public void testMovieFound() throws Exception {
        String mockJson = """
            {
              "Title": "Inception",
              "Year": "2010",
              "Response": "True"
            }
            """;

        Mockito.when(omdbService.getMovieByTitle("Inception"))
                .thenReturn(mockJson);

        mockMvc.perform(get("/api/movie/search")
                        .param("title", "Inception"))
                .andExpect(status().isOk());
    }

    @Test
    public void testMovieNotFound() throws Exception {
        String notFoundJson = """
            {
              "Response":"False",
              "Error":"Movie not found!"
            }
            """;

        Mockito.when(omdbService.getMovieByTitle("wrong"))
                .thenReturn(notFoundJson);

        mockMvc.perform(get("/api/movie/search")
                        .param("title", "wrong"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testEmptyTitle() throws Exception {
        mockMvc.perform(get("/api/movie/search")
                        .param("title", ""))
                .andExpect(status().isBadRequest());
    }
}
