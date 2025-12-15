package com.project.cinematch.Controller;

import com.project.cinematch.Model.Favorite;
import com.project.cinematch.Model.User;
import com.project.cinematch.Repository.FavoriteRepository;
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
    private FavoriteRepository favoriteRepository;

    @PostMapping("/favorite")
    public String favoriteMovie(@RequestParam String movieId, @RequestParam String movieTitle) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(auth.getName());
        if (user == null) {
            return "redirect:/login";
        }

        if (favoriteRepository.existsByUserAndMovieId(user, movieId)) {
            return "redirect:/";
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setMovieId(movieId);
        favorite.setMovieTitle(movieTitle);
        favoriteRepository.save(favorite);

        return "redirect:/favorites";
    }

    @GetMapping("/favorites")
    public String showFavorites(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName().equals("anonymousUser")) {
            return "redirect:/login";
        }

        User user = userRepository.findByEmail(auth.getName());
        List<Favorite> favorites = favoriteRepository.findByUser(user);
        model.addAttribute("favorites", favorites);

        return "favorites";
    }
}

