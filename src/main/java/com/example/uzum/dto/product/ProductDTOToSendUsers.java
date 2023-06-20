package com.example.uzum.dto.product;

import com.example.uzum.entity.Category;
import com.example.uzum.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTOToSendUsers {

    private Product product;
    private List<Category> categories; /* grand, father, child */

}
