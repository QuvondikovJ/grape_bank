package com.example.uzum.repository;

import com.example.uzum.entity.Notification;
import com.example.uzum.entity.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {


    List<Notification> getByNotificationTypeAndEntityNameAndCauseOfNotificationAndNotifiedObjectIDInAndCreatedAtGreaterThan(NotificationType notificationType, String entityName, String causeOfNotification, Collection<Long> notifiedObjectID, Timestamp createdAt);
    List<Notification> getByNotificationTypeAndEntityNameAndCauseOfNotificationAndNotifiedObjectIDInAndCreatedAtLessThan(NotificationType notificationType, String entityName, String causeOfNotification, Collection<Long> notifiedObjectID, Timestamp createdAt);




}
