package com.example.uzum.repository;

import com.example.uzum.entity.ViewedProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViewedProductsRepo extends JpaRepository<ViewedProducts, Long> {


    Optional<ViewedProducts> findBySessionIdAndProductId(String sessionId, Integer product_id);

    @Query(value = "SELECT * FROM viewed_products AS vp WHERE vp.session_id=:sessionId ORDER BY vp.created_at DESC LIMIT 20 ", nativeQuery = true)
    List<ViewedProducts> getBySessionId(String sessionId);
}
