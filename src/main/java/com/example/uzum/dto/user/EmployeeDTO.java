package com.example.uzum.dto.user;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmployeeDTO {
    @NotBlank(message = "Firstname can not be null.")
    private String firstname;
    @NotBlank(message = "Lastname can not be null.")
    private String lastname;
    private String middleName;
    @NotBlank(message = "Role name can not be null.")
    private String roleName;
    @NotBlank(message = "Email can not be null.")
    private String email;
    @NotBlank(message = "Phone number must not be null!")
    private String phoneNumber;
    @NotBlank(message = "Password can not be null.")
    private String password;
    @NotBlank(message = "Gender can not be null.")
    private String gender;
    @NotBlank(message = "Birth date can not be null.")
    private String birthDate;
    @NotBlank(message = "Card number can not be null.")
    private String cardNumber;
    @NotBlank(message = "Card expire can not be null.")
    private String cardExpiredDate;
    @NotNull(message = "Salary can not be null.")
    private Integer salary;

}
