package com.example.uzum.service;

import com.example.uzum.dto.Result;
import org.springframework.stereotype.Service;

@Service
public interface SearchedWordsService {


    Result<?> add(String search, String sessionId);
    Result<?> getAll(String page);

    Result<?> getBySessionId(String sessionId);

    Result<?> deleteBySessionId(String sessionId);

    Result<?> deleteById(Long id);

    Result<?> getPromptWordsForSearching(String search, String sessionId);
}
