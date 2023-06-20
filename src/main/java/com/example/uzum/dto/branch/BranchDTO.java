package com.example.uzum.dto.branch;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchDTO {

    @ApiModelProperty(notes = "This field is english name of branch.")
    @Size(min = 4, message = "Branch english name must be 4 characters at least.")
    @NotBlank(message = "Branch english name can not be null.")
    private String nameEn;

    @ApiModelProperty(notes = "This field is uzbek name of branch.")
    @Size(min = 4, message = "Branch uzbek name must be 4 characters at least.")
    @NotBlank(message = "Branch uzbek name can not be null.")
    private String nameUz;

    @ApiModelProperty(notes = "This field is region ID that belongs to branch.")
    @NotNull(message = "Region ID can not be null.")
    private Integer regionId;

    @ApiModelProperty(notes = "This field is start time of work.")
    @Min(value = 5, message = "Start time of working will be between 5-11 AM. So enter hour that belongs to this interval.")
    @Max(value = 11, message = "Start time of working will be between 5-11 AM. So enter hour that belongs to this interval.")
    @NotNull(message = "Start time of working can not be null.")
    private Integer startTimeOfWorking;

    @ApiModelProperty(notes = "This field is end time of work.")
    @Min(value = 18, message = "End time of working will be between 18-23 AM. So enter hour that belongs to this interval.")
    @Max(value = 23, message = "End time of working will be between 18-23 AM. So enter hour that belongs to this interval.")
    @NotNull(message = "End time of working can not be null.")
    private Integer endTimeOfWorking;

    @ApiModelProperty(notes="This field is latitude of branch.")
    @NotNull(message = "Latitude of branch can not be null.")
    private double latitude;

    @ApiModelProperty(notes="This field is longitude of branch.")
    @NotNull(message = "Longitude of branch can not be null.")
    private double longitude;




}
