package com.example.uzum.dto.comment;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "This object is used to transfer comment details.")
public class CommentDTO {

    @ApiModelProperty(notes = "Product ID that comment belongs to.")
    private Integer productId;

    @ApiModelProperty(notes = "Rate to comment. Comment rate must be between 1 and 5.")
    private Integer amountOfStars;

    @ApiModelProperty(notes = "This comment text.")
    @Size(min = 5, message = "Comment text must be 5 character at least.")
    @NotBlank(message = "Comment text can not be null.")
    private String text;

    @ApiModelProperty(notes = "Attachment IDs for comment")
    private List<Long> attachmentIds;

}
