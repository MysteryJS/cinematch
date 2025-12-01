package com.project.cinematch.Service;

import com.project.cinematch.Model.Actor;
import com.project.cinematch.Model.Movie;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class OmdbService {

    private final String apiKey = Dotenv.load().get("OMDB_KEY");
    private final RestTemplate restTemplate = new RestTemplate();



    public String getMovieByTitle(String title) {
        String url = "http://www.omdbapi.com/?apikey=" + apiKey + "&t=" + title + "&plot=full";
        return restTemplate.getForObject(url, String.class);
    }



    // 1) Get movie data by IMDb ID
    public Movie getMovieById(String imdbId) {
        String url = "http://www.omdbapi.com/?apikey=" + apiKey + "&i=" + imdbId + "&plot=full";
        return restTemplate.getForObject(url, Movie.class);
    }

    // 2) Return empty list because OMDb does NOT support actor → movies search
    public List<Movie> getMoviesByActor(String actorName) {
        return Collections.emptyList();
    }

    // 3) Return simple Actor object because OMDb does NOT support actor lookup
    public Actor getActorById(String actorName) {
        Actor actor = new Actor();
        actor.setName(actorName);
        return actor;
    }
}
