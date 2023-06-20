package com.example.uzum.dto.searchWords;

import com.example.uzum.entity.Category;
import com.example.uzum.entity.Product;
import com.example.uzum.entity.SearchedWords;
import com.example.uzum.entity.Seller;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromptWordsDTO {

    private List<SearchedWords> previouslySearchedWords;
    private List<Product> productNames;

    private List<Category> categories;

    private List<Seller> sellers;

}
