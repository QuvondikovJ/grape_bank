package com.example.uzum.dto.seller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SellerDTOToSendUsers {

    private Integer id;
    private String name;
    private Integer amountSoldProducts;
    private Integer amountComments;
    private String rating;
    private String info;
    private Timestamp joiningDate;
    private List<Long> attachmentIds;

}
