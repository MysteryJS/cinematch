package com.project.cinematch.Service;

import com.project.cinematch.Model.SearchHistory;
import com.project.cinematch.Repository.SearchHistoryRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SearchHistoryService {

    private final SearchHistoryRepository searchHistoryRepository;

    public SearchHistoryService(SearchHistoryRepository searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }

    public void addHistory(Long userId, String movieId) {
        SearchHistory history = new SearchHistory();
        history.setUserId(userId);
        history.setMovieId(movieId);
        searchHistoryRepository.save(history);
    }

    public List<SearchHistory> getUserHistory(Long userId) {
        return searchHistoryRepository.findByUserId(userId);
    }
}
