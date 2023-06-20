package com.example.uzum.dto.externalService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TodayDTO {

    private String city;
    private String region;
    private String country;
    private String lastUpdatedWeather;
    private String tempC;
    private String tempF;
    private String condition;
    private String windSpeed;
    private String precipitation;
    private String humidity;
    private String sunRise;
    private String sunSet;
    private String moonRise;
    private String moonSet;

}
