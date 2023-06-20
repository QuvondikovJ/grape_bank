package com.example.uzum.dto.soldProducts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetStatForBank {

    private Integer amountOfSoldProducts;
    private Integer totalSales;
    private Integer byCash;
    private Integer byCard;
    private Integer benefit;


}
