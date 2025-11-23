package com.project.cinematch.Controller;

import org.springframework.web.bind.annotation.*;
import com.project.cinematch.Service.QuizService;

import java.util.Map;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public Map<String, Object> getQuiz(
            @RequestParam(defaultValue = "5") int amount,
            @RequestParam(defaultValue = "easy") String difficulty) {
        return quizService.fetchQuiz(amount, difficulty);
    }
}