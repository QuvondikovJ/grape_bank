package com.example.uzum.dto.externalService;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDTO {

    private String name;
    private String region;
    private String country;
    private Double latitude;
    private Double longitude;



}
