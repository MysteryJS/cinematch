package com.project.cinematch.Service;

import com.project.cinematch.Model.Movie;
import com.project.cinematch.Model.Actor;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OmdbService {

    private final String apiKey = Dotenv.load().get("OMDB_KEY");
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Convert OMDb JSON into our Movie model
     */
    private Movie mapToMovie(Map<String, Object> json) {

        if (json == null || "False".equals(json.get("Response"))) {
            return null;
        }

        String id = (String) json.get("imdbID");
        String title = (String) json.get("Title");
        String year = (String) json.get("Year");
        String director = (String) json.get("Director");
        String poster = (String) json.get("Poster");
        String awards = (String) json.get("Awards");

        double rating = 0.0;
        try {
            rating = Double.parseDouble((String) json.get("imdbRating"));
        } catch (Exception ignored) {}

        long votes = 0;
        try {
            String votesStr = (String) json.get("imdbVotes");
            if (votesStr != null) {
                votes = Long.parseLong(votesStr.replace(",", ""));
            }
        } catch (Exception ignored) {}

        return new Movie(
                id,
                title,
                year,
                director,
                rating,
                votes,
                poster,
                awards
        );
    }

    /**
     * Fetch movie by IMDB id
     */
    public Movie getMovieById(String movieId) {
        String url = "https://www.omdbapi.com/?apikey=" + apiKey + "&i=" + movieId + "&plot=full";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        return mapToMovie(response);
    }

    /**
     * Fetch movie by title
     */
    public Movie getMovieByTitle(String title) {
        String url = "https://www.omdbapi.com/?apikey=" + apiKey + "&t=" + title + "&plot=full";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        return mapToMovie(response);
    }

    /**
     * OMDb cannot fetch actor info directly.
     * So we construct a basic Actor object manually.
     */
    public Actor getActorById(String actorName) {
        // No direct OMDb actor endpoint → χρησιμοποιούμε το όνομα ως id
        return new Actor(
                actorName,
                actorName,
                null,
                10.0           // default popularity score
        );
    }

    /**
     * Get all movies for a given actor using OMDb search
     */
    public List<Movie> getMoviesByActor(String actorName) {
        String url = "https://www.omdbapi.com/?apikey=" + apiKey + "&s=" + actorName + "&type=movie";

        Map<String, Object> searchResponse = restTemplate.getForObject(url, Map.class);
        List<Movie> movies = new ArrayList<>();

        if (searchResponse == null || !"True".equals(searchResponse.get("Response"))) {
            return movies;
        }

        List<Map<String, String>> searchResults =
                (List<Map<String, String>>) searchResponse.get("Search");

        for (Map<String, String> entry : searchResults) {
            String movieId = entry.get("imdbID");
            Movie movie = getMovieById(movieId);
            if (movie != null) {
                movies.add(movie);
            }
        }

        return movies;
    }
}