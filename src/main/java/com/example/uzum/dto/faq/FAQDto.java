package com.example.uzum.dto.faq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class FAQDto {

    @NonNull
    private String queEn;
    @NonNull
    private String queRu;
    private String ansEn;
    private String ansRu;
    private Integer parentFaqId;

}
