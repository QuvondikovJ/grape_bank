package com.example.uzum.repository;


import com.example.uzum.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Integer> {


    boolean existsByNameEnAndParentCategoryAndIsActiveOrNameUzAndParentCategoryAndIsActive(String nameEn, Category parentCategory, Boolean isActive, String nameUz, Category parentCategory2, Boolean isActive2);

    boolean existsByNameEnAndParentCategoryAndIsActiveAndIdNotOrNameUzAndParentCategoryAndIsActiveAndIdNot(String nameEn, Category parentCategory, Boolean isActive, Integer id, String nameUz, Category parentCategory2, Boolean isActive2, Integer id2);


    List<Category> getCategoriesByParentCategoryAndIsActive(Category parentCategory, Boolean isActive);

    List<Category> getCategoriesByParentCategoryIdAndIsActive(Integer parentCategory_id, Boolean isActive);


    Optional<Category> findByIdAndIsActive(Integer id, Boolean isActive);


    @Query(value = "SELECT ctg FROM Category AS ctg WHERE ctg.parentCategory=NULL AND " +
            " (ctg.isActive=FALSE OR ctg.id IN (SELECT fctg.parentCategory.id FROM Category AS fctg WHERE fctg.isActive=FALSE)" +
            " OR ctg.id IN (SELECT fCtg.parentCategory.id FROM Category AS fCtg WHERE fCtg.id IN (SELECT chCtg.parentCategory.id FROM Category AS chCtg WHERE chCtg.isActive=FALSE))) ")
    List<Category> getDeletedCategories();


    @Query(value = "SELECT ctg FROM Category AS ctg WHERE ctg.parentCategory.id=:grandCategoryId AND " +
            " (ctg.isActive=FALSE OR ctg.id IN (SELECT chCtg.parentCategory.id FROM Category AS chCtg WHERE chCtg.isActive=FALSE))")
    List<Category> getDeletedFatherCategories(Integer grandCategoryId);


    @Transactional
    @Modifying
    @Query(value = "UPDATE Category AS ctg SET ctg.isActive=:isActive WHERE " +
            "ctg.id=:id OR ctg.parentCategory.id=:id OR ctg.parentCategory.id IN (SELECT cc.id FROM Category AS cc WHERE cc.parentCategory.id=:id)")
    void deleteOrRestoreCategoriesFromGrand(Integer id, Boolean isActive);  // need join Product table to restore them which belongs to this category

    @Transactional
    @Modifying
    @Query(value = "UPDATE Category AS ctg " +
            " SET ctg.isActive=:isActive WHERE ctg.id=:id OR ctg.parentCategory.id=:id ")
    void deleteOrRestoreCategoriesFromFather(Integer id, Boolean isActive);  // need join Product table to restore them which belongs to this category

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE Category AS ctg  SET ctg.isActive=:isActive WHERE ctg.id=:id")
    void deleteOrRestoreCategoriesFromChild(Integer id, Boolean isActive);


    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Category AS ctg WHERE " +
            " (SELECT fctg.parentCategory.id FROM Category AS fctg WHERE fctg.id IN ctg.parentCategory.id)=:id OR " +
            " ctg.parentCategory.id=:id OR ctg.id=:id")
    void deleteFromGrand(Integer id); // need join Product table to restore them which belongs to this category

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Category AS fctg WHERE fctg.parentCategory.id=:id OR fctg.id=:id")
    void deleteFromFather(Integer id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Category AS chctg WHERE chctg.id=:id")
    void deleteFromChild(Integer id);

    @Query(value = "SELECT COUNT(ctg)>0 FROM Category AS ctg WHERE ctg.parentCategory.id=:parentCategoryId AND ctg.isActive=TRUE ")
    boolean existsChildCategoryByParentCategoryId(Integer parentCategoryId);

    Optional<Category> findByMainPanelIdAndIsActive(Integer mainPanel_id, Boolean isActive);

    boolean existsByMainPanelIdAndIsActive(Integer mainPanel_id, Boolean isActive);

    @Query(value = "SELECT ctg FROM Category AS ctg WHERE ctg.isActive=TRUE AND ctg.mainPanel IS NOT NULL ")
    List<Category> getConnectedPanelsToCategory();


    @Transactional
    @Modifying
    @Query(value = "UPDATE Category AS ctg SET ctg.mainPanel=NULL WHERE ctg.mainPanel.id=:panelId")
    void disconnectByMainPanelId(Integer panelId);
}
