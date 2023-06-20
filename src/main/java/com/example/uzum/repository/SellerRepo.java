package com.example.uzum.repository;

import com.example.uzum.dto.soldProducts.GetAllSeller;
import com.example.uzum.entity.Seller;
import org.hibernate.sql.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepo extends JpaRepository<Seller, Integer> {

    boolean existsByNameAndIsActive(String name, Boolean isActive);

    boolean existsByNameAndIsActiveAndIdNot(String name, Boolean isActive, Integer id);

    Optional<Seller> findByIdAndIsActive(Integer id, Boolean isActive);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Seller AS sel SET sel.isActive=:isActive WHERE sel.id=:id")
    void deleteOrRestoreSeller(Integer id, Boolean isActive);

    Optional<Seller> findByMainPanelIdAndIsActive(Integer mainPanel_id, Boolean isActive);


    @Query(value = "SELECT sel.id, sel.name, sel.rating, COUNT(bp.product_amount) AS amountSoldProduct, SUM(pro.purchased_price_from_seller*bp.product_amount) AS costOfSoldProduct FROM " +
            " seller AS sel INNER JOIN product AS pro ON sel.id=pro.seller_id INNER JOIN " +
            " basket_product AS bp ON pro.id=bp.product_id INNER JOIN  basket AS bk ON bp.basket_id=bk.id INNER JOIN " +
            " orders AS ord ON bk.id=ord.basket_id WHERE " +
            " sel.isActive=TRUE AND ord.time_of_selling>=:from AND ord.time_of_selling<:to AND ord.status='SOLD' ", nativeQuery = true)
    Page<GetAllSeller> getAllSellers(Timestamp from, Timestamp to, Pageable pageable);

    @Query(value = "SELECT * FROM seller AS sel WHERE POSITION(:searchedText IN sel.name)>0 AND sel.created_date>=:from AND sel.created_date<:to AND sel.is_active=:isActiveBoolean", nativeQuery = true)
    Page<Seller> findByFilter(String searchedText, Timestamp from, Timestamp to, boolean isActiveBoolean, Pageable pageable);


    boolean existsByMainPanelIdAndIsActive(Integer mainPanel_id, Boolean isActive);

    @Query(value = "SELECT sel FROM Seller AS sel WHERE sel.isActive=TRUE AND sel.mainPanel IS NOT NULL ")
    List<Seller> getConnectedPanelsToSeller();

    @Transactional
    @Modifying
    @Query(value = "UPDATE Seller AS sel SET sel.mainPanel=NULL WHERE sel.mainPanel.id=:panelId ")
    void disconnectByMainPanelId(Integer panelId);
}