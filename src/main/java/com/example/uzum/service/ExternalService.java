package com.example.uzum.service;

import com.example.uzum.dto.Result;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface ExternalService {


    Result<?> getCurrencyExchange(String from, String to, String amount, List<String> date) throws IOException;

    Result<?> getAllCurrencies(List<String> date) throws IOException;

    Result<?> getCurrentLocationWeather(String ip) throws IOException;

    Result<?> getWeatherAutoComplete(String locationName) throws IOException;

    Result<?> getWeatherByLocation(String locationName) throws IOException;
}
