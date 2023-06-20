package com.example.uzum.controller;

import com.example.uzum.dto.order.OrderDTO;
import com.example.uzum.dto.Result;
import com.example.uzum.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiOperation(value = "This method is used to add new order.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_BUYER', 'ORDER_ADD')")
    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody OrderDTO dto) throws IOException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return orderService.add(dto);
    }

    @ApiOperation(value = "This method is used to search locations on order page.")
    @GetMapping("/searchLocation")
    public Result<?> searchLocation(@RequestParam String locationName) throws IOException {
        return orderService.searchLocation(locationName);
    }

    @ApiOperation(value = "This method is used to get orders that is being prepared by branch ID.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ORDER_GET_BY_PREPARING')")
    @GetMapping("/getPreparingByBranchId/{branchId}")
    public Result<?> getPreparingByBranchId(@PathVariable Integer branchId,
                                            @RequestParam(defaultValue = "atMoment") List<String> date,
                                            @RequestParam(defaultValue = "0") String page) {
        return orderService.getPreparingByBranchId(branchId, date, page);
    }

    @ApiOperation(value = "This method is used to get orders that is being delivered at the moment, and these orders aren't delivered to their addresses yet.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ORDER_GET_BY_DELIVERING')")
    @GetMapping("/getDeliveringByBranchId/{branchId}")
    public Result<?> getDeliveringByBranchId(@PathVariable Integer branchId,
                                             @RequestParam(defaultValue = "atMoment") List<String> date,
                                             @RequestParam(defaultValue = "0") String page) {
        return orderService.getDeliveringByBranchId(branchId, date, page);
    }

    @ApiOperation("This method is used to get returned orders by branch ID.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ORDER_GET_BY_RETURNED')")
    @GetMapping("/getReturnedByBranchId/{branchId}")
    public Result<?> getReturnedByBranchId(@PathVariable Integer branchId,
                                           @RequestParam(defaultValue = "allTime") List<String> date,
                                           @RequestParam(defaultValue = "0") String page) {
        return orderService.getReturnedByBranchId(branchId, date, page);
    }

    @ApiOperation(value = "This method is used to get orders that is being waited their clients on branches.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ORDER_GET_BY_WAITING_CLIENT')")
    @GetMapping("/getWaitingClientOrdersByBranchId/{branchId}")
    public Result<?> getWaitingClientOrdersByBranchId(@PathVariable Integer branchId,
                                                      @RequestParam(defaultValue = "0") String page) {
        return orderService.getWaitingClientOrdersByBranchId(branchId, page);
    }

    @ApiOperation(value = "This method is used to get order by its ID.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_BUYER', 'ORDER_GET_BY_ID')")
    @GetMapping("/getById/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    @ApiOperation(value = "This method is used to get order by buyer's firstname and lastname and phone number.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ORDER_GET_BY_BUYER_DETAILS')")
    @GetMapping("/getByFirstNameAndLastNameAndPhoneNumber")
    public Result<?> getByFirstNameAndLastNameAndPhoneNumber(@RequestParam String name, // here get user's all of orders
                                                             @RequestParam String phone,
                                                             @RequestParam(defaultValue = "0") String page) {
        return orderService.getByFirstNameAndLastNameAndPhoneNumber(name, phone, page);
    }

    @ApiOperation(value = "This method is used to get branch stat by branch ID.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ORDER_GET_STAT_BY_BRANCH_ID')")
    @GetMapping("/getStatByBranchId/{branchId}")
    public Result<?> getStatByBranchId(@PathVariable Integer branchId,
                                       @RequestParam(defaultValue = "allTime") List<String> date) {
        return orderService.getStatByBranchId(branchId, date);
    }

    @ApiOperation(value = "This method is used to get region stat by region ID.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN','ORDER_GET_STAT_BY_REGION_ID')")
    @GetMapping("/getStatByRegionId/{regionId}")
    public Result<?> getStatByRegionId(@PathVariable Integer regionId,
                                       @RequestParam(defaultValue = "allTime") List<String> date) {
        return orderService.getStatByRegionId(regionId, date);
    }

    @ApiOperation(value = "This method is used to get all region stats.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ORDER_GET_ALL_REGION_STAT')")
    @GetMapping("/getAllRegionStat")
    public Result<?> getAllRegionStat(@RequestParam(defaultValue = "allTime") List<String> date,
                                      @RequestParam(defaultValue = "byCreated") String order) {
        return orderService.getAllRegionStat(date, order);
    }

    @ApiOperation(value = "This method is used to get all branch stats by region ID.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ORDER_GET_ALL_BRANCH_STAT_BY_REGION_ID')")
    @GetMapping("/getAllBranchStatByRegionId/{regionId}")
    public Result<?> getAllBranchStatByRegionId(@PathVariable Integer regionId,
                                                @RequestParam(defaultValue = "allTime") List<String> date,
                                                @RequestParam(defaultValue = "byCreated") String order) {
        return orderService.getAllBranchStatByRegionId(regionId, date, order);
    }

    @ApiOperation(value = "This method is used to get buyer orders by his ID.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_BUYER', 'ORDER_GET_BY_BUYER_ID')")
    @GetMapping("/getByBuyerId/{userId}")
    public Result<?> getByBuyerId(@PathVariable Integer userId,
                                  @RequestParam(defaultValue = "0") String page) {
        return orderService.getByBuyerId(userId, page);
    }

    @ApiOperation(value = "This method is used to get orders by branch ID that are to home.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ORDER_GET_TO_HOME_BY_BRANCH_ID')")
    @GetMapping("/getToHomeByBranchId/{branchId}")
    public Result<?> getToHomeByBranchId(@PathVariable Integer branchId,
                                         @RequestParam(defaultValue = "0") String page) {
        return orderService.getToHomeByBranchId(branchId, page);
    }

    @ApiOperation(value = "This method is used to change order status.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ORDER_CHANGE_STATUS')")
    @PutMapping("/changeStatus/{orderId}")
    public Result<?> changeStatus(@PathVariable Long orderId,
                                  @Valid @RequestBody String statusName) {
        return orderService.changeStatus(orderId, statusName);
    }

    @ApiOperation(value = "This method is used to edit order details.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_BUYER', 'ORDER_EDIT')")
    @PutMapping("/edit/{id}")
    public Result<?> edit(@PathVariable Long id, @Valid @RequestBody OrderDTO dto) throws IOException {
        return orderService.edit(id, dto);
    }

}
