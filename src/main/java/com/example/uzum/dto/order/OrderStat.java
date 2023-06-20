package com.example.uzum.dto.order;

import com.example.uzum.entity.Branch;
import com.example.uzum.entity.Region;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderStat {

    private Region region; // If branch stat is called region will be null, otherwise branch will be null// .
    private Branch branch;
    private Integer created;
    private Integer delivered;
    private Integer sold;
    private Integer returned;
}
