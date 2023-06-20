package com.example.uzum.repository;

import com.example.uzum.entity.Chat;
import com.example.uzum.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {


    Page<Message> getByChatId(Long id, Pageable pageable);

    @Query(value = "SELECT mes.chat_id FROM (SELECT mess FROM message AS mess WHERE mess.is_read=FALSE ORDER BY mess.id) AS mes GROUP BY mes.chat_id ", nativeQuery = true)
    Page<Long> getChatIdsByUnreadMessages(Pageable pageable);
    @Query(value = "SELECT mes FROM (SELECT mess FROM (SELECT messs FROM message AS messs ORDER BY messs.id DESC) AS mess GROUP BY mes.chat_id) AS mes ORDER BY mes.created_at ", nativeQuery = true)
    Page<Message> getMessageByChatActive(Pageable pageable);

}
