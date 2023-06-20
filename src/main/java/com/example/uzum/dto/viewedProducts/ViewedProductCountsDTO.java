package com.example.uzum.dto.viewedProducts;

import com.example.uzum.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViewedProductCountsDTO {

    private Product product;
    private Long amountOfView;

}
