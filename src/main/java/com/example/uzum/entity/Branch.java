package com.example.uzum.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("This table is saved information about branch.")
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String nameEn;
    @Column
    private String nameUz;
    @ManyToOne
    private Region region;

    @Column
    private Integer startTimeOfWorking;

    @Column
    private Integer endTimeOfWorking;

    @Column
    private boolean isActive = true;

    @Column
    private double latitude;

    @Column
    private double longitude;



}
