package com.example.uzum.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String buyerCookie;

    @Column
    private String name;

    @CreationTimestamp
    private Timestamp createdAt;

    @Column
    private Integer amountOfUnreadMessages;

    @Column
    private Boolean block = false; // if buyer violates the rules of requesting information, he will be blocked and can't send any messages to server

}
