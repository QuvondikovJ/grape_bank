package com.example.uzum.mockito;

import com.example.uzum.dto.user.EmployeeDTO;
import com.example.uzum.entity.enums.Gender;
import com.example.uzum.entity.enums.Role;
import com.example.uzum.repository.EmployeeRepo;
import com.example.uzum.security.EncryptAndDecrypt;
import com.example.uzum.service.TwilioSmsSender;
import com.example.uzum.serviceImpl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeServiceImpl employeeServiceImpl;
    private EmployeeRepo employeeRepo;
    PasswordEncoder passwordEncoder;
    EncryptAndDecrypt encryptAndDecrypt;
    TwilioSmsSender twilioSmsSender;
    String mySecretKey = "This_is_my_secret_key_for_card_details_so_it_is_so_secret_1136";

    @BeforeEach
    public EmployeeDTO createEmployeeObject() throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, InvalidKeyException {
        EmployeeDTO userDTO = new EmployeeDTO();
        userDTO.setFirstname("Bobur");
        userDTO.setLastname("Boburov");
        userDTO.setMiddleName("Boburovich");
        userDTO.setRoleName(Role.ADMIN.name());
        userDTO.setGender(Gender.MALE.name());
        userDTO.setBirthDate(LocalDate.now().minusYears(20).toString());
        userDTO.setPhoneNumber("+998900162969");
        userDTO.setEmail("aaa@gmail.com");
        userDTO.setPassword("Aa111222");
        userDTO.setCardNumber("1111222233334444");
        userDTO.setCardExpiredDate("0125");
        userDTO.setSalary(25000);
        return userDTO;
    }

    @Test
    public void registerTest() throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, IOException {
//        EmployeeDTO userDTO = createEmployeeObject();
//        Employee employee = new Employee();
//        when(employeeRepo.existsByPhoneNumber(userDTO.getPhoneNumber())).thenReturn(false);
//        doNothing().when(employeeRepo.save(employee));
//        employeeServiceImpl.register(userDTO);
//        doNothing().when(twilioSmsSender./sendSms(employee.getPhoneNumber(), anyString()));


    }

}
