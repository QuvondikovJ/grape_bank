package com.example.uzum.dto.category;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {

    @ApiModelProperty(notes = "This field is category uzbek name.")
    @Size(min = 4, message = "Category uzbek name must be 4 characters at least.")
    @NotBlank(message = "Uzbek name must not be null!")
    private String nameUz;

    @ApiModelProperty(notes = "This field is category english name.")
    @Size(min = 4, message = "Category english name must be 4 characters at least.")
    @NotBlank(message = "English name must not be null!")
    private String nameEn;

    @NotNull(message = "Parent Category can not be null!")
    private Integer parentCategoryId;

}
