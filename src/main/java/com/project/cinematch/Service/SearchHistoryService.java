package com.project.cinematch.Service;

import com.project.cinematch.Model.SearchHistory;
import com.project.cinematch.Repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SearchHistoryService {
    @Autowired
    private SearchHistoryRepository repo;

    public SearchHistory addHistory(Long userId, String query) {
        SearchHistory entry = new SearchHistory();
        entry.setUserId(userId);
        entry.setQuery(query);
        entry.setSearchedAt(LocalDateTime.now());

        return repo.save(entry);
    }

    public List<SearchHistory> getUserHistory(Long userId) {
        return repo.findByUserId(userId);
    }
}
