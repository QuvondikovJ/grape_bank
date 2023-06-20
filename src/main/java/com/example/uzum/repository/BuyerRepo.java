package com.example.uzum.repository;

import com.example.uzum.entity.Buyer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface BuyerRepo extends JpaRepository<Buyer, Integer> {

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    Optional<Buyer> findByPhoneNumberAndIsActive(String phoneNumber, Boolean isActive);

    Optional<Buyer> findByIdAndIsActive(Integer id, Boolean isActive);

    @Query("SELECT buy FROM Buyer AS buy WHERE (buy.firstname LIKE :firstname% OR buy.firstname=:firstname) AND (buy.lastname LIKE :lastname% OR buy.lastname=:lastname) AND buy.isActive=TRUE")
    Page<Buyer> findByFirstnameAndLastname(String firstname, String lastname, Pageable pageable);

    @Query(value = "SELECT buy FROM Buyer AS buy WHERE (buy.firstname LIKE :firstname% OR buy.firstname=:firstname) AND buy.isActive=TRUE ")
    Page<Buyer> findByFirstname(String firstname, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Buyer AS buy SET buy.isActive=FALSE WHERE buy.id=:id")
    void blockBuyer(Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Buyer AS buy SET buy.isActive=TRUE WHERE buy.id=:id")
    void unblockBuyer(Integer id);

    @Query(value = "SELECT COUNT(buy) FROM Buyer AS buy WHERE buy.isActive=:isActive AND buy.createdDate>=:from AND buy.createdDate<:to")
    Integer getAmountOfActiveBuyers(Timestamp from, Timestamp to, Boolean isActive);

    Optional<Buyer> findByPhoneNumber(String phoneNumber);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Buyer AS buy SET buy.cashbackPercent=0.0 WHERE buy.isActive=TRUE AND buy.cashbackPercent>0 ")
    void cleanCashbackPercent();
}
