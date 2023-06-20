package com.example.uzum.repository;

import com.example.uzum.dto.soldProducts.GetProductBySeller;
import com.example.uzum.entity.Category;
import com.example.uzum.entity.Orders;
import com.example.uzum.entity.Product;
import org.aspectj.weaver.ast.Or;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductRepo extends JpaRepository<Product, Integer> {

    boolean existsByNameEnAndCategoryIdAndIsActive(String nameEn, Integer category_id, Boolean isActive);

    boolean existsByNameUzAndCategoryIdAndIsActive(String nameUz, Integer category_id, Boolean isActive);

    Optional<Product> findByIdAndIsActive(Integer id, Boolean isActive);

    boolean existsByNameEnAndCategoryIdAndIsActiveAndIdNot(String nameEn, Integer category_id, Boolean isActive, Integer id);

    boolean existsByNameUzAndCategoryIdAndIsActiveAndIdNot(String nameUz, Integer category_id, Boolean isActive, Integer id);


    @Query(value = "SELECT pro.id FROM Product AS pro WHERE pro.isActive=TRUE AND pro.mainPanel.id=:mainPanelId ")
    List<Integer> getProductIdsByMainPanelId(Integer mainPanelId);


    @Transactional
    @Modifying
    @Query(value = "UPDATE Product AS pro SET pro.isActive=:isActive WHERE pro.seller.id=:sellerId")
    void deleteOrRestoreProductsBySellerId(Integer sellerId, Boolean isActive);

    Page<Product> findAllBySellerIdAndIsActive(Integer seller_id, Boolean isActive, Pageable pageable);

    @Query(value = "SELECT pr.id, pr.nameEn, pr.nameUz, pr.amountOfSoldProducts AS soldProductAmount, pr.amount AS leftProductAmount, pr.purchasedPriceFromSeller AS price, pr.purchasedPriceFromSeller*pr.amountOfSoldProducts AS costOfSoldProduct FROM Product AS pr WHERE pr.isActive=TRUE AND pr.seller.id=:sellerId ORDER BY (pr.purchasedPriceFromSeller*pr.amountOfSoldProducts)")
    Page<GetProductBySeller> getByCostOfSoldProducts(Integer sellerId, Pageable pageable);

    @Query(value = "SELECT pro FROM Product AS pro WHERE pro.isActive=TRUE AND pro.id IN (SELECT fav.product.id FROM Favourite AS fav WHERE fav.buyer.id=:buyerId) ")
    Page<Product> getFavouriteProductsByBuyerId(Integer buyerId, Pageable pageable);

    @Query(value = "SELECT pro FROM Product AS pro WHERE pro.isActive=TRUE AND pro.id IN (SELECT fav.product.id FROM Favourite AS fav WHERE fav.cookie=:cookie) ")
    Page<Product> getFavouriteProductsByCookie(String cookie, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Product AS pro SET pro.isActive=FALSE WHERE pro.category.id=:categoryId")
    void deleteByCategoryId(Integer categoryId);

    @Query(value = "SELECT pro.id FROM Product AS pro WHERE pro.isActive=TRUE AND pro.amount>0 AND pro.id<>:id AND pro.category.id=:categoryId ")
    List<Integer> getAmountOfProductsByCategoryId(Integer id, Integer categoryId);


    @Query(value = "SELECT pro FROM Product AS pro WHERE pro.id<>:id AND pro.category.id=:categoryId AND pro.isActive=TRUE AND pro.amount>0 ")
    List<Product> findAllByCategoryId(Integer id, Integer categoryId);

    @Query(value = "SELECT pro FROM Product AS pro WHERE pro.id IN :similarProductIdSet")
    List<Product> getProductsByProductIds(Set<Integer> similarProductIdSet);

    @Query(value = "SELECT pa.attachments_id FROM product_attachments AS pa WHERE pa.attachments_id IN :attachmentIdList AND pa.product_id<>:productId", nativeQuery = true)
    List<Long> getAttachmentIdsThatAlreadyAdded(Integer productId, List<Long> attachmentIdList);

    boolean existsByMainPanelIdAndIsActive(Integer mainPanel_id, Boolean isActive);

    @NotNull
    Product getById(Integer id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Product AS pro SET pro.mainPanel=NULL WHERE pro.mainPanel.id=:panelId")
    void disconnectByMainPanelId(Integer panelId);
}
