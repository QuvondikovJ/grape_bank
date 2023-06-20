package com.example.uzum.entity;

import com.example.uzum.entity.enums.MessageWrittenBy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String buyerCookie;

    @ManyToOne
    private Employee operator;

    @Column
    private String text;

    @OneToMany
    private List<Attachment> attachments;

    @CreationTimestamp
    private Timestamp createdAt;

    @Column
    private Boolean isRead = false;

    @Column
    private Boolean isEdited = false;

    @Enumerated(EnumType.STRING)
    private MessageWrittenBy writtenBy;

    @ManyToOne
    private Chat chat;

}
