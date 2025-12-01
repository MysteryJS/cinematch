package com.project.cinematch.Controller;

import com.project.cinematch.Model.Actor;
import com.project.cinematch.Model.Movie;
import com.project.cinematch.Repository.QuizHistoryRepository;
import com.project.cinematch.Service.KpiService;
import com.project.cinematch.Service.OmdbService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kpi")
public class KpiController {

    private final KpiService kpiService;
    private final OmdbService omdbService;
    private final QuizHistoryRepository quizHistoryRepository;

    public KpiController(KpiService kpiService,
                         OmdbService omdbService,
                         QuizHistoryRepository quizHistoryRepository) {
        this.kpiService = kpiService;
        this.omdbService = omdbService;
        this.quizHistoryRepository = quizHistoryRepository;
    }

    // 1) Star Power Index (actor)
    @GetMapping("/actor/starpower/{actorName}")
    public double getActorStarPower(@PathVariable String actorName) {
        Actor actor = omdbService.getActorById(actorName);
        List<Movie> movies = omdbService.getMoviesByActor(actorName);
        return kpiService.calculateStarPower(actor, movies);
    }

    // 2) Box Office Popularity Proxy (movie)
    @GetMapping("/movie/boxoffice/{movieId}")
    public double getMovieBoxOfficeKpi(@PathVariable String movieId) {
        Movie movie = omdbService.getMovieById(movieId);
        return kpiService.calculateBoxOfficeProxy(movie);
    }

    // 3) Awards Potential (movie)
    @GetMapping("/movie/awards/{movieId}")
    public double getMovieAwardsKpi(@PathVariable String movieId) {
        Movie movie = omdbService.getMovieById(movieId);
        return kpiService.calculateAwardsPotential(movie);
    }

    // 4) Audience Engagement (quiz)
    @GetMapping("/quiz/audience")
    public double getQuizAudienceEngagement() {

        int starts = (int) quizHistoryRepository.count();
        int completions = starts;

        Double avgScoreObj = quizHistoryRepository.findAverageScore();
        double avgScore = avgScoreObj != null ? avgScoreObj : 0.0;

        double maxScore = 10.0;

        return kpiService.calculateAudienceEngagement(
                starts,
                completions,
                avgScore,
                maxScore
        );
    }
}
