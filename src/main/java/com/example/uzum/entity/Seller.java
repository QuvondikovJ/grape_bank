package com.example.uzum.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(value = "This table is saved information about seller.")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;

    @Column
    private Integer amountSoldProducts = 0;

    @Column
    private Integer amountComments = 0;

    @Column
    private String rating = "0";

    @Column
    private Integer percentOfAnsweredComments = 0;

    @Column
    private String info;

    @CreationTimestamp
    private Timestamp createdDate;

    @Column
    private Timestamp deletedDate;

    @ManyToMany
    private List<Attachment> attachments; // In this field is saved seller LOGO and TEMPLATE, so 0-index will be LOGO and 1-index will be TEMPLATE

    @Column
    private Boolean isActive = true;

    @Column
    private Long costOfSoldProducts = 0L;

    @Column
    private Integer amountOfProductsReturned = 0;

    @ManyToOne
    private MainPanel mainPanel;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<Employee> employees;

    @Column
    private Integer howMuchPaidByGrapeBank = 0;

}
