package com.navigators.demo.global.dao.searchHistory.impl;

import com.navigators.demo.global.dao.searchHistory.SearchHistoryDao;
import com.navigators.demo.global.dto.SearchHistoryDto;
import com.navigators.demo.global.entity.SearchHistory;
import com.navigators.demo.global.repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class SearchHistoryDaoImpl implements SearchHistoryDao {

    private final Integer MAX_HISTORY_NUM = 8;
    private final SearchHistoryRepository searchHistoryRepository;

    @Autowired
    public SearchHistoryDaoImpl(SearchHistoryRepository searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }

    @Override
    public Optional<SearchHistory> getEntityById(String historyId) {
        return searchHistoryRepository.findById(historyId);
    }

    @Override
    public void deleteDto(SearchHistoryDto searchHistoryDto) {
        searchHistoryRepository.delete(searchHistoryDto.toEntity());
    }

    @Override
    public void addOrUpdatedEntity(String userUuid, String type, String entityId) {
        String pseudoId = userUuid + entityId + "__";
        /* existence check */
        if (searchHistoryRepository.findById(pseudoId).isPresent()) {
            /* if exists, update date */
            SearchHistory targetHistory = searchHistoryRepository.findById(pseudoId).get();
            targetHistory.setCreatedAt(LocalDateTime.now());
            searchHistoryRepository.save(targetHistory);
        } else {
            /* if not exists, check length and save */
            List<SearchHistory> historyList = searchHistoryRepository.findBySearchHistoryUserIdOrderByCreatedAtDesc(userUuid);

            if (historyList.size() >= MAX_HISTORY_NUM) {
                /* delete the last one */
                searchHistoryRepository.delete(historyList.get(historyList.size() - 1));
            }

            /* add process */
            SearchHistoryDto newDto = SearchHistoryDto.builder()
                    .historyId(pseudoId)
                    .createdAt(LocalDateTime.now())
                    .searchHistoryUserId(userUuid)
                    .build();
            if (type.equals("building")) {
                newDto.setSearchHistoryBuildingId(entityId);
            } else {
                newDto.setSearchHistoryLocationId(entityId);
            }

            /* save */
            searchHistoryRepository.save(newDto.toEntity());
        }
    }

    @Override
    public List<SearchHistory> getSearchHistoryByUserUuid(String userUuid) {
        return searchHistoryRepository.findBySearchHistoryUserIdOrderByCreatedAtDesc(userUuid);
    }

}
