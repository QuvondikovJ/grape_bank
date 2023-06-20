package com.example.uzum.dto.favourite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavouriteDto {


    private Integer productId;
    private String cookie;
    private Integer buyerId;


}
