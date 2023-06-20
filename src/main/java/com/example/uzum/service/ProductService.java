package com.example.uzum.service;

import com.example.uzum.dto.product.ProductDTO;
import com.example.uzum.dto.Result;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {

    Result<?> addProduct(ProductDTO productDto);


    Result<?> getById(Integer id);

    Result<?> getBySimilarProducts(Integer id);

    Result<?> edit(Integer id, ProductDTO productDto);

    Result<?> delete(Integer id);

    Result<?> getByFilter(String search, String categoryId, List<String> prices, List<String> brandIds, String order, String page);

    Result<?> getPricesByFilter(String search, String categoryId, List<String> brandIds);

    List<String> getMethodNamesForConnectToPanels();

    Result<?> getByPanelId(Integer panelId, String categoryId, List<String> prices, List<String> brandIds, String order, String page);

    Result<?> getPricesByPanelId(Integer panelId, String categoryId, List<String> brandIds);

    String directMethodNameToMethod(String methodName);

     Result<?> getConditionByPanelId(Integer panelId);

}
