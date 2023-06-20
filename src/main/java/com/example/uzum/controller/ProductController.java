package com.example.uzum.controller;

import com.example.uzum.dto.product.ProductDTO;
import com.example.uzum.dto.Result;
import com.example.uzum.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    ProductService productService;


    @ApiOperation(value = "This method is used to add new products.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'PRODUCT_ADD')")
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody ProductDTO productDto) {
        return productService.addProduct(productDto);
    }

    @ApiOperation(value = "This method is used to get products by filter.")
    @GetMapping("/getByFilter")
    public Result<?> getByFilter(@RequestParam(required = false) String search,
                                 @RequestParam(required = false) String categoryId,
                                 @RequestParam(required = false) List<String> prices,
                                 @RequestParam(required = false) List<String> brandIds,
                                 @RequestParam(defaultValue = "mostSold") String order,
                                 @RequestParam(defaultValue = "0") String page) {
        return productService.getByFilter(search, categoryId, prices, brandIds, order, page);
    }

    @ApiOperation(value = "This method is used to get prices by filter.")
    @GetMapping("/getPricesByFilter")
    public Result<?> getPricesByFilter(@RequestParam(required = false) String search,
                                       @RequestParam(required = false) String categoryId,
                                       @RequestParam(required = false) List<String> brandIds) {
        return productService.getPricesByFilter(search, categoryId, brandIds);
    }

    @ApiOperation(value = "This method is used to get prices by panel ID.")
    @GetMapping("/getPricesByPanelId/{panelId}")
    public Result<?> getPricesByPanelId(@PathVariable Integer panelId,
                                        @RequestParam(required = false) String categoryId,
                                        @RequestParam(required = false) List<String> brandIds) {
        return productService.getPricesByPanelId(panelId, categoryId, brandIds);
    }

    @ApiOperation(value = "This method is used to products by panel ID.")
    @GetMapping("/getByPanelId/{panelId}")
    public Result<?> getByPanelId(@PathVariable Integer panelId,
                                  @RequestParam(required = false) String categoryId,
                                  @RequestParam(required = false) List<String> prices,
                                  @RequestParam(required = false) List<String> brandIds,
                                  @RequestParam(defaultValue = "mostSold") String order,
                                  @RequestParam(defaultValue = "0") String page) {
        return productService.getByPanelId(panelId, categoryId, prices, brandIds, order, page);
    }

    @ApiOperation(value = "This method is used to get product by its ID.")
    @GetMapping("/getById/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return productService.getById(id);
    }

    @ApiOperation(value = "This method is used to get similar products of something product")
    @GetMapping("/getBySimilarProducts/{id}")
    public Result<?> getBySimilarProducts(@PathVariable Integer id) {
        return productService.getBySimilarProducts(id);
    }

    @ApiOperation(value = "This method is used to edit product details.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'PRODUCT_EDIT')")
    @PutMapping("/edit/{id}")
    public Result<?> edit(@PathVariable Integer id, @Valid @RequestBody ProductDTO productDto) {
        return productService.edit(id, productDto);
    }

    @ApiOperation(value = "This method is used to delete product by its ID.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'PRODUCT_DELETE')")
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        return productService.delete(id);
    }


}
