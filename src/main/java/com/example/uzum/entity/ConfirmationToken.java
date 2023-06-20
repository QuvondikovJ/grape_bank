package com.example.uzum.entity;

import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.sql.Timestamp;

import static org.hibernate.annotations.CascadeType.SAVE_UPDATE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String code;

    @CreationTimestamp
    private Timestamp createdAt;

    @Column
    private Timestamp expiresAt;

    @ManyToOne
    private Buyer buyer;

    @ManyToOne
    private Employee employee;

    @Column
    private Timestamp confirmedAt;

    @Column
    private Boolean isBlocked = false; //when user try to log in many times, then he will be blocked by this column

    @Column
    private String temporaryField; // When confirmation token is sent to buyer, we will need place to save new field by the confirmation time.
    // if buyer is confirmed, then we will use value of this key, otherwise this operation will not be performed.

}
