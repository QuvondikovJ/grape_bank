package com.example.uzum.dto.soldProducts;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetProductBySeller {

    private Integer id;
    private String nameEn;
    private String nameRu;
    private Integer soldProductAmount;
    private Integer leftProductAmount;
    private Integer price;
    private Integer costOfSoldProduct;


}
