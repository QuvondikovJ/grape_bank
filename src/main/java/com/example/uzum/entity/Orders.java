package com.example.uzum.entity;

import com.example.uzum.entity.enums.OrderStatus;
import com.example.uzum.entity.enums.PaymentType;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Map;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@ApiModel(description = "This table is saved order details.")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Basket basket;

    @ManyToOne
    private Buyer buyer;

    @ManyToOne
    private Branch branch;

    @Column
    private boolean toHome;

    @Column
    private Integer priceOfDelivery;  // 1km = 2000 UZS from nearest branch to house

    @Column
    private String homeLatitude;

    @Column
    private String homeLongitude;

    @Column
    private boolean isPaid;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @CreationTimestamp
    private Timestamp createdAt;   // order is preparing

    @Column
    private Timestamp timeOfGivingToDeliver;  // order is being given to deliver

    @Column Timestamp timeOfDelivered;

    @Column
    private Timestamp timeOfWaitingClient;

    @Column
    private Timestamp timeOfSelling;   // order done, so buyer bought order products.

    @Column
    private Timestamp timeOfReturning; // order canceled, it may be while delivering or after selling by 7 days.

    @Column
    private String region;

    @Column
    private String district;

    @Column
    private String street;

    @Column
    private Integer moneyOfProducts;

    @ManyToOne
    private Employee deliver;

    @Column
    private String lastSixCharOFBuyerCard;

}
