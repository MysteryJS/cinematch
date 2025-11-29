package com.project.cinematch.Service;

import com.project.cinematch.Model.Actor;
import com.project.cinematch.Model.Movie;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KpiService {

    /**
     * KPI #1 – Star Power Index (actor)
     * Βασίζεται σε:
     *  - μέσο imdb rating των ταινιών του
     *  - πόσες ταινίες έχει
     *  - popularity του ηθοποιού
     */
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

    /**
     * KPI #2 – Box Office Popularity Proxy (movie)
     * Βασίζεται σε:
     *  - imdb rating
     *  - imdb votes (log scale για να μη ξεφεύγουν τα νούμερα)
     */
    public double calculateBoxOfficeProxy(Movie movie) {

        double rating = movie.getImdbRating();
        long votes = movie.getImdbVotes() != null ? movie.getImdbVotes() : 0;

        double normalizedVotes = Math.log10(votes + 1); // 0,1,... ~ 6

        return (rating * 0.5) + (normalizedVotes * 0.5);
    }

    /**
     * KPI #3 – Awards Potential (movie)
     * Βασίζεται σε:
     *  - imdb rating
     *  - text από το πεδίο Awards, από όπου μαζεύουμε όλους τους αριθμούς (wins/nominations)
     */
    public double calculateAwardsPotential(Movie movie) {

        double base = movie.getImdbRating();

        String awards = movie.getAwards();
        if (awards == null || awards.equalsIgnoreCase("N/A")) {
            return base; // χωρίς info, μένουμε στο rating
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

        double bonus = numbersSum * 0.1; // απλό bonus ανά αριθμό στα awards

        return base + bonus;
    }

    /**
     * KPI #4 – Audience Engagement Score (quiz)
     * Παίρνει:
     *  - quizStarts: πόσοι άρχισαν το quiz
     *  - quizCompletions: πόσοι το τελείωσαν
     *  - avgScore: μέσο σκορ
     *  - maxScore: μέγιστο δυνατό σκορ
     *
     * Επιστρέφει score 0–100.
     */
    public double calculateAudienceEngagement(int quizStarts,
                                              int quizCompletions,
                                              double avgScore,
                                              double maxScore) {

        if (quizStarts <= 0 || maxScore <= 0) {
            return 0.0;
        }

        double completionRate = (double) quizCompletions / quizStarts; // 0–1
        if (completionRate > 1) completionRate = 1;

        double normalizedScore = avgScore / maxScore; // 0–1
        if (normalizedScore > 1) normalizedScore = 1;

        double engagement = (completionRate * 0.6) + (normalizedScore * 0.4);

        return engagement * 100.0; // 0–100
    }
}
