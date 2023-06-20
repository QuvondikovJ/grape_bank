package com.example.uzum.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(value = "This table is saved brands.")
public class Brand{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty(notes = "This field is brand name.")
    @Size(min = 3, message = "Brand name size must be 3 characters at least.")
    @NotBlank(message = "Brand name can not be null.")
    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "is_active")
    private Boolean isActive=true;

    @CreationTimestamp
    private Timestamp createdDate;

}
