package com.example.uzum.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPhonesDto {
    @NotBlank(message = "Phone number can not be null!")
    private String phoneNumber;
    @NotBlank(message = "New phone number can not be null!")
    private String newPhoneNumber;

}
