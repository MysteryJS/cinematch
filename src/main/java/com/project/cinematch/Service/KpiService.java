package com.project.cinematch.Service;

import com.project.cinematch.Model.Actor;
import com.project.cinematch.Model.Movie;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KpiService {


    public double calculateStarPower(Actor actor, List<Movie> movies) {

        double avgRating = movies.stream()
                .mapToDouble(Movie::getImdbRating)
                .average()
                .orElse(0.0);

        int movieCount = movies.size();

        double actorPopularity = actor.getPopularity() != null
                ? actor.getPopularity()
                : 10.0; // default

        return (avgRating * 0.6)
                + (movieCount * 0.3)
                + (actorPopularity * 0.1);
    }


    public double calculateBoxOfficeProxy(Movie movie) {

        double rating = movie.getImdbRating();
        long votes = movie.getImdbVotes() != null ? movie.getImdbVotes() : 0;

        double normalizedVotes = Math.log10(votes + 1);
        return (rating * 0.5) + (normalizedVotes * 0.5);
    }


    public double calculateAwardsPotential(Movie movie) {

        double base = movie.getImdbRating();

        String awards = movie.getAwards();
        if (awards == null || awards.equalsIgnoreCase("N/A")) {
            return base;
        }

        int numbersSum = 0;
        String[] tokens = awards.split("[^0-9]+");
        for (String t : tokens) {
            if (!t.isBlank()) {
                try {
                    numbersSum += Integer.parseInt(t);
                } catch (NumberFormatException ignored) {}
            }
        }

        double bonus = numbersSum * 0.1;

        return base + bonus;
    }


    public double calculateAudienceEngagement(int quizStarts,
                                              int quizCompletions,
                                              double avgScore,
                                              double maxScore) {

        if (quizStarts <= 0 || maxScore <= 0) {
            return 0.0;
        }

        double completionRate = (double) quizCompletions / quizStarts;
        if (completionRate > 1) completionRate = 1;

        double normalizedScore = avgScore / maxScore;
        if (normalizedScore > 1) normalizedScore = 1;

        double engagement = (completionRate * 0.6) + (normalizedScore * 0.4);

        return engagement * 100.0; 
    }
}