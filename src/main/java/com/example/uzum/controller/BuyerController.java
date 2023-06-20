package com.example.uzum.controller;

import com.example.uzum.dto.user.FillBalanceDTO;
import com.example.uzum.dto.TokenDTO;
import com.example.uzum.dto.user.BuyerDTO;
import com.example.uzum.dto.user.UserLoginDto;
import com.example.uzum.dto.Result;
import com.example.uzum.dto.user.UserPhonesDto;
import com.example.uzum.service.BuyerService;
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
import java.util.List;

@RestController
@RequestMapping("/api/buyer")
public class BuyerController {


    @Autowired
    private BuyerService buyerService;

    @ApiOperation(value = "This method is used to register buyer.")
    @PostMapping("/register")
    public Result<?> register(@Valid @RequestBody BuyerDTO dto) {
        return buyerService.register(dto);
    }

    @ApiOperation(value = "This method is used to confirm phone number.")
    @GetMapping("/confirm-phone")
    public Result<?> confirmPhoneNumber(@RequestBody UserLoginDto dto) {
        return buyerService.confirmPhoneNumber(dto);
    }

    @ApiOperation(value = "This method is used to login or register, so if used hasn't register and will apply this method, he will be redirected to register, otherwise will be redirected to login.")
    @GetMapping("/login-or-register")
    public Result<?> loginOrRegister(@Valid @RequestBody UserLoginDto dto) {
        return buyerService.loginOrRegister(dto);
    }

    @ApiOperation(value = "This method is used to login by phone number.")
    @GetMapping("/login-by-phone")
    public Result<?> loginByPhoneNumber(@Valid @RequestBody UserLoginDto dto) {
        return buyerService.loginByPhoneNumber(dto.getPhoneNumber());
    }

    @ApiOperation(value = "This method is used to login by password.")
    @GetMapping("/login-by-password")
    public Result<?> loginByPhoneNumberAndPassword(@RequestBody UserLoginDto dto) {
        return buyerService.loginByPhoneNumberAndPassword(dto);
    }

    @ApiOperation(value = "This method is used to confirm email.")
    @GetMapping("/confirm-email")
    public Result<?> confirmEmail(@RequestParam String token) {
        return buyerService.confirmEmail(token);
    }

    @ApiOperation(value = "This method is used to get code when code is not sent.")
    @GetMapping("/get-code-again")
    public Result<?> getCodeToRegisterOrLogin(@Valid @RequestBody UserLoginDto dto) {
        return buyerService.getCodeToRegisterOrLogin(dto.getPhoneNumber());
    }

    @ApiOperation(value = "This method is used to see buyer information.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_BUYER', 'BUYER_GET_BY_ID')")
    @GetMapping("/getById/{id}")
    public Result<?> getById(@PathVariable Integer id) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        return buyerService.getById(id);
    }

    @ApiOperation(value = "This method is used to get buyer by his name and phone number.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'BUYER_GET_BY_NAME_AND_PHONE')")
    @GetMapping("/getByFirstnameAndLastnameAndPhone")
    public Result<?> getByFirstnameAndLastnameAndPhone(@RequestParam(required = false) String name,
                                                       @RequestParam(required = false) String phoneNumber,
                                                       @RequestParam(defaultValue = "0") String page) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return buyerService.getByFirstNameAndLastNameAndPhone(name, phoneNumber, page);
    }

    @ApiOperation(value = "This method is used to get amount of all active buyers. AllTime parameter is used to get buyers that registered at GrapeBank at that time.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'BUYER_GET_AMOUNT')")
    @GetMapping("/getBuyersAmount")
    public Result<?> getBuyersAmount(@RequestParam(defaultValue = "allTime") List<String> time,
                                     @RequestParam(defaultValue = "true") String isActive) { // if isActive is true, we will send active buyers count, otherwise is false we'll send block buyers count
        return buyerService.getBuyersAmount(time, isActive);
    }

    @ApiOperation(value = "This method is used to edit buyer's information.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN','ROLE_BUYER','BUYER_EDIT')")
    @PutMapping("/edit/{id}")
    public Result<?> edit(@PathVariable Integer id, @Valid @RequestBody BuyerDTO dto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, IOException {
        return buyerService.edit(id, dto);
    }

    @ApiOperation(value = "This method is used to block employee.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'BUYER_BLOCK')")
    @PutMapping("/block/{id}")
    public Result<?> block(@PathVariable Integer id) {
        return buyerService.block(id);
    }

    @ApiOperation(value = "This method is used to unblock buyer, and it is used when buyer begs to unblock from admin.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'BUYER_UNBLOCK')")
    @PutMapping("/unblock/{id}")
    public Result<?> unblock(@PathVariable Integer id) {
        return buyerService.unblock(id);
    }

    @ApiOperation(value = "This method is used to confirm new phone number and it is used when buyer is changing his phone number.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_BUYER', 'BUYER_CONFIRM_NEW_PHONE')")
    @PutMapping("/confirm-new-phone-number-to-edit")
    public Result<?> confirmNewPhoneNumberToEdit(@Valid @RequestBody UserLoginDto dto) {
        return buyerService.confirmNewPhoneNumberToEdit(dto);
    }

    @ApiOperation(value = "This method is used to get code to confirm new phone number.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR', 'ROLE_ADMIN', 'ROLE_BUYER', 'BUYER_GET_CODE_TO_CONFIRM_NEW_PHONE')")
    @GetMapping("/getCodeToConfirmNewPhoneNumber")
    public Result<?> getCodeToConfirmNewPhoneNumber(@Valid @RequestBody UserPhonesDto dto) {
        return buyerService.getCodeToConfirmNewPhoneNumber(dto);
    }

    @ApiOperation(value = "This method is used to fill buyer balance.")
    @PreAuthorize(value = "hasAnyAuthority('ROLE_DIRECTOR','ROLE_ADMIN','ROLE_BUYER','BUYER_FILL_BALANCE')")
    @PutMapping("/fillBalance")
    public Result<?> fillBalance(@Valid @RequestBody FillBalanceDTO dto) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return buyerService.fillBalance(dto);
    }

    @PutMapping("/cleanCashbackPercent")
    public void cleanCashbackPercent(@Valid @RequestBody TokenDTO token) throws Exception {
        buyerService.cleanCashbackPercent(token.getJwtToken());
    }

    @ApiOperation(value = "This method is used to referral buyers.")
    @GetMapping("/referralLink/{buyerId}")
    public Result<?> referralLink(@PathVariable Integer buyerId) {
        return buyerService.referralLink(buyerId);
    }


}
