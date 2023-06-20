package com.example.uzum.entity;


import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "This table is saved categories.")
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name_uz", "parent_category_id", "is_active"}),
        @UniqueConstraint(columnNames = {"name_en", "parent_category_id", "is_active"})})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name_uz", nullable = false)
    private String nameUz;

    @Column(name = "name_en",nullable = false)
    private String nameEn;


    @ManyToOne
    private Category parentCategory;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne
    private MainPanel mainPanel;

    public Category(String nameUz, String nameEn, Category parentCategory){
        this.nameUz = nameUz;
        this.nameEn = nameEn;
        this.parentCategory = parentCategory;
    }

}
