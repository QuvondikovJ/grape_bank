package com.example.uzum.entity;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@ApiModel(description = "This table is saved basket details.")
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Buyer buyer;

    @Column
    private String sessionId;

    @ElementCollection
    @CollectionTable(name = "basket_product", joinColumns = @JoinColumn(name = "basket_id", referencedColumnName = "id"))
    @MapKeyJoinColumn(name = "product_id")
    private Map<Product, Integer> productAmount;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @Column(name = "is_ordered")
    private boolean isOrdered = false;  // when buyer orders this basket, then this field will be true

    /* DELIVERY FEE IS 2000 UZS FOR PER KM. */

}
