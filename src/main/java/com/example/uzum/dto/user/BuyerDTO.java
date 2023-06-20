package com.example.uzum.dto.user;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "This object is used to transfer buyer details.")
public class BuyerDTO {

    @ApiModelProperty(notes = "This field is buyer firstname.")
    private String firstname;

    @ApiModelProperty(notes = "This field is buyer lastname.")
    private String lastname;

    @ApiModelProperty(notes = "This field is buyer middleName.")
    private String middleName;

    @ApiModelProperty(notes = "This field is buyer email.")
    private String email;

    @ApiModelProperty(notes = "This field is buyer phone number.")
    @Size(min = 13, max = 13, message = "Phone number must be 13 characters that registered in Uzbekistan.")
    @NotBlank(message = "Phone number can not be null!")
    private String phoneNumber;

    @ApiModelProperty(notes = "This field is buyer password, It must be 8 characters at least and contain uppercase letter, lowercase letter and number.")
    private String password;

    @ApiModelProperty(notes = "This field is buyer gender and it must be either male and female.")
    private String gender;

    @ApiModelProperty(notes = "This field is buyer birth date. It is like yyyy-mm-dd.")
    private String birthDate;

    @ApiModelProperty(notes = "This field is buyer card number and it must be 16 numbers.")
    private String cardNumber;

    @ApiModelProperty(notes = "This field is buyer card expire date and it must be 4 numbers.")
    private String cardExpiredDate;

}
