package com.example.uzum.dto.mainPanel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "This object is used to to transfer data.")
public class MainPanelDTO {

    @ApiModelProperty(notes = "This field is panel english name, if it isn't attachment.")
    private String nameEn;

    @ApiModelProperty(notes = "This field is panel uzbek name, if it isn't attachment.")
    private String nameUz;

    @ApiModelProperty(notes = "Attachment ID of panel.")
    private Long attachmentId;

    @ApiModelProperty(notes = "Panel order.")
    private Integer order;

    @ApiModelProperty(notes = "Panel link, If panel is attachment, then link is available, otherwise it is ignored.")
    private String link;

    @ApiModelProperty(notes = "When panel isn't attachment, then how many products need to get.")
    private Integer howManyGetProduct;

    @ApiModelProperty(notes = "When panel isn't attachment, then how to draw products in panel.")
    private Boolean isDrawCarousel;

}
