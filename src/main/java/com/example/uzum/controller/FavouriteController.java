package com.example.uzum.controller;

import com.example.uzum.dto.favourite.FavouriteDto;
import com.example.uzum.dto.Result;
import com.example.uzum.service.FavouriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/favourite")
public class FavouriteController {

    @Autowired
    private FavouriteService favouriteService;


    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody FavouriteDto dto) {
        return favouriteService.add(dto);
    }

    @GetMapping("/getByFilter")
    public Result<?> getByFilter(@RequestParam String cookie,
                                 @RequestParam String buyerId,
                                 @RequestParam(defaultValue = "mostSold") String order,
                                 @RequestParam(defaultValue = "0") String page) {
        return favouriteService.getByFilter(cookie, buyerId, order, page);
    }

    @GetMapping("/getAmountOfFavouriteProduct/{productId}")
    public Result<?> getAmountOfFavouriteProduct(@PathVariable Integer productId) {
        return favouriteService.getAmountOfFavouriteProduct(productId);
    }

    @GetMapping("/checkOutProduct")
    public Result<?> checkOutProduct(@Valid @RequestBody FavouriteDto dto) {  // check out that is this product favourite.
        return favouriteService.checkOutProduct(dto);
    }

    @DeleteMapping("/delete")
    public Result<?> delete(@Valid @RequestBody FavouriteDto dto) {
        return favouriteService.delete(dto);
    }


}
