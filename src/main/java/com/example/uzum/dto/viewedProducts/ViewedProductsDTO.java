package com.example.uzum.dto.viewedProducts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "This object is used to transfer viewed product details.")
public class ViewedProductsDTO {

    @ApiModelProperty(notes = "This user session ID.")
    @NotBlank(message = "Session ID can not be null.")
    private String sessionId;

    @ApiModelProperty(notes = "This field is product ID that buyer has seen.")
    @NotNull(message = "Product ID can not be null.")
    private Integer productId;
}
