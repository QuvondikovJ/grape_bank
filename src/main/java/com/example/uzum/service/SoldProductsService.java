package com.example.uzum.service;

import com.example.uzum.dto.Result;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SoldProductsService {
    Result<?> getByBranch(List<String> date, String branchId);

    Result<?> getByRegion(List<String> date, String regionId);

    Result<?> getAllRegion(List<String> date, String order);

    Result<?> getBranchesByRegion(List<String> date, String order, String regionId);

    Result<?> getBySellerIdForSellers(Integer sellerId, String order, String page);

    Result<?> getAllSeller(List<String> date, String order, String page);

    Result<?> getStatForBank(List<String> date);
}
