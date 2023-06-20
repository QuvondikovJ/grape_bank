package com.example.uzum.service;

import com.example.uzum.dto.Result;
import com.example.uzum.dto.user.BuyerDTO;
import com.example.uzum.dto.user.EmployeeDTO;
import com.example.uzum.dto.user.UserLoginDto;
import com.example.uzum.dto.user.UserPhonesDto;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Service
public interface UserService extends UserDetailsService {
    Result<?> confirmPhoneNumber(UserLoginDto dto);

    Result<?> confirmEmail(String token);

    Result<?> getById(Integer id) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException;

    Result<?> getByFirstNameAndLastNameAndPhone(String name, String phone, String page) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;

    Result<?> block(Integer id);

    Result<?> unblock(Integer id);

    Result<?> confirmNewPhoneNumberToEdit(UserLoginDto dto);

    Result<?> getCodeToConfirmNewPhoneNumber(UserPhonesDto dto);


}
