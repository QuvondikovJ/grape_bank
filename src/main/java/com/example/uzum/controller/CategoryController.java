package com.example.uzum.controller;

import com.example.uzum.dto.ValidatorDTO;
import com.example.uzum.dto.category.CategoryDTO;
import com.example.uzum.dto.Result;
import com.example.uzum.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(value = "/api/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "This method is used to add new category.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'CATEGORY_ADD')")
    @PostMapping(value = "/add")
    public Result<String> addCategory(@Valid @RequestBody CategoryDTO categoryDto) {
        return categoryService.addCategory(categoryDto);

    }

    @ApiOperation(value = "This method is used to get categories by filter.")
    @GetMapping("/getByFilter")
    public Result<?> getByFilter(@RequestParam(required = false) String search,
                                 @RequestParam(required = false) String categoryId,
                                 @RequestParam(required = false) List<String> prices,
                                 @RequestParam(required = false) List<String> brandIds) {
        return categoryService.getByFilter(search, categoryId, prices, brandIds);
    }

    @ApiOperation(value = "This method is used to get categories by panel ID.")
    @GetMapping("/getByPanelId/{panelId}")
    public Result<?> getByPanelId(@PathVariable Integer panelId,
                                  @RequestParam(required = false) String categoryId,
                                  @RequestParam(required = false) List<String> prices,
                                  @RequestParam(required = false) List<String> brandIds) {
        return categoryService.getByPanelId(panelId, categoryId, prices, brandIds);
    }

    @ApiOperation(value = "This method is used to get all of grand categories.")
    @GetMapping(value = "/getGrandCategory")
    public Result<?> getGrandCategory() {
        return categoryService.getGrandCategory();
    }

    @ApiOperation(value = "This method is used to get second and third step categories by their grand category ID.")
    @GetMapping(value = "/getByGrandCategoryId/{id}")
    public Result<?> getByGrandCategoryId(@Valid @PathVariable Integer id) {
        return categoryService.getByGrandCategoryId(id);
    }

    @ApiOperation(value = "This method is used to get category tree.")
    @GetMapping(value = "/getByParentCategoryId/{id}")
    public Result<?> getByParentCategoryId(@Valid @PathVariable Integer id) {
        return categoryService.getByParentCategoryId(id);
    }

    @ApiOperation(value = "This method is used to edit category by its ID.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'CATEGORY_EDIT')")
    @PutMapping(value = "/editById/{id}")
    public Result<?> editById(@Valid @RequestBody CategoryDTO categoryDto, @PathVariable Integer id) {
        return categoryService.editById(categoryDto, id);
    }

    @ApiOperation(value = "This method is used to deactivate category.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'CATEGORY_DELETE')")
    @DeleteMapping(value = "/deleteById/{id}")
    public Result<?> deleteById(@Valid @PathVariable Integer id) {
        return categoryService.deleteById(id);
    }

}
