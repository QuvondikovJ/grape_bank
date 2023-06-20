package com.example.uzum.repository;

import com.example.uzum.entity.Region;
import com.example.uzum.entity.SoldProducts;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface SoldProductsRepo extends JpaRepository<SoldProducts, Integer> {

    @Transactional
    @Modifying
    @Query(value = "DELETE SoldProducts AS sp WHERE sp.order.id=:orderId ")
    void deleteByOrderId(Long orderId);

    @Query(value = "SELECT SUM(bp.product_amount) FROM sold_products AS sp INNER JOIN orders AS ord ON sp.order_id=ord.id INNER JOIN basket AS bk ON ord.basket_id=bk.id INNER JOIN basket_product AS bp ON bk.id=bp.basket_id WHERE sp.sold_date>=:from AND sp.sold_date<:to AND ord.branch_id=:branchId", nativeQuery = true)
    Integer getSoldProductAmountByBranch(Integer branchId, Timestamp from, Timestamp to);


    @Query(value = "SELECT SUM(ord.moneyOfProducts) FROM SoldProducts AS sp INNER JOIN Orders AS ord ON sp.order.id=ord.id WHERE ord.branch.id=:branchId AND sp.soldDate>=:from AND sp.soldDate<:to AND ord.paymentType='CASH'")
    Integer getCashByBranch(Integer branchId, Timestamp from, Timestamp to);

    @Query(value = "SELECT SUM(ord.moneyOfProducts) FROM SoldProducts AS sp INNER JOIN Orders AS ord ON sp.order.id=ord.id WHERE ord.branch.id=:branchId AND sp.soldDate>=:from AND sp.soldDate<:to AND ord.paymentType='CARD'")
    Integer getCardByBranch(Integer branchId, Timestamp from, Timestamp to);

    @Query(value = "SELECT SUM(bp.product_amount) FROM sold_product AS sp INNER JOIN orders AS ord ON sp.order_id=ord.id INNER JOIN basket AS bk ON ord.basket_id=bk.id INNER JOIN basket_product AS bp ON bk.id=bp.basket_id WHERE sp.sold_date>=:from AND sp.sold_date<:to AND ord.branch_id IN (SELECT br.id FROM branch WHERE br.is_active=TRUE AND br.region_id=:regionId))", nativeQuery = true)
    Integer getSoldProductAmountByRegion(Integer regionId, Timestamp from, Timestamp to);

    @Query(value = "SELECT SUM(ord.moneyOfProducts) FROM SoldProducts AS sp INNER JOIN Orders AS ord ON sp.order.id=ord.id WHERE sp.soldDate>=:from AND sp.soldDate<:to AND ord.paymentType='CASH' AND ord.branch.region=:region")
    Integer getCashByRegion(Region region, Timestamp from, Timestamp to);

    @Query(value = "SELECT SUM(ord.moneyOfProducts) FROM SoldProducts AS sp INNER JOIN Orders AS ord ON sp.order.id=ord.id WHERE sp.soldDate>=:from AND sp.soldDate<:to AND ord.paymentType='CARD' AND ord.branch.region=:region")
    Integer getCardByRegion(Region region, Timestamp from, Timestamp to);

}
