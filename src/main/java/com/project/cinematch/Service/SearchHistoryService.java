package com.project.cinematch.Service;

import com.project.cinematch.Model.SearchHistory;
import com.project.cinematch.Repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SearchHistoryService {

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    public void addHistory(Long userId, String query) {
        try {
            SearchHistory history = new SearchHistory();
            history.setUserId(userId);
            history.setQuery(query); // ✔ ΠΡΕΠΕΙ να υπάρχει
            history.setSearchedAt(LocalDateTime.now());

            searchHistoryRepository.save(history);

        } catch (Exception e) {
            System.out.println("⚠ Could not save search history: " + e.getMessage());
        }
    }
}

