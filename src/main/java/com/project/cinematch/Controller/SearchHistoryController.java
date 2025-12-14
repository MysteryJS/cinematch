package com.project.cinematch.Controller;

import com.project.cinematch.Model.SearchHistory;
import com.project.cinematch.Model.User;
import com.project.cinematch.Service.SearchHistoryService;
import com.project.cinematch.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class SearchHistoryController {

    @Autowired
    private SearchHistoryService service;

    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<?> addHistory(@RequestParam String movieId, Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        Long userId = user.getId();
        service.addHistory(userId, movieId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/mine")
    public ResponseEntity<List<SearchHistory>> myHistory(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return ResponseEntity.status(401).build();
        }
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        Long userId = user.getId();
        return ResponseEntity.ok(service.getUserHistory(userId));
    }
}