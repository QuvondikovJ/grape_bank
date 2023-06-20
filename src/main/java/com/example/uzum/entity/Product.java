package com.example.uzum.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name_uz", "category_id", "is_active"}),
        @UniqueConstraint(columnNames = {"name_en", "category_id", "is_active"})
})
@ApiModel(description = "Products are saved in this table.")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("This product ID. It should be unique.")
    private Integer id;

    @Column(name = "name_uz", nullable = false)
    private String nameUz;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column
    private Boolean isCredit;

    @Column
    private Integer howMuchPerMonth;  // if product may sell to credit

    @ManyToOne
    private Seller seller;

    @ManyToOne
    private Brand brand;

    @Column
    private Integer deliveryDate;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column
    private Integer discountedPrice;  // if product has not discount price, then its discount price will equal to price

    @Column
    private String info; /* This info is additional field, it is located below of the price */

    @Column
    private String describe;

    @Column
   private String made;

    @Column
    private String directionToUse;

    @Column
    private String size;

    @ManyToOne
    private Category category;

    /* If product is deleted by user, product deletes. If product is deleted when category, seller
     are deleted then product may be restored */
    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany
    private List<Attachment> attachments;

    @CreationTimestamp
    private Timestamp createdDate;

    @Column
    private Boolean temporaryDiscount = false; /* Aksiya */

    @Column
    private Timestamp fromDateOfTemporaryDiscount;

    @Column
    private Timestamp toDateOfTemporaryDiscount;

    @ManyToOne
    private MainPanel mainPanel;

    @Column
    private String rating = "0";

    @Column
    private Integer amountComments = 0;

    @Column
    private Integer percentOfRepliedComments = 0;

    @Column
    private Integer amountOfSoldProducts;

    @Column
    private Integer purchasedPriceFromSeller;

}
