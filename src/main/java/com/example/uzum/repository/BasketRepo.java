package com.example.uzum.repository;

import com.example.uzum.entity.Basket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BasketRepo extends JpaRepository<Basket, Long> {


    @Query(value = "SELECT bk FROM Basket AS bk WHERE bk.buyer.id=:buyerId AND bk.isOrdered=FALSE ")
    Optional<Basket> findByBuyerIdAndOrderedIs(Integer buyerId);

    @Query(value = "SELECT bk FROM Basket AS bk WHERE bk.sessionId=:sessionId AND bk.isOrdered=FALSE ")
    Optional<Basket> findBySessionIdAndOrderedIs(String sessionId);

@Query(value = "SELECT COUNT(*) FROM basket_product AS bp INNER JOIN basket AS bas ON bp.basket_id=bas.id WHERE bas.is_ordered=FALSE AND bp.product_id=:productId", nativeQuery = true)
    Integer getAmountOfBuyers(Integer productId);

@Query(value = "SELECT COUNT(bas) FROM Basket AS bas WHERE bas.isOrdered=FALSE ")
    Integer getAmountOfBaskets();
}
