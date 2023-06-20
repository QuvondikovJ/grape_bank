package com.example.uzum.repository;

import com.example.uzum.dto.soldProducts.GetStatForBank;
import com.example.uzum.entity.Orders;
import com.example.uzum.entity.Region;
import com.example.uzum.entity.enums.OrderStatus;
import com.example.uzum.entity.enums.PaymentType;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepo extends JpaRepository<Orders, Long> {

    @Query(value = "SELECT CASE WHEN COUNT(ord)> 0 THEN TRUE ELSE FALSE END FROM Orders AS ord WHERE ord.branch.id=:branchId")
    Boolean existsOrdersByBranchId(Integer branchId);

    @Query(value = "SELECT ord FROM Orders AS ord WHERE ord.branch.id=:branchId AND ord.status='PREPARING' ORDER BY ord.createdAt")
    Page<Orders> getByPreparing(Integer branchId, Pageable pageable);

    @Query(value = "SELECT ord FROM Orders AS ord WHERE ord.branch.id=:branchId AND ord.createdAt>:from AND ord.createdAt<:to ORDER BY ord.createdAt")
    Page<Orders> getByCreatedDate(Integer branchId, Timestamp from, Timestamp to, Pageable pageable);

    @Query(value = "SELECT COUNT(ord) FROM Orders AS ord WHERE ord.branch.id=:branchId AND ord.status='PREPARING'")
    Integer getAmountOfOrdersByPreparing(Integer branchId);

    @Query(value = "SELECT COUNT(ord) FROM Orders AS ord WHERE ord.branch.id=:branchId AND ord.createdAt>:from AND ord.createdAt<:to")
    Integer getAmountOfOrdersByCreatedDate(Integer branchId, Timestamp from, Timestamp to);

    @Query(value = "SELECT ord FROM Orders AS ord WHERE ord.branch.id=:branchId AND ord.status='DELIVERING' ORDER BY ord.createdAt ")
    Page<Orders> getByDelivering(Integer branchId, Pageable pageable);

    @Query(value = "SELECT COUNT(ord) FROM Orders AS ord WHERE ord.branch.id=:branchId AND ord.status='DELIVERING'")
    Integer getAmountOfOrdersByDelivering(Integer branchId);

    @Query(value = "SELECT COUNT(ord) FROM Orders AS ord WHERE ord.branch.id=:branchId AND ((ord.timeOfWaitingClient>:from AND ord.timeOfWaitingClient<:to) OR (ord.timeOfSelling>:from AND ord.timeOfSelling<:to) OR (ord.timeOfReturning>:from AND ord.timeOfReturning<:to AND ord.timeOfSelling=NULL))")
    Integer getAmountOfOrdersByDeliveredDate(Integer branchId, Timestamp from, Timestamp to);

    @Query(value = "SELECT ord FROM Orders AS ord WHERE ord.branch.id=:branchId AND ((ord.timeOfWaitingClient>:from AND ord.timeOfWaitingClient<:to) OR (ord.timeOfSelling>:from AND ord.timeOfSelling<:to) OR (ord.timeOfReturning>:from AND ord.timeOfReturning<:to AND ord.timeOfSelling=NULL)) ORDER BY ord.createdAt")
    Page<Orders> getByDeliveredDate(Integer branchId, Timestamp from, Timestamp to, Pageable pageable);

    @Query(value = "SELECT ord FROM Orders AS ord WHERE ord.branch.id=:branchId AND ord.timeOfReturning>:from AND ord.timeOfReturning<:to ORDER BY ord.timeOfReturning ")
    Page<Orders> getByReturned(Integer branchId, Timestamp from, Timestamp to, Pageable pageable);

    @Query(value = "SELECT COUNT(ord) FROM Orders AS ord WHERE ord.branch.id=:branchId AND ord.timeOfReturning>:from AND ord.timeOfReturning<:to")
    Integer getAmountOfOrdersByReturnedDate(Integer branchId, Timestamp from, Timestamp to);

    @Query(value = "SELECT ord FROM Orders AS ord WHERE ord.branch.id=:branchId AND ord.status='WAITING_CLIENT' ORDER BY ord.timeOfWaitingClient")
    Page<Orders> getWaitingClientOrdersByBranchId(Integer branchId, Pageable pageable);

    @Query(value = "SELECT COUNT(ord) FROM Orders AS ord WHERE ord.branch.id=:branchId AND ord.status='WAITING_CLIENT'")
    Integer getAmountOfWaitingClientOrders(Integer branchId);

    Page<Orders> getByBuyerIdOrderByCreatedAt(Integer buyer_id, Pageable pageable);

    @Query(value = "SELECT COUNT(ord) FROM Orders AS ord WHERE ord.branch.id=:branchId AND ord.timeOfSelling>:from AND ord.timeOfSelling<:to AND ord.timeOfReturning=NULL ")
    Integer getAmountOfOrdersBySoldDate(Integer branchId, Timestamp from, Timestamp to);

    @Query(value = "SELECT COUNT(ord) FROM Orders AS ord WHERE ord.branch.region=:region AND ord.createdAt>:from AND ord.createdAt<:to")
    Integer getAmountOfOrdersByCreatedDateAndRegion(Region region, Timestamp from, Timestamp to);

    @Query(value = "SELECT COUNT(ord) FROM Orders AS ord WHERE ord.branch.region=:region AND ((ord.timeOfWaitingClient>:from AND ord.timeOfWaitingClient<:to) OR (ord.timeOfSelling>:from AND ord.timeOfSelling<:to) OR (ord.timeOfReturning>:from AND ord.timeOfReturning<:to AND ord.timeOfSelling=NULL))")
    Integer getAmountOfOrdersByDeliveredDateAndRegion(Region region, Timestamp from, Timestamp to);

    @Query(value = "SELECT COUNT(ord) FROM Orders AS ord WHERE ord.branch.region=:region AND ord.timeOfSelling>:from AND ord.timeOfSelling<:to AND ord.timeOfReturning=NULL ")
    Integer getAmountOfOrdersBySoldDateAndRegion(Region region, Timestamp from, Timestamp to);

    @Query(value = "SELECT COUNT(ord) FROM Orders AS ord WHERE ord.branch.region=:region AND ord.timeOfReturning>:from AND ord.timeOfReturning<:to")
    Integer getAmountOfOrdersByReturnedDateAndRegion(Region region, Timestamp from, Timestamp to);

    @Query(value = "SELECT ord FROM Orders AS ord WHERE ord.branch.id=:branchId AND ord.toHome=TRUE AND ord.status='PREPARING' ")
    Page<Orders> getByToHome(Integer branchId, Pageable pageable);

    @Query(value = "SELECT ord FROM Orders AS ord WHERE ord.status='PREPARING' AND ord.createdAt<:oneDayAgo AND ord.createdAt>:twoDaysAgo")
    List<Orders> getByDelayingToPrepareForWarning(Timestamp oneDayAgo, Timestamp twoDaysAgo);

    @Query(value = "SELECT ord FROM Orders AS ord WHERE ord.status='PREPARING' AND ord.createdAt>:threeDaysAgo AND ord.createdAt<:twoDaysAgo")
    List<Orders> getByDelayingToPrepareForFire(Timestamp twoDaysAgo, Timestamp threeDaysAgo);

    @Query(value = "SELECT ord FROM Orders AS ord WHERE ord.status='PREPARING' AND ord.createdAt<:threeDaysAgo")
    List<Orders> getByDelayingToPrepareForExtremelyFire(Timestamp threeDaysAgo);

    @Query(value = "SELECT ord FROM Orders AS ord WHERE ord.status='DELIVERING' AND ord.timeOfGivingToDeliver>:twoDaysAgo AND ord.timeOfGivingToDeliver<:oneDayAgo")
    List<Orders> getByDelayingToDeliveryForWarning(Timestamp oneDayAgo, Timestamp twoDaysAgo);

    List<Orders> getByStatusAndTimeOfGivingToDeliverGreaterThanAndTimeOfGivingToDeliverLessThan(OrderStatus status, Timestamp twoDaysAgo, Timestamp oneDayAgo);

    List<Orders> getByStatusAndTimeOfGivingToDeliverLessThan(OrderStatus status, Timestamp threeDaysAgo);

    //    @Query(value = "SELECT ord FROM Orders AS ord WHERE ord.status='WAITING_CLIENT' AND ord.timeOfWaitingClient>:from AND ord.timeOfWaitingClient<=:to ORDER BY ord.timeOfWaitingClient ASC ")
    List<Orders> getByStatusAndTimeOfWaitingClientGreaterThanAndTimeOfWaitingClientLessThan(OrderStatus status, Timestamp from, Timestamp to);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Orders AS ord SET ord.status='RETURNED', ord.timeOfReturning=NOW() WHERE ord.status='WAITING_CLIENT' AND ord.timeOfWaitingClient<:fiveDaysAgo")
    void changeOrderStatusToReturn(LocalDateTime fiveDaysAgo);

    List<Orders> getByStatusAndTimeOfWaitingClientLessThan(OrderStatus status, Timestamp timeOfWaitingClient);

    @Query(value = "SELECT SUM(ord.moneyOfProducts) FROM Orders AS ord WHERE ord.status='SOLD' AND ord.timeOfSelling>=:from AND ord.timeOfSelling<:to AND ord.paymentType='CASH' ")
    Integer getByCash(Timestamp from, Timestamp to);

    @Query(value = "SELECT SUM(ord.moneyOfProducts) FROM Orders AS ord WHERE ord.status='SOLD' AND ord.timeOfSelling>=:from AND ord.timeOfSelling<:to AND ord.paymentType='CARD' ")
    Integer getByCard(Timestamp from, Timestamp to);

    @Query(value = "SELECT SUM(bp.product_amount) FROM orders AS ord INNER JOIN basket AS bk ON ord.basket_id=bk.id INNER JOIN basket_product AS bp ON bk.id=bp.basket_id WHERE ord.status='SOLD' AND ord.timeOfSelling>=:from AND ord.timeOfSelling<:to ", nativeQuery = true)
    Integer getAmountOfSoldProducts(Timestamp from, Timestamp to);

    @Query(value = "SELECT SUM(pro.purchased_price_from_seller*bp.product_amount) FROM orders AS ord INNER JOIN basket AS bk ON ord.basket_id=bk.id INNER JOIN basket_product AS bp ON bk.id=bp.basket_id INNER JOIN product AS pro ON bp.product_id=pro.id WHERE ord.status='SOLD' AND ord.timeOfSelling>=:from AND ord.timeOfSelling<:to ", nativeQuery = true)
    Integer getByOriginalPriceOfProducts(Timestamp from, Timestamp to);


}
