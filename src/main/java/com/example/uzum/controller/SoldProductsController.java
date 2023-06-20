package com.example.uzum.controller;

import com.example.uzum.dto.Result;
import com.example.uzum.service.SoldProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sold-products")
public class SoldProductsController {


    @Autowired
    private SoldProductsService soldProductsService;


    @GetMapping("/getByBranch")
    public Result<?> getByBranch(@RequestParam(defaultValue = "allTime") List<String> date,
                                 @RequestParam String branchId) {
        return soldProductsService.getByBranch(date, branchId);
    }

    @GetMapping("/getByRegion")
    public Result<?> getByRegion(@RequestParam(defaultValue = "allTime") List<String> date,
                                 @RequestParam String regionId) {
        return soldProductsService.getByRegion(date, regionId);
    }

    @GetMapping("/getAllRegion")
    public Result<?> getAllRegion(@RequestParam(defaultValue = "allTime") List<String> date,
                                  @RequestParam(defaultValue = "alphabet") String order) {
        return soldProductsService.getAllRegion(date, order);
    }

    @GetMapping("/getBranchesByRegion")
    public Result<?> getBranchesByRegion(@RequestParam(defaultValue = "allTime") List<String> date,
                                         @RequestParam(defaultValue = "alphabet") String order,
                                         @RequestParam String regionId) {
        return soldProductsService.getBranchesByRegion(date, order, regionId);
    }

    @GetMapping("/getBySellerId/{sellerId}")
    public Result<?> getBySellerIdForSellers(@PathVariable Integer sellerId,
                                             @RequestParam(defaultValue = "mostSold") String order,
                                             @RequestParam(defaultValue = "0") String page) {
        return soldProductsService.getBySellerIdForSellers(sellerId, order, page);
    }

    @GetMapping("/getAllSeller")
    public Result<?> getAllSeller(@RequestParam(defaultValue = "allTime") List<String> date,
                                  @RequestParam(defaultValue = "alphabet") String order,
                                  @RequestParam(defaultValue = "0") String page) {
        return soldProductsService.getAllSeller(date, order, page);
    }

    @GetMapping("/getStatForBank")
    public Result<?> getStatForBank(@RequestParam(defaultValue = "allTime") List<String> date){
        return soldProductsService.getStatForBank(date);
    }




}
