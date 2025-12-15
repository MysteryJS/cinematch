package com.project.cinematch.Controller;

import com.project.cinematch.Model.Favorite;
import com.project.cinematch.Model.Movie;
import com.project.cinematch.Model.User;
import com.project.cinematch.Repository.FavoriteRepository;
import com.project.cinematch.Repository.MovieRepository;
import com.project.cinematch.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FavoriteController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @PostMapping("/favorite")
    public String favoriteMovie(@RequestParam Long movieId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return "redirect:/login";
        }

        Movie movie = movieRepository.findById(movieId).orElse(null);
        if (movie == null) {
            return "redirect:/";
        }

        if (favoriteRepository.existsByUserAndMovie(user, movie)) {
            return "redirect:/";
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setMovie(movie);
        favoriteRepository.save(favorite);

        return "redirect:/favorites";
    }

    @GetMapping("/favorites")
    public String showFavorites(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email);

        List<Favorite> favorites = favoriteRepository.findByUser(user);
        model.addAttribute("favorites", favorites);

        return "favorites";
    }
}

