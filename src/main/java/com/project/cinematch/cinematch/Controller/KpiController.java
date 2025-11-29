package com.project.cinematch.Controller;

import com.project.cinematch.Model.Actor;
import com.project.cinematch.Model.Movie;
import com.project.cinematch.Service.KpiService;
import com.project.cinematch.Service.OmdbService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kpi")
public class KpiController {

    private final KpiService kpiService;
    private final OmdbService omdbService;

    public KpiController(KpiService kpiService, OmdbService omdbService) {
        this.kpiService = kpiService;
        this.omdbService = omdbService;
    }

    // 1) Star Power Index (actor)
    // παράδειγμα: /api/kpi/actor/starpower/Keanu%20Reeves
    @GetMapping("/actor/starpower/{actorName}")
    public double getActorStarPower(@PathVariable String actorName) {
        Actor actor = omdbService.getActorById(actorName);
        List<Movie> movies = omdbService.getMoviesByActor(actorName);
        return kpiService.calculateStarPower(actor, movies);
    }

    // 2) Box Office Popularity Proxy (movie)
    // παράδειγμα: /api/kpi/movie/boxoffice/tt0133093
    @GetMapping("/movie/boxoffice/{movieId}")
    public double getMovieBoxOfficeKpi(@PathVariable String movieId) {
        Movie movie = omdbService.getMovieById(movieId);
        return kpiService.calculateBoxOfficeProxy(movie);
    }

    // 3) Awards Potential (movie)
    // παράδειγμα: /api/kpi/movie/awards/tt0133093
    @GetMapping("/movie/awards/{movieId}")
    public double getMovieAwardsKpi(@PathVariable String movieId) {
        Movie movie = omdbService.getMovieById(movieId);
        return kpiService.calculateAwardsPotential(movie);
    }

    // 4) Audience Engagement (quiz)
    // παράδειγμα:
    // /api/kpi/quiz/audience?starts=100&completions=60&avgScore=7.5&maxScore=10
    @GetMapping("/quiz/audience")
    public double getQuizAudienceEngagement(@RequestParam int starts,
                                            @RequestParam int completions,
                                            @RequestParam double avgScore,
                                            @RequestParam double maxScore) {
        return kpiService.calculateAudienceEngagement(starts, completions, avgScore, maxScore);
    }
}
