package com.example.uzum.entity;

import com.example.uzum.entity.enums.CauseOfTransaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.sql.Timestamp;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String fromCardNumber;

    @Column(nullable = false)
    private String fromCardExpireDate;

    @Column(nullable = false)
    private String toCardNumber;

    @Column(nullable = false)
    private String toCardExpireDate;

    @ManyToOne
    private Employee employee; // who is being paid

    @ManyToOne
    private Buyer buyer; // who is paying

    @Column
    private Integer amountOfMoney;

    @Column
    private String forWhichMonth; // MAY, MARCH,....

    @Column
    private Integer forWhichYear; // 2021,2022,....

    @CreationTimestamp
    private Timestamp createdDate;

    @Enumerated(EnumType.STRING)
    private CauseOfTransaction causeOfTransaction;


}
