/*package com.project.cinematch.Controller;

import com.project.cinematch.Service.SearchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")

public class SearchHistoryController {
    @Autowired
    private SearchHistoryService service;

    @PostMapping("/add")
    public void addHistory(@RequestParam Long userId, @RequestParam String query) {
        service.addHistory(userId, query);
    }

    @GetMapping("/{userId}")
    public Object getHistory(@PathVariable Long userId) {
        return service.getUserHistory(userId);
    }
}
*/