package com.example.uzum.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.web.server.Cookie;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDto {
    @NotBlank(message = "Phone number can not be null.")
    private String phoneNumber;
    private String password;
    private String code;
    private Cookie cookie;

}
