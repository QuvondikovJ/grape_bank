package com.example.uzum.controller;

import com.example.uzum.dto.Result;
import com.example.uzum.dto.seller.SellerDTO;
import com.example.uzum.service.SellerService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @ApiOperation(value = "This method is used to add new seller.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'SELLER_ADD')")
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody SellerDTO sellerDto) {
        return sellerService.add(sellerDto);
    }

    @ApiOperation(value = "This method is used to get sellers by filter.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'SELLER_GET_BY_FILTER')")
    @GetMapping("/getByFilter")
    public Result<?> getByFilter(@RequestParam(defaultValue = "") String search,
                                 @RequestParam(defaultValue = "alphabet") String order,
                                 @RequestParam(defaultValue = "allTime") List<String> time,
                                 @RequestParam(defaultValue = "0") String page,
                                 @RequestParam(defaultValue = "true") String isActive) {
        return sellerService.getByFilter(search, order, time, page, isActive);
    }

    @ApiOperation(value = "This method is used to get seller by its ID.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_SELLER', 'SELLER_GET_BY_ID')")
    @GetMapping("/getById/{id}")
    public Result<?> getById(@PathVariable Integer id) {
        return sellerService.getById(id);
    }

    @ApiOperation(value = "This method is used to edit information of sellers.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_SELLER', 'SELLER_EDIT')")
    @PutMapping("/edit/{id}")
    public Result<?> edit(@PathVariable Integer id, @Valid @RequestBody SellerDTO sellerDto) {
        return sellerService.edit(id, sellerDto);
    }

    @ApiOperation(value = "This method used to deactivate sellers.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR','ROLE_ADMIN','SELLER_DELETE')")
    @PutMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        return sellerService.delete(id);
    }

    @ApiOperation(value = "This method is used to restore inactive sellers.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR','ROLE_ADMIN','SELLER_RESTORE')")
    @PutMapping("/restore/{id}")
    public Result<?> restoreSeller(@PathVariable Integer id) {
        return sellerService.restoreSeller(id);
    }


}
