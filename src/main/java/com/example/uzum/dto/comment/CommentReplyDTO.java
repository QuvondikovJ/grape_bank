package com.example.uzum.dto.comment;

import com.example.uzum.entity.Attachment;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "This object is used to for writing reply comment.")
public class CommentReplyDTO {

    @ApiModelProperty(notes = "Comment reply text.")
    @Size(min = 5, message = "Comment text must be 5 characters at least.")
    @NotBlank(message = "Comment text can not be null.")
    private String text;

    @ApiModelProperty(notes = "Attachment IDs for comment.")
    private List<Long> attachmentIds;

}
