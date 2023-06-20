package com.example.uzum.dto.externalService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ForecastDTO {

    private String date;
    private Double maxTempC;
    private Double maxTempF;
    private Double minTempC;
    private Double minTempF;
    private Double avgTempC;
    private Double avgTempF;
    private Double maxWindSpeed;
    private Double totalPrecipitation;
    private Double totalSnow;
    private Double avgHumidity;
    private String condition;
    private String sunRise;
    private String sunSet;
    private String moonRise;
    private String moonSet;





}
