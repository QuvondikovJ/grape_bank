package com.example.uzum.dto.soldProducts;

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
public class GetByBranchAndRegionDto {

    private Region region;
    private Branch branch;
    private Integer totalSales;
    private Integer salesByCard;
    private Integer salesByCash;
    private Integer soldProductAmount;

}
