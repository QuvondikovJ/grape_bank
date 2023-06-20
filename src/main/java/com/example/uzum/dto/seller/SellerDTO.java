package com.example.uzum.dto.seller;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SellerDTO {

    @ApiModelProperty(notes = "Name of seller.", required = true)
    @Size(min = 3, message = "Seller name must be 3 characters at least.")
    @NotBlank(message = "Seller name can not be null!")
    private String name;


    @ApiModelProperty(notes = "Info about seller.")
    @Size(min = 10, max = 1000, message = "Seller info must be 10 characters at least and 1000 characters at most.")
    @NotBlank(message = "Seller info can not be null!")
    private String info;

    @ApiModelProperty(notes = "Logo attachment ID.")
    private Long logoAttachmentId;

    @ApiModelProperty(notes = "Template attachment ID.")
    private Long templateAttachmentId;

    @ApiModelProperty(notes = "This field is saved employees that own or admins of seller shop.")
    @NotEmpty(message = "Employees' amount must be 1 at least.")
    private List<Integer> sellerIDs;

}
