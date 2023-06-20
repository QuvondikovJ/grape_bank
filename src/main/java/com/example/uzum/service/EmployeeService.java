package com.example.uzum.service;

import com.example.uzum.dto.Result;
import com.example.uzum.dto.user.BuyerDTO;
import com.example.uzum.dto.user.EmployeeDTO;
import com.example.uzum.dto.user.UserLoginDto;
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
public interface EmployeeService extends UserService {

    Result<?> register(EmployeeDTO dto) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeyException, IOException;

    Result<?> login(UserLoginDto dto);

    Result<?> getEmployees(String role, String page) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;

    Result<?> getCodeToConfirmPhoneNumber(UserLoginDto dto);


    void payStaffSalaries(String jwtToken) throws Exception;

    Result<?> edit(Integer id, EmployeeDTO dto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException;

}
