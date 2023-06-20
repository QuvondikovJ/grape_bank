package com.example.uzum.dto.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "This object is used to transfer order details.")
public class OrderDTO {

    @ApiModelProperty(notes = "This field is buyer ID.",required = true)
    @NotNull(message = "Buyer ID can not be null.")
    private Integer buyerId;

    @ApiModelProperty(notes = "This field is branch ID.")
    private Integer branchId;

    @ApiModelProperty(notes = "This field is that order is to home or to branch.", required = true)
    @NotNull(message = "To home can not be null. It must be true or false.")
    private boolean toHome;

    @ApiModelProperty(notes = "This field is home latitude.")
    private String homeLatitude;

    @ApiModelProperty(notes = "This field is home longitude.")
    private String homeLongitude;

    @ApiModelProperty(notes = "This field is payment type.",required = true)
    @NotNull(message = "Payment type can not be null.")
    private String paymentType;

    @ApiModelProperty(notes = "This field is buyer credit card number.")
    private String creditCardNumber;

    @ApiModelProperty(notes = "This field is buyer credit card expire date.")
    private String creditCardExpireDate;
}
