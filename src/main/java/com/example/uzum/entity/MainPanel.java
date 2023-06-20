package com.example.uzum.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ApiModel(value = "This table is used to save main panel details.")
public class MainPanel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String nameEn;

    @Column
    private String nameUz;

    @OneToOne
    private Attachment attachment;

    @Column
    private Integer panelOrder;

    @Column
    private String link;


    @Column
    private Boolean isActive = true;

    @Column
    private Integer howManyGetProduct;

    @Column
    private Boolean isDrawCarousel;

    @CreationTimestamp
    private Timestamp createdDate;

    /* Main panel connects to method,category,seller and product. When it will connect to one of them,
    then will not connect to another. When it connects to product, it can connect only to product again. */

}
