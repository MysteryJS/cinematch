package com.project.cinematch.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class TraktService {

    private final String clientId;
    private final HttpClient http;

    public TraktService() {
        Dotenv dotenv = Dotenv.load();
        this.clientId = dotenv.get("TRAKT_CLIENT_ID");
        this.http = HttpClient.newHttpClient();
    }

    // ========== PUBLIC API ==========

    public List<Movie> getTrendingMovies() throws Exception {
        // /movies/trending – public endpoint (δε χρειάζεται απαραίτητα access token)
        String json = get("/movies/trending?limit=10");

        // Πολύ απλό, χειροποίητο parsing (χωρίς JSON library)
        List<Movie> movies = new ArrayList<>();

        int idx = 0;
        while (true) {
            int movieKey = json.indexOf("\"movie\"", idx);
            if (movieKey == -1)
                break; // δεν έχει άλλο

            int titleIdx = json.indexOf("\"title\"", movieKey);
            if (titleIdx == -1)
                break;

            String title = extractJsonString(json, "title", titleIdx);
            int year = extractJsonInt(json, "year", titleIdx);
            int traktId = extractJsonInt(json, "trakt", titleIdx);

            if (title != null && year != -1 && traktId != -1) {
                movies.add(new Movie(title, year, traktId));
            }

            idx = titleIdx + 1;
        }

        return movies;
    }

    // ========== LOW-LEVEL HTTP ==========

    private String get(String path) throws Exception {
        String url = "https://api.trakt.tv" + path;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("trakt-api-version", "2")
                .header("trakt-api-key", clientId)
                .GET()
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() / 100 != 2) {
            throw new RuntimeException("HTTP error " + response.statusCode() + ": " + response.body());
        }

        return response.body();
    }

    // ========== ΧΕΙΡΟΠΟΙΗΤΟ JSON PARSING (για απλότητα) ==========

    private String extractJsonString(String json, String fieldName, int startFrom) {

        int nameIdx = json.indexOf("\"" + fieldName + "\"", startFrom);
        if (nameIdx == -1)
            return null;

        int colon = json.indexOf(":", nameIdx);
        if (colon == -1)
            return null;

        int quote1 = json.indexOf("\"", colon + 1);
        if (quote1 == -1)
            return null;

        int quote2 = json.indexOf("\"", quote1 + 1);
        if (quote2 == -1)
            return null;

        return json.substring(quote1 + 1, quote2);
    }

    private int extractJsonInt(String json, String fieldName, int startFrom) {
        // ψάχνει από startFrom και μετά "fieldName": 1234
        int nameIdx = json.indexOf("\"" + fieldName + "\"", startFrom);
        if (nameIdx == -1)
            return -1;

        int colon = json.indexOf(":", nameIdx);
        if (colon == -1)
            return -1;

        int i = colon + 1;

        while (i < json.length() && Character.isWhitespace(json.charAt(i))) {
            i++;
        }

        int start = i;
        while (i < json.length() && Character.isDigit(json.charAt(i))) {
            i++;
        }
        if (start == i)
            return -1;

        return Integer.parseInt(json.substring(start, i));
    }

}
