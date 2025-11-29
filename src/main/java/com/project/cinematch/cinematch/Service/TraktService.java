package com.project.cinematch.Service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import io.github.cdimascio.dotenv.Dotenv;

@Service
public class TraktService {

    private final String clientId = Dotenv.load().get("TRAKT_CLIENT_ID");
    private final RestTemplate restTemplate = new RestTemplate();

    // Header
    public List<String> getTop10TrendingMovieTitles() {
        var headers = new org.springframework.http.HttpHeaders();
        headers.add("trakt-api-version", "2");
        headers.add("trakt-api-key", clientId);
        var entity = new org.springframework.http.HttpEntity<>(headers);

        String json = restTemplate.exchange(
                "https://api.trakt.tv/movies/trending?limit=10",
                org.springframework.http.HttpMethod.GET,
                entity,
                String.class).getBody();

        return extractTitles(json);
    }

    // Title parsing
    private List<String> extractTitles(String json) {
        List<String> titles = new ArrayList<>();
        int idx = 0, count = 0;
        while ((idx = json.indexOf("\"title\"", idx)) != -1 && count < 10) {
            int start = json.indexOf("\"", idx + 8) + 1;
            int end = json.indexOf("\"", start);
            titles.add(json.substring(start, end));
            idx = end + 1;
            count++;
        }
        return titles;
    }

}