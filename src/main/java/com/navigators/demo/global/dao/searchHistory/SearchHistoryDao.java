package com.navigators.demo.global.dao.searchHistory;

import com.navigators.demo.global.dto.SearchHistoryDto;
import com.navigators.demo.global.entity.SearchHistory;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryDao {

    Optional<SearchHistory> getEntityById(String historyId);

    void addOrUpdatedEntity(String userUuid, String type, String entityId);
    void deleteDto(SearchHistoryDto searchHistoryDto);

    List<SearchHistory> getSearchHistoryByUserUuid(String userUuid);
    List<SearchHistory> getSearchHistoryByBuildingId(String buildingId);
    List<SearchHistory> getSearchHistoryByLocationId(String locationId);
}
