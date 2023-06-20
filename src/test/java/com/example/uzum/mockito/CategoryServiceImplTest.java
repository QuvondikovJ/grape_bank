package com.example.uzum.mockito;

import com.example.uzum.dto.category.CategoryDTO;
import com.example.uzum.serviceImpl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {


    @Spy
    private CategoryServiceImpl categoryServiceImpl;

    @BeforeEach
    private CategoryDTO getCategoryDto() {
        CategoryDTO categoryDTO = CategoryDTO.builder()
                .nameEn("Electronics")
                .nameUz("Elektronika")
                .parentCategoryId(0)
                .build();
        return categoryDTO;
    }

    @Test
    public void addCategoryTest() {
        CategoryDTO categoryDTO = getCategoryDto();
    }


}
