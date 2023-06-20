package com.example.uzum.entity;

import io.swagger.annotations.ApiModel;
import jdk.dynalink.linker.LinkerServices;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "This table is saved comment details.")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Buyer buyer; // Buyer only write comment for product, for product employee don't write any comments

    @ManyToOne
    private Product product;

    @ManyToOne
    private Employee employee; // Employee only write to reply comment of buyer

    @Column
    private Integer amountOfStars;

    @Column
    private String text;

    @OneToMany
    private List<Attachment> attachments;

    @CreationTimestamp
    private Timestamp createdAt;

    @Column
    private Boolean isActive = true;

    @OneToMany
    private List<Comment> replyComments;

    @Column
    private String whoWasWrittenBy;

}
