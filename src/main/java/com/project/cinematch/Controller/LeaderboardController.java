package com.project.cinematch.Controller;

import com.project.cinematch.Service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping("/leaderboard")
    public List<Map<String, Object>> getGlobalLeaderboard() {
        return leaderboardService.getGlobalLeaderboard();
    }
}