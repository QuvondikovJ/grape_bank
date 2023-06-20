package com.example.uzum.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Time;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FAQ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String queRu;


    @Column
    private String queEn;

    @Column
    private String ansRu;

    @Column
    private String ansEn;

    @ManyToOne(fetch = FetchType.LAZY)
    private FAQ parentFAQ;

    @CreationTimestamp
    private Timestamp createdAt;

}
