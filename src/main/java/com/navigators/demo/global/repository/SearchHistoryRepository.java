package com.navigators.demo.global.repository;

import com.navigators.demo.global.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, String> {

    Optional<SearchHistory> findById(String historyId);

    List<SearchHistory> findBySearchHistoryUserIdOrderByCreatedAtDesc(String userUuid);

}
