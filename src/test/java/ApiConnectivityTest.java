package com.project.cinematch.Service;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiConnectivityTest {

    @Test
    void testApiIsReachable() {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://opentdb.com/api.php?amount=1&category=11&difficulty=easy";

        Map<String, Object> response =
                restTemplate.getForObject(url, Map.class);

        assertNotNull(response, "API response should not be null");
        assertTrue(response.containsKey("results"), "API response should contain 'results'");
    }
}
