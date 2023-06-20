package com.example.uzum.service;

import com.example.uzum.dto.order.OrderDTO;
import com.example.uzum.dto.Result;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public interface OrderService {


    Result<?> add(OrderDTO dto) throws IOException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException;

    Result<?> getPreparingByBranchId(Integer branchId, List<String> date, String page);

    Result<?> getDeliveringByBranchId(Integer branchId, List<String> date, String page);

    Result<?> getReturnedByBranchId(Integer branchId, List<String> date, String page);

    Result<?> getById(Long id);

    Result<?> getByFirstNameAndLastNameAndPhoneNumber(String name, String phone, String page);

    Result<?> getStatByBranchId(Integer branchId, List<String> date);

    Result<?> getStatByRegionId(Integer regionId, List<String> date);

    Result<?> getByBuyerId(Integer userId, String page);

    Result<?> getAllRegionStat(List<String> date, String order);

    Result<?> getAllBranchStatByRegionId(Integer regionId, List<String> date, String order);
    
    Result<?> changeStatus(Long orderId, String statusName);

    Result<?> getToHomeByBranchId(Integer branchId, String page);

    Result<?> edit(Long id, OrderDTO dto) throws IOException;

    Result<?> searchLocation(String locationName) throws IOException;

    Result<?> getWaitingClientOrdersByBranchId(Integer branchId, String page);


}
