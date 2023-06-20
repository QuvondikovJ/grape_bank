package com.example.uzum.dto.product;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Model for transfer product info.")
public class ProductDTO {

    @ApiModelProperty(notes = "This field is product uzbek name.")
    @Size(min = 4, message = "Product uzbek name must be 4 characters at least")
    @NotBlank(message = "Uzbek name must not be null or empty!")
    private String nameUz;

    @ApiModelProperty(notes = "This field is product english name.")
    @Size(min = 4, message = "Product english name must be 4 characters at least.")
    @NotBlank(message = "English name must not be null or empty!")
    private String nameEn;

    @ApiModelProperty(notes = "This field is category ID of product.")
    @Min(value = 0, message = "Seller ID need to be 0 at least. It can not be minus.")
    @NotNull(message = "Category ID can not be null!")
    private Integer categoryId;

    @ApiModelProperty(notes = "Attachment IDs of product.")
    @Size(min = 2, max = 10, message = "Attachment size need to be 2 at least.")
    @NotEmpty(message = "Attachments can not be null!")
    private List<Long> attachmentIds;

    @ApiModelProperty(notes = "Seller ID of product.")
    @Min(value = 1, message = "Seller ID need to be 1 at least. It can not be zero or minus.")
    @NotNull(message = "Seller ID can not be null!")
    private Integer sellerId;

    @ApiModelProperty(notes = "Delivery date of product.")
    @Min(value = 1, message = "Delivery date need to be 1 day at least.")
    @Max(value = 3, message = "Delivery date need to be 3 days at most.")
    @NotNull(message = "Delivery date can not be null!")
    private Integer deliveryDate;

    @ApiModelProperty(notes = "Amount of product.")
    @Min(value = 1, message = "Product amount need to be 1 at least.")
    @NotNull(message = "Amount can not be null!")
    private Integer amount;

    @ApiModelProperty(notes = "Price of product.")
    @Min(value = 1000, message = "Product cost need to be 1000 UZS at least.")
    @NotNull(message = "Price can not be null!")
    private Integer price;

    @ApiModelProperty(notes = "Brand ID of product.")
    @Min(value = 1, message = "Brand Id need to be 1 at least. It can not be zero or minus.")
    private Integer brandId;


    @ApiModelProperty(notes = "Discounted price of product.")
    @Min(value = 1000, message = "Discounted price need to be 1000 at least.")
    private Integer discountedPrice;

    @ApiModelProperty(notes = "Info of product.")
    private String info;

    @ApiModelProperty(notes = "Describe details of product.")
    private String describe;

    @ApiModelProperty(notes = "What is made of product.")
    private String made;

    @ApiModelProperty(notes = "Direction to use product.")
    private String directionToUse;

    @ApiModelProperty(notes = "Sizes of product.")
    private String size;

    @ApiModelProperty(notes = "Product is available to credit.")
    private Boolean isCredit;

    @ApiModelProperty(notes = "When product is being getting to credit, how much need to monthly pay to bank.")
    private Integer howMuchPerMonth;


}
