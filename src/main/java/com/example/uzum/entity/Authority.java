package com.example.uzum.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;


import javax.persistence.*;

import static org.hibernate.annotations.CascadeType.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Authority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String authority;

    @ManyToOne
    private Employee employee;
}
