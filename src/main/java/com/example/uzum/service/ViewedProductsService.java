package com.example.uzum.service;

import com.example.uzum.dto.Result;
import com.example.uzum.dto.viewedProducts.ViewedProductsDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ViewedProductsService {

    Result<?> add(ViewedProductsDTO dto);

    Result<?> getBySessionId(String sessionId);

    Result<?> getByFilter(List<String> time, String sellerId, String order, String page);
}
