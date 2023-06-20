package com.example.uzum.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MaxAndMinPriceDTO {

    private Integer maxPrice;
    private Integer minPrice;

}
