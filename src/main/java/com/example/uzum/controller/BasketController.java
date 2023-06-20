package com.example.uzum.controller;

import com.example.uzum.dto.Result;
import com.example.uzum.service.BasketService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/api/basket")
public class BasketController {

    @Autowired
    private BasketService basketService;

    @ApiOperation(value = "This method is used to add new basket.")
    @PostMapping("/add")
    public Result<?> add(@RequestParam String sessionId,
                         @RequestParam(required = false) String buyerId,
                         @RequestParam String productId) {
        return basketService.add(sessionId, buyerId, productId);
    }

    @ApiOperation(value = "This method is used to get basket products by buyer session ID.")
    @GetMapping("/getBySessionId")
    public Result<?> getBySessionId(@RequestParam String sessionId) {
        return basketService.getBySessionId(sessionId);
    }

    @ApiOperation(value = "This method is used to get basket products by buyer ID.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_BUYER', 'BASKET_GET_BY_BUYER_ID')")
    @GetMapping("/getByBuyerId/{buyerId}")
    public Result<?> getByBuyerId(@PathVariable Integer buyerId) {
        return basketService.getByBuyerId(buyerId);
    }

    @ApiOperation("This method is used to calculate delivery fee to home.")
@GetMapping("/getDeliveryFeeToHome")
    public Result<?> calculateDeliveryFeeToHome(@RequestParam String ip) throws IOException {
        return basketService.calculateDeliveryFeeToHome(ip);
    }

    @ApiOperation(value = "This method is used to see that how many people's basket have exactly the product.")
    @GetMapping("/getAmountOfBuyersByProductId/{productId}")
    public Result<?> getAmountOfBuyersByProductId(@PathVariable Integer productId) {
        return basketService.getAmountOfBuyersByProductId(productId);
    }

    @ApiOperation(value = "This method is used to get amount all of active baskets.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'BASKET_GET_AMOUNT')")
    @GetMapping("/getAmountOfBaskets")
    public Result<?> getAmountOfBaskets() {
        return basketService.getAmountOfBaskets();
    }

    @ApiOperation(value = "This method is used to edit basket products.")
    @PutMapping("/edit")
    public Result<?> edit(@RequestParam String basketId,
                          @RequestParam String productId,
                          @RequestParam String amount,
                          @RequestParam String sessionId) {
        return basketService.edit(basketId, productId, amount, sessionId);
    }

    @ApiOperation(value = "This method is used to delete product from basket.")
    @DeleteMapping("/delete")
    public Result<?> delete(@RequestParam String basketId,
                            @RequestParam String productId,
                            @RequestParam String sessionId) {
        return basketService.delete(basketId, productId, sessionId);
    }


}
