package com.example.uzum.controller;

import com.example.uzum.dto.Result;
import com.example.uzum.dto.user.EmployeeDTO;
import com.example.uzum.dto.user.UserLoginDto;
import com.example.uzum.dto.user.UserPhonesDto;
import com.example.uzum.service.EmployeeService;
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
import java.security.spec.InvalidKeySpecException;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @ApiOperation(value = "This method is used to register new employee and this method can be applied only by Director or Admin.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN','EMPLOYEE_ADD')")
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody EmployeeDTO dto) throws InvalidAlgorithmParameterException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException, IOException {
        return employeeService.register(dto);
    }

    @ApiOperation(value = "This method is used to confirm phone number and it is used when new employee is registering.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN','EMPLOYEE_CONFIRM_PHONE_NUMBER')")
    @GetMapping("/confirm-phone-number")
    public Result<?> confirmPhoneNumber(@Valid @RequestBody UserLoginDto dto) {
        return employeeService.confirmPhoneNumber(dto);
    }

    @ApiOperation(value = "This method is used to get code to confirm phone number and is used when new employee is registering.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN','EMPLOYEE_GET_CODE_TO_CONFIRM_PHONE_NUMBER')")
    @GetMapping("/get-code-to-confirm-phone-number")
    public Result<?> getCodeToConfirmPhoneNumber(@Valid @RequestBody UserLoginDto dto) {
        return employeeService.getCodeToConfirmPhoneNumber(dto);
    }

    @GetMapping("/confirm-email")
    public Result<?> confirmEmail(@RequestParam String token) {
        return employeeService.confirmEmail(token);
    }

    @ApiOperation(value = "This method is used to login to server.")
    @GetMapping("/login")
    public Result<?> login(@Valid @RequestBody UserLoginDto dto) {
        return employeeService.login(dto);
    }

    @ApiOperation(value = "This method is used to get all employees and it can be applied only by Director.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'EMPLOYEES_GET')")
    @GetMapping("/getEmployees")
    public Result<?> getEmployees(@RequestParam(defaultValue = "allEmployee") String role,
                                  @RequestParam(defaultValue = "0") String page) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return employeeService.getEmployees(role, page);
    }

    @ApiOperation(value = "This method is used to get one employee and every employee only can see his information except Admin and Director.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_SELLER','EMPLOYEE_GET_BY_ID')")
    @GetMapping("/getById/{id}")
    public Result<?> getById(@PathVariable Integer id) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        return employeeService.getById(id);
    }

    @ApiOperation(value = "This method is used to get one employee and it can be applied only by Admin and Director.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN','EMPLOYEE_GET_BY_NAME_PHONE')")
    @GetMapping("/getByFirstnameAndLastnameAndPhone")
    public Result<?> getByFirstNameAndLastNameAndPhone(@RequestParam(required = false) String name,
                                                       @RequestParam(required = false) String phone,
                                                       @RequestParam(defaultValue = "0") String page) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return employeeService.getByFirstNameAndLastNameAndPhone(name, phone, page);
    }

    @ApiOperation(value = "This method is used to edit each employee's information and each employee can only edit his information, not that of others.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_SELLER', 'EMPLOYEE_EDIT')")
    @PutMapping("/edit/{id}")
    public Result<?> edit(@PathVariable Integer id, @Valid @RequestBody EmployeeDTO dto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException {
        return employeeService.edit(id, dto);
    }

    @ApiOperation(value = "This method is used to confirm new phone number and it is used when employee is editing his phone number.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN','ROLE_SELLER','EMPLOYEE_CONFIRM_NEW_PHONE_NUMBER_TO_EDIT')")
    @PutMapping("/confirm-new-phone-number-to-edit")
    public Result<?> confirmNewPhoneNumberToEdit(@Valid @RequestBody UserLoginDto dto) {
        return employeeService.confirmNewPhoneNumberToEdit(dto);
    }

    @ApiOperation(value = "This method is used to get code to confirm new phone number and is used when employee is editing his phone number.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_SELLER','EMPLOYEE_GET_CODE_TO_CONFIRM_NEW_PHONE_NUMBER')")
    @GetMapping("/getCodeToConfirmNewPhoneNumber")
    public Result<?> getCodeToConfirmNewPhoneNumber(@Valid @RequestBody UserPhonesDto dto) {
        return employeeService.getCodeToConfirmNewPhoneNumber(dto);
    }

    @PutMapping("/paySalary")
    public void payStaffSalaries(@RequestParam String jwtToken) throws Exception {
        employeeService.payStaffSalaries(jwtToken);
    }

    @ApiOperation(value = "This method is used to block employee, so employee is not deleted, it is blocked.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN','EMPLOYEE_BLOCK')")
    @PutMapping("/block/{id}")
    public Result<?> block(@PathVariable Integer id) {
        return employeeService.block(id);
    }

    @ApiOperation(value = "This method is used to unblock employee.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN','EMPLOYEE_UNBLOCK')")
    @PutMapping("/unblock/{id}")
    public Result<?> unblock(@PathVariable Integer id) {
        return employeeService.unblock(id);
    }


}
