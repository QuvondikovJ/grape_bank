package com.example.uzum.service;

import com.example.uzum.dto.Result;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface BasketService {


    Result<?> add(String sessionId, String basketId, String productId);

    Result<?> getByBuyerId(Integer buyerId);

    Result<?> getAmountOfBuyersByProductId(Integer productId);

    Result<?> getAmountOfBaskets();

    Result<?> edit(String basketId, String productId, String amount, String sessionId);

    Result<?> delete(String basketId, String productId, String sessionId);

    Result<?> getBySessionId(String sessionId);

    Result<?> calculateDeliveryFeeToHome(String ip) throws IOException;
}
