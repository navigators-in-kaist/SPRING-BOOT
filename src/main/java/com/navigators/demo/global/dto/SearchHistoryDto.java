package com.navigators.demo.global.dto;

import com.navigators.demo.global.entity.SearchHistory;
import lombok.*;

import java.time.LocalDateTime;

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
}
