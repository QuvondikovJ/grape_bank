package com.example.uzum.dto.soldProducts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllSeller {

    private Integer id;
    private String name;
    private String rating;
    private Integer amountSoldProduct;
    private Integer costOfSoldProduct;

}
