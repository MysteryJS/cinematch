package com.project.cinematch.Repository;

import com.project.cinematch.Model.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long>
{
    List<SearchHistory> findByUserId(Long userId);
}
