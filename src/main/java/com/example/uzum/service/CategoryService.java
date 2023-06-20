package com.example.uzum.service;

import com.example.uzum.dto.category.CategoryDTO;
import com.example.uzum.dto.Result;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    Result<String> addCategory(CategoryDTO categoryDto);

    Result<?> getGrandCategory();

    Result<?> getByGrandCategoryId(Integer id);

    Result<?> getByParentCategoryId(Integer id);

    Result<?> editById(CategoryDTO categoryDto, Integer id);

    Result<?> deleteById(Integer id);

    Result<?> getByFilter(String search, String categoryId, List<String> prices, List<String> brandIds);

    Result<?> getByPanelId(Integer panelId, String categoryId, List<String> prices, List<String> brandIds);

    List<Integer> getDeepCategoryIDsByHigherCategoryId(List<Integer> newList, Integer id);

}
