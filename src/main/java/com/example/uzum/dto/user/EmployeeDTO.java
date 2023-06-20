package com.example.uzum.dto.user;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ApiModel(description = "This object is used to transfer employee details.")
public class EmployeeDTO {

    @ApiModelProperty(notes = "This field is employee firstname.")
    @Size(min = 3, message = "Firstname must be 3 characters at least.")
    @NotBlank(message = "Firstname can not be null.")
    private String firstname;

    @ApiModelProperty(notes = "This field is employee lastname.")
    @Size(min = 3, message = "Lastname must be 3 characters at least.")
    @NotBlank(message = "Lastname can not be null.")
    private String lastname;

    @ApiModelProperty(notes = "This field is employee middleName.")
    private String middleName;

    @ApiModelProperty(notes = "This field is employee role. Role must be one of the following: Admin, Seller.")
    @NotBlank(message = "Role name can not be null.")
    private String roleName;

    @ApiModelProperty(notes = "This field is employee email.")
    @Size(min = 10, message = "Email must 11 characters at least, because it need like *@gmail.com")
    @NotBlank(message = "Email can not be null.")
    private String email;

    @ApiModelProperty(notes = "This field is employee phone number.")
    @Size(min = 13, max = 13, message = "Phone number must be 13 characters that registered in Uzbekistan.")
    @NotBlank(message = "Phone number must not be null!")
    private String phoneNumber;

    @ApiModelProperty(notes = "This field is employee password, It must be 8 characters at least and contain uppercase letter, lowercase letter and number.")
    @Size(min = 8, message = "Password must be 8 characters at least.")
    @NotBlank(message = "Password can not be null.")
    private String password;

    @ApiModelProperty(notes = "This field is employee gender and it must be either male and female.")
    @Size(min = 4, max = 6, message = "Gender must be male or female.")
    @NotBlank(message = "Gender can not be null.")
    private String gender;

    @ApiModelProperty(notes = "This field is employee birth date. It is like yyyy-mm-dd.")
    @Size(min = 8, message = "birth date must 8 characters at least when it is like yyyy-m-d.")
    @NotBlank(message = "Birth date can not be null.")
    private String birthDate;

    @ApiModelProperty(notes = "This field is employee card number and it must be 16 numbers.")
    @Size(min = 16, max = 16, message = "Card number must be 16 numbers.")
    @NotBlank(message = "Card number can not be null.")
    private String cardNumber;

    @ApiModelProperty(notes = "This field is employee card expire date and it must be 4 numbers.")
    @Size(min = 4, max = 4, message = "Card expire date must be 4 characters.")
    @NotBlank(message = "Card expire can not be null.")
    private String cardExpiredDate;

    @ApiModelProperty(notes = "This field is employee salary.")
    @Size(min = 10, message = "Employee salary must be 10$ at least.")
    @NotNull(message = "Salary can not be null.")
    private Integer salary;

}
