package com.example.uzum.dto.faq;

import com.example.uzum.entity.FAQ;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetFAQ {

    private Integer id;
    private String queEn;
    private String queRu;
    private String ansEn;
    private String ansRu;
    private Integer parentFAQId;
    private Timestamp createdAt;
    private List<GetFAQ> childFAQs;

}
