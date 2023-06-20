package com.example.uzum.service;

import com.example.uzum.dto.Result;
import com.example.uzum.dto.seller.SellerDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SellerService {


    Result<?> add(SellerDTO sellerDto);

    Result<?> getByFilter(String search, String order, List<String> time, String page, String isDeleted);

    Result<?> getById(Integer id);

    Result<?> edit(Integer id, SellerDTO sellerDto);

    Result<?> delete(Integer id);

    Result<?> restoreSeller(Integer id);

}
