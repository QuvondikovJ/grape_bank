package com.example.uzum.dto.order;

import com.example.uzum.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDTOWithAmount {

    private Page<Orders> orders;
    private Integer amountOfOrders;

}
