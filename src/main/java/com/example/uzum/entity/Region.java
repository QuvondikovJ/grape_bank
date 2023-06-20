package com.example.uzum.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "This table is saved information of region.")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(notes = "This field is saved uzbek name of region.")
    @Size(min = 4, message = "Uzbek name of region must be 4 characters at least.")
    @NotBlank(message = "Uzbek name of region can not be null")
    @Column
    private String nameUz;

    @ApiModelProperty(notes = "This field is saved english name of region.", required = true)
    @Size(min = 4, message = "English name of region must e 4 characters at least.")
    @NotBlank(message = "English name of region can not be null.")
    @Column
    private String nameEn;

    @Column
    private boolean isActive = true;

    @ApiModelProperty(notes = "This field is saved latitude of region.", required = true)
    @NotNull(message = "Region latitude can not be null!")
    @Column
    private Double latitude;

    @ApiModelProperty(notes = "This field is saved longitude of region.", required = true)
    @NotNull(message = "Region longitude can not be null!")
    @Column
    private Double longitude;

}