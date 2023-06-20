package com.example.uzum.repository;

import com.example.uzum.entity.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FavouriteRepo extends JpaRepository<Favourite, Integer> {


    Optional<Favourite> findByCookieAndProductId(String cookie, Integer product_id);

    Optional<Favourite> findByBuyerIdAndProductId(Integer buyer_id, Integer product_id);

    @Query(value = "SELECT COUNT(fav.id) FROM Favourite AS fav WHERE fav.product.id=:productId")
    Integer getAmountOfFavouriteProducts(Integer productId);

    @Transactional
    @Modifying
    @Query(value = "DELETE Favourite AS fav WHERE fav.product.id=:productId")
    void deleteByProductId(Integer productId);

    @Transactional
    @Modifying
    @Query(value = "DELETE Favourite AS fav WHERE fav.cookie=:cookie")
    void deleteByCookie(String cookie);
}
