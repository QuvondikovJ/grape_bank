package com.example.uzum.repository;

import com.example.uzum.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepo extends JpaRepository<Chat, Long> {

    Optional<Chat> findByBuyerCookie(String buyerCookie);

    @Query(value = "SELECT COUNT(id) FROM Chat ")
    Integer getByAmountOfChats();

    @Query(value = "SELECT COUNT(Chat) FROM Chat WHERE amountOfUnreadMessages>0 AND block=FALSE ")
    Integer getAmountOfUnreadChats();

    boolean existsByNameAndIdNot(String name, Long id);


}
