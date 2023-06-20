package com.example.uzum.dto.externalService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetWeatherDTO {

    private TodayDTO todayDTO;
    private List<ForecastDTO> forecastDTOS;

}
