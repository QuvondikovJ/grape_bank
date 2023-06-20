package com.example.uzum.service;

import com.example.uzum.dto.Result;
import com.example.uzum.entity.Brand;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

@Service
public interface BrandService {


     Result<?> add(Brand brand);

     Result<?> getAll();

     Result<?> getByFilter(String search, String categoryId, List<String> prices);

     Result<?> editById(Brand brand, Integer id);

     Result<?> deleteById(Integer id);

    Result<?> getByPanelId(Integer panelId, String categoryId, List<String> prices);
}
