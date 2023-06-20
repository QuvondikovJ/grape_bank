package com.example.uzum.service;

import com.example.uzum.dto.user.FillBalanceDTO;
import com.example.uzum.dto.user.BuyerDTO;
import com.example.uzum.dto.user.UserLoginDto;
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
public interface BuyerService extends UserService {

    Result<?> register(BuyerDTO dto);

    Result<?> loginOrRegister(UserLoginDto dto);

    Result<?> loginByPhoneNumber(String phoneNumber);

    Result<?> loginByPhoneNumberAndPassword(UserLoginDto dto);

    Result<?> getCodeToRegisterOrLogin(String phoneNumber);

    Result<?> getBuyersAmount(List<String> time, String isActive);
    Result<?> edit(Integer id, BuyerDTO dto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException;


    Result<?> fillBalance(FillBalanceDTO dto) throws InvalidAlgorithmParameterException,NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;

    void cleanCashbackPercent(String token) throws Exception;

    Result<?> referralLink(Integer buyerId);
}
