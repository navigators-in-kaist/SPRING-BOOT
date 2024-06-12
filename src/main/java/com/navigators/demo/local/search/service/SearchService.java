package com.navigators.demo.local.search.service;

import java.util.Map;

public interface SearchService {

    Map<String, Object> getSearchHistoryList(String userId);
    Map<String, Object> deleteSearchHistory(String userId, String historyId);

}
