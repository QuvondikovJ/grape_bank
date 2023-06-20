package com.example.uzum.controller;

import com.example.uzum.dto.Result;
import com.example.uzum.entity.Brand;
import com.example.uzum.service.BrandService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;

    @ApiOperation(value = "This method is used to add new brand.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'BRAND_ADD')")
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody Brand brand) {
        return brandService.add(brand);
    }

    @ApiOperation(value = "This method is used to get all brands.")
    @GetMapping("/all")
    public Result<?> getAll() {
        return brandService.getAll();
    }

    @ApiOperation(value = "This method is used to get brands by filter")
    @GetMapping("/getByFilter")
    public Result<?> getByFilter(@RequestParam(required = false) String search,
                                 @RequestParam(required = false) String categoryId,
                                 @RequestParam(required = false) List<String> prices) {
        return brandService.getByFilter(search, categoryId, prices);
    }

    @ApiOperation(value = "This method is used to brands by panel ID.")
    @GetMapping("/getByPanelId/{panelId}")
    public Result<?> getByPanelId(@PathVariable Integer panelId,
                                  @RequestParam(required = false) String categoryId,
                                  @RequestParam(required = false) List<String> prices) {
        return brandService.getByPanelId(panelId, categoryId, prices);
    }

    @ApiOperation(value = "This method is used to edit brand by its ID.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'BRAND_EDIT')")
    @PutMapping("/editById/{id}")
    public Result<?> editById(@Valid @RequestBody Brand brand, @PathVariable Integer id) {
        return brandService.editById(brand, id);
    }

    @ApiOperation(value = "This method is used to delete brand by its ID.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'BRAND_DELETE')")
    @DeleteMapping("/deleteById/{id}")
    public Result<?> deleteById(@PathVariable Integer id) {
        return brandService.deleteById(id);
    }

}
