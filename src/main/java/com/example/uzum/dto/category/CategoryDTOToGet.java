package com.example.uzum.dto.category;

import com.example.uzum.entity.Category;
import com.example.uzum.entity.MainPanel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class CategoryDTOToGet {


    private Category category;
    private List<Category> childCategories;

}
