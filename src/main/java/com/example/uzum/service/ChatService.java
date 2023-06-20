package com.example.uzum.service;

import com.example.uzum.dto.Result;
import org.springframework.stereotype.Service;

@Service
public interface ChatService {

    Result<?> getByCookie(String cookie, String page);

    Result<?> getById(Long id, String page);

    Result<?> getUnreadChats(String page, String order);

    Result<?> getAllChats(String page, String order);

    Result<?> edit(Long id, String name, String isBlocked);
}
