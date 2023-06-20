package com.example.uzum.dto.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BuyerDTO {
    private String firstname;
    private String lastname;
    private String middleName;
    private String email;
    @Size(min=13,max = 13, message = "Phone number must be 13 characters and start with +998 UZB code.")
    @NotBlank(message = "Phone number can not be null!")
    private String phoneNumber;
    private String password;
    private String gender;
    private String birthDate;
    private String cardNumber;
    private String cardExpiredDate;

}
