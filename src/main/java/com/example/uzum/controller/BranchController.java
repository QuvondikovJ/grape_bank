package com.example.uzum.controller;

import com.example.uzum.dto.branch.BranchDTO;
import com.example.uzum.dto.Result;
import com.example.uzum.service.BranchService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/branch")
public class BranchController {


    @Autowired
    private BranchService branchService;

    @ApiOperation(value = "This method is used to add new branch.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'BRANCH_ADD')")
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody BranchDTO dto) {
        return branchService.add(dto);
    }

    @ApiOperation(value = "This method is used to get branches by region ID.")
    @GetMapping("/getByRegionId/{regionId}")
    public Result<?> getByRegionId(@PathVariable Integer regionId) {
        return branchService.getByRegionId(regionId);
    }

    @ApiOperation(value = "This method is used to get branch by its ID.")
    @GetMapping("/getById/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return branchService.getById(id);
    }

    @ApiOperation(value = "This method is used to edit information of branch by its ID.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'BRANCH_EDIT')")
    @PutMapping("/edit/{id}")
    public Result<?> edit(@PathVariable Integer id, @Valid @RequestBody BranchDTO dto) {
        return branchService.edit(id, dto);
    }

    @ApiOperation(value = "This method is used to deactivate branch by its ID.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'BRANCH_DELETE')")
    @PutMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        return branchService.delete(id);
    }

    @ApiOperation(value = "This method is used to delete branch.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'BRANCH_COMPLETELY_DELETE')")
    @DeleteMapping("/completelyDelete/{id}")
    public Result<?> completelyDelete(@PathVariable Integer id) {
        return branchService.completelyDelete(id);
    }

}
