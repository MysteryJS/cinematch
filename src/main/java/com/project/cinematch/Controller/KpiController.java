package com.project.cinematch.Controller;

import com.project.cinematch.Model.Actor;
import com.project.cinematch.Model.Movie;
import com.project.cinematch.Repository.QuizHistoryRepository;
import com.project.cinematch.Service.KpiService;
import com.project.cinematch.Service.OmdbService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;
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

    @GetMapping("")
    @ResponseBody
    public Map<String, Object> getKpi() {
        Map<String, Object> kpi = new HashMap<>();
        kpi.put("popularity", 87);
        kpi.put("searchCount", 142);
        kpi.put("ratingInfluence", 73);
        return kpi;
    }

    @GetMapping("/actor/starpower/{actorName}")
    public double getActorStarPower(@PathVariable String actorName) {
        Actor actor = omdbService.getActorById(actorName);
        List<Movie> movies = omdbService.getMoviesByActor(actorName);
        return kpiService.calculateStarPower(actor, movies);
    }

    @GetMapping("/movie/boxoffice/{movieId}")
    public double getMovieBoxOfficeKpi(@PathVariable String movieId) {
        Movie movie = omdbService.getMovieById(movieId);
        return kpiService.calculateBoxOfficeProxy(movie);
    }

    @GetMapping("/movie/awards/{movieId}")
    public double getMovieAwardsKpi(@PathVariable String movieId) {
        Movie movie = omdbService.getMovieById(movieId);
        return kpiService.calculateAwardsPotential(movie);
    }

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
