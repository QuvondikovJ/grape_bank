package com.example.uzum.controller;

import com.example.uzum.dto.Result;
import com.example.uzum.service.ExternalService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
    @RequestMapping("/api/external-service")
public class ExternalServiceController {

    @Autowired
    private ExternalService externalService;

    @ApiOperation(value = "This method exchanges currencies. Its parameters such as from and to is given as currency abbreviation UZS,RUB,USD.")
    @GetMapping("/currency-exchange")
    public Result<?> getCurrencyExchange(@RequestParam String from,
                                         @RequestParam String amount,
                                         @RequestParam String to,
                                         @RequestParam(required = false) List<String> date) throws IOException {
        return externalService.getCurrencyExchange(from, to, amount, date);
    }

    @GetMapping("/get-all-currencies")
    public Result<?> getAllCurrencies(@RequestParam(required = false) List<String> date) throws IOException {
        return externalService.getAllCurrencies(date);
    }

    @GetMapping("/get-current-location-weather")
    public Result<?> getCurrentLocationWeather(@RequestParam String ip) throws IOException {
        return externalService.getCurrentLocationWeather(ip);
    }

    @GetMapping("/getWeatherAutoComplete")
    public Result<?> getWeatherAutoComplete(@RequestParam String locationName) throws IOException {
        return externalService.getWeatherAutoComplete(locationName);
    }

    @GetMapping("/getWeatherByLocation")
    public Result<?> getWeatherByLocation(@RequestParam String locationName) throws IOException {
        return externalService.getWeatherByLocation(locationName);
    }

}
