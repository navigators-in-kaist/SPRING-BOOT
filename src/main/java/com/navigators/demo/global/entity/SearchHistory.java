package com.navigators.demo.global.entity;

import com.navigators.demo.global.dto.SearchHistoryDto;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "search_histories")
public class SearchHistory {

    @Id
    @Column(name = "history_id")
    private String historyId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "fk__search_histories__buildings")
    private String searchHistoryBuildingId;

    @Column(name = "fk__search_histories__locations")
    private String searchHistoryLocationId;

    @Column(name = "fk__search_histories__users")
    private String searchHistoryUserId;

    public SearchHistoryDto toDto() {
        return SearchHistoryDto.builder()
                .historyId(historyId)
                .createdAt(createdAt)
                .searchHistoryBuildingId(searchHistoryBuildingId)
                .searchHistoryLocationId(searchHistoryLocationId)
                .searchHistoryUserId(searchHistoryUserId)
                .build();
    }

}
