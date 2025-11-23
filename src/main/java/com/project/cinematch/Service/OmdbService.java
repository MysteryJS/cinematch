package com.project.cinematch.Service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OmdbService {

    private final Dotenv dotenv = Dotenv.load();
    private final String apiKey = dotenv.get("OMDB_KEY");
    private final RestTemplate restTemplate = new RestTemplate();

    public String getMovieByTitle(String title) {
        String url = "http://www.omdbapi.com/?apikey=" + apiKey + "&t=" + title + "&plot=full";
        return restTemplate.getForObject(url, String.class);
    }
}