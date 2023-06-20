package com.example.uzum.dto.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDto {

    private String buyerCookie;
    private Integer operatorId;
    private String text;
    private List<Long> attachmentIds;
    private String byWritten;

}
