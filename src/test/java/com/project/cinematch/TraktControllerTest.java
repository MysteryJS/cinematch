package com.project.cinematch;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import com.project.cinematch.Controller.TraktController;
import org.springframework.http.MediaType;
import com.project.cinematch.Service.TraktService;

@WebMvcTest(TraktController.class)
@Import(TestSecurityConfig.class)
class TraktControllerTest {
    @Autowired MockMvc mvc;
    @MockBean TraktService service;

    @Test
    void shouldReturnTrendingMovies() throws Exception {
        when(service.getTop10TrendingMovieTitles())
            .thenReturn(List.of("Movie1", "Movie2", "Movie3"));

        mvc.perform(get("/api/trending")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("Movie1"))
            .andExpect(jsonPath("$[1]").value("Movie2"))
            .andExpect(jsonPath("$[2]").value("Movie3"));
    }
}
