package com.example.uzum.controller;

import com.example.uzum.dto.Result;
import com.example.uzum.dto.viewedProducts.ViewedProductsDTO;
import com.example.uzum.service.ViewedProductsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/viewed-products")
public class ViewedProductsController {

    @Autowired
    private ViewedProductsService viewedProductsService;

    @ApiOperation(value = "This method is used to add viewed product and this method works automatically when buyer visits to product page.")
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody ViewedProductsDTO dto){
        return viewedProductsService.add(dto);
    }

    @ApiOperation(value = "This method is used to get viewed products by buyer session ID in order to buyer can see his visited product pages.")
    @GetMapping("/getBySessionId")
    public Result<?> getBySessionId(@RequestParam String sessionId){
        return viewedProductsService.getBySessionId(sessionId);
    }

    @ApiOperation(value = "This method is used to get viewed products by filter.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'VIEWED_PRODUCTS_GET_BY_FILTER')")
    @GetMapping("/getByFilter")
    public Result<?> getByFilter(@RequestParam(defaultValue = "allTime") List<String> time,
                                 @RequestParam(required = false) String sellerId,
                                 @RequestParam(defaultValue = "mostViewed") String order,
                                 @RequestParam(defaultValue = "0") String page){
        return viewedProductsService.getByFilter(time, sellerId, order, page);
    }

}
