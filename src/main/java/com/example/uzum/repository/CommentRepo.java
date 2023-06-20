package com.example.uzum.repository;

import com.example.uzum.entity.Category;
import com.example.uzum.entity.Comment;
import com.example.uzum.entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {

    boolean existsByBuyerIdAndProductIdAndTextAndIsActive(Integer buyer_id, Integer product_id, String text, Boolean isActive);

    boolean existsByEmployeeIdAndProductIdAndTextAndIsActive(Integer employee_id, Integer product_id, String text, Boolean isActive);

    @Query(value = "SELECT SUM(comm.amountOfStars) FROM Comment AS comm WHERE comm.product.id=:productId AND comm.isActive=TRUE ")
    Integer getAmountOfStarsByProductId(Integer productId);

    @Query(value = "SELECT SUM(comm.amountOfStars) FROM Comment AS comm WHERE comm.product.id IN (SELECT pro.id FROM Product AS pro WHERE pro.seller.id=:sellerId AND pro.isActive=TRUE) AND comm.isActive=TRUE ")
    Integer getAmountOfStarsBySellerId(Integer sellerId);

    Page<Comment> findByProductIdAndIsActive(Integer product_id, Boolean isActive, Pageable pageable);

    Page<Comment> findByBuyerIdAndIsActive(Integer buyer_id, Boolean isActive, Pageable pageable);

    @Query(value = "SELECT comm FROM Comment AS comm WHERE comm.product.id=:productId AND comm.replyComments.size=0 AND comm.isActive=TRUE ")
    Page<Comment> getUnansweredComments(Integer productId, Pageable pageable);

    Optional<Comment> findByIdAndIsActive(Long id, Boolean isActive);

    @Query(value = "SELECT comm.replyComments FROM Comment AS comm WHERE comm.id=:id AND comm.isActive=TRUE ")
    List<Comment> getReplyCommentsByCommentId(Long id);

    @Query(value = "SELECT COUNT(comm) FROM Comment AS comm WHERE comm.product.id=:productId AND comm.replyComments.size>0 AND comm.isActive=TRUE ")
    Integer getCountOfAnsweredCommentsByProductId(Integer productId);

    @Query(value = "SELECT COUNT(comm) FROM Comment AS comm WHERE comm.product.id=:productId AND comm.isActive=TRUE ")
    Integer getCountOfAllCommentsByProductId(Integer productId);

    @Query(value = "SELECT COUNT(comm) FROM Comment AS comm WHERE comm.product.id IN (SELECT pro.id FROM Product AS pro WHERE pro.seller.id=:sellerId AND pro.isActive=TRUE ) AND comm.replyComments.size>0 AND comm.isActive=TRUE ")
    Integer getCountOfAnsweredCommentsBySellerId(Integer sellerId);

    @Query(value = "SELECT COUNT(comm) FROM Comment AS comm WHERE comm.product.id IN (SELECT pro.id FROM Product AS pro WHERE pro.seller.id=:sellerId AND pro.isActive=TRUE ) AND comm.isActive=TRUE ")
    Integer getCountOfAllCommentsBySellerId(Integer sellerId);

    @Query(value = "SELECT comm.id FROM comment AS comm WHERE comm.id IN (SELECT crc.comment_id FROM comment_reply_comments AS crc WHERE crc.reply_comments_id=:id)", nativeQuery = true)
    Long findFatherCommentId(Long id);

    Comment getById(Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Comment AS comm SET comm.isActive=:isActive WHERE comm.product.id IN (SELECT pro.id FROM Product AS pro WHERE pro.seller.id=:sellerId)")
    void deleteOrRestoreCommentsBySellerId(Integer sellerId, boolean isActive);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Comment AS comm SET comm.isActive=FALSE WHERE comm.product.id IN (SELECT pro.id FROM Product AS pro WHERE pro.category.id=:categoryId)")
    void deleteByCategory(Integer categoryId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Comment AS comm WHERE comm.product.id=:productId ")
    void deleteByProductId(Integer productId);

}

