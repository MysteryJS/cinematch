package com.project.cinematch.Controller;

import com.project.cinematch.Model.QuizHistory;
import com.project.cinematch.Model.User;
import com.project.cinematch.Repository.QuizHistoryRepository;
import com.project.cinematch.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class QuizHistoryController {

    @Autowired
    private QuizHistoryRepository quizHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/quiz-history")
    public void saveQuizHistory(@Valid @RequestBody QuizRequest request, Principal principal) {

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        QuizHistory qh = new QuizHistory();
        qh.setUser(user);
        qh.setScore(request.getScore());
        qh.setTakenAt(LocalDateTime.now());

        quizHistoryRepository.save(qh);
    }
}
