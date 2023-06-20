package com.example.uzum.dto.externalServiceDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrencyDTO {

    private Integer id;
    private String code;
    private String ccy;
    private String ccyNm_RU;
    private String ccyNm_UZ;
    private String ccyNm_UZC;
    private String ccyNm_EN;
    private String nominal;
    private String rate;
    private String diff;
    private String date;


}
