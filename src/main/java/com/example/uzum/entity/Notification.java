package com.example.uzum.entity;

import com.example.uzum.entity.enums.NotificationType;
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
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @CreationTimestamp
    private Timestamp createdAt;

    @Column
    private String entityName;

    @Column
    private Long notifiedObjectID;

    @Column
    private String causeOfNotification;

}
