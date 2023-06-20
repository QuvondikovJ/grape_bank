package com.example.uzum.service;

import com.example.uzum.dto.favourite.FavouriteDto;
import com.example.uzum.dto.Result;
import org.springframework.stereotype.Service;

@Service
public interface FavouriteService {


    Result<?> add(FavouriteDto dto);

    Result<?> getByFilter(String cookie, String buyerId, String order, String page);

    Result<?> getAmountOfFavouriteProduct(Integer productId);

    Result<?> checkOutProduct(FavouriteDto dto);

    Result<?> delete(FavouriteDto dto);
}
