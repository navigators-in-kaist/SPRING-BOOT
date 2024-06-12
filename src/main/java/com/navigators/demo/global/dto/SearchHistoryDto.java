package com.navigators.demo.global.dto;

import com.navigators.demo.global.entity.SearchHistory;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Setter
public class SearchHistoryDto {

    private String historyId;
    private LocalDateTime createdAt;
    private String searchHistoryBuildingId;
    private String searchHistoryLocationId;
    private String searchHistoryUserId;

    public SearchHistory toEntity() {
        return SearchHistory.builder()
                .historyId(historyId)
                .createdAt(createdAt)
                .searchHistoryBuildingId(searchHistoryBuildingId)
                .searchHistoryLocationId(searchHistoryLocationId)
                .searchHistoryUserId(searchHistoryUserId)
                .build();
    }

    public Map<String, Object> toMap(String officialCode, String name) {
        Map<String, Object> infoMap = new HashMap<>();

        infoMap.put("historyId", historyId);
        infoMap.put("createdAt", createdAt);
        infoMap.put("searchHistoryBuildingId", searchHistoryBuildingId);
        infoMap.put("searchHistoryLocationId", searchHistoryLocationId);
        infoMap.put("searchHistoryUserId", searchHistoryUserId);

        /* additional */
        infoMap.put("officialCode", officialCode);
        infoMap.put("name", name); /* building name of location name */

        return infoMap;
    }

}
