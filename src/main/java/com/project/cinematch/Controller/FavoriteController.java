package com.project.cinematch.Controller;


import org.springframework.web.bind.annotation.*;
import com.project.cinematch.Model.User;
import com.project.cinematch.Repository.UserRepository;
import com.project.cinematch.Service.FavoriteService;

import org.springframework.beans.factory.annotation.Autowired;
import java.security.Principal;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/watched")
    public ResponseEntity<?> addWatched(@RequestBody String movieName, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        favoriteService.addWatched(user.getId(), movieName.trim());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/watched")
    public ResponseEntity<?> removeWatched(@RequestBody String movieName, Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        favoriteService.removeWatched(user.getId(), movieName.trim());
        return ResponseEntity.ok().build();
    }
}