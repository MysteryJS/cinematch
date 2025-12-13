package com.project.cinematch.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;

@Controller
public class PageControllers {

    @GetMapping("/search")
    public String search(Model model, Authentication authentication) {
        boolean isLoggedIn = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);
        model.addAttribute("isLoggedIn", isLoggedIn);
        return "search";
    }

    @GetMapping("/trending")
    public String treanding() {
        return "trending";
    }

    @GetMapping("/forum")
    public String forum() {
        return "forum";
    }

    @GetMapping("/sentiment")
    public String sentiment() {
        return "sentiment";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/quiz")
    public String quiz(Model model, Authentication authentication) {
        boolean isLoggedIn = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);
        model.addAttribute("isLoggedIn", isLoggedIn);
        return "quiz";
    }

    @GetMapping("/face")
    public String face() {
        return "face_detection";
    }

}