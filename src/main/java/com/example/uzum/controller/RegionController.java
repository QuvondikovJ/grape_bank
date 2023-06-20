package com.example.uzum.controller;

import com.example.uzum.dto.Result;
import com.example.uzum.entity.Region;
import com.example.uzum.service.RegionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/region")
public class RegionController {


    @Autowired
    private RegionService regionService;

    @ApiOperation(value = "This method is used to add new region.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'REGION_ADD')")
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody Region region) {
        return regionService.add(region);
    }

    @ApiOperation(value = "This method is used to get all regions.")
    @GetMapping("/getAll")
    public Result<?> getAll() {
        return regionService.getAll();
    }

    @ApiOperation(value = "This method is used to get region by its ID.")
    @GetMapping("/getById/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return regionService.getById(id);
    }

    @ApiOperation(value = "This method is used to edit information of region.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'REGION_EDIT')")
    @PutMapping("/edit/{id}")
    public Result<?> edit(@PathVariable Integer id, @Valid @RequestBody Region region) {
        return regionService.edit(id, region);
    }

    @ApiOperation(value = "This method is used to deactivate region.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'REGION_DELETE')")
    @PutMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        return regionService.delete(id);
    }

    @ApiOperation(value = "This method is used to delete region.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'REGION_COMPLETELY_DELETE')")
    @DeleteMapping("/completelyDelete/{id}")
    public Result<?> completelyDelete(@PathVariable Integer id) {
        return regionService.completelyDelete(id);
    }
}
