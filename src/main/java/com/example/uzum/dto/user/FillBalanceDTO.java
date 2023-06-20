package com.example.uzum.dto.user;

import lombok.*;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FillBalanceDTO {

    @NotNull(message = "Buyer ID can not be null.")
    private Integer buyerId;
    private String cardNumber;
    private String cardExpireDate;
    @NotNull(message = "Money can not be null.")
    private Integer amountOfMoney;
}
