package com.project.cinematch.Controller;

import com.project.cinematch.Model.Favorite;
import com.project.cinematch.Model.FavoriteId;
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
import java.util.Optional;

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

        Optional<User> optionalUser = userRepository.findByUsername(auth.getName());
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }
        User user = optionalUser.get();

        FavoriteId favoriteId = new FavoriteId(user.getId().intValue(), movieId);
        if (favoriteRepository.existsById(favoriteId)) {
            return "redirect:/";
        }

        Favorite favorite = new Favorite();
        favorite.setId(favoriteId);
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

        Optional<User> optionalUser = userRepository.findByUsername(auth.getName());
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }
        User user = optionalUser.get();

        List<Favorite> favorites = favoriteRepository.findByIdUserId(user.getId().intValue());
        model.addAttribute("favorites", favorites);

        return "favorites";
    }
}

