package com.example.uzum.service;

import com.example.uzum.dto.message.MessageDto;
import com.example.uzum.dto.Result;
import org.springframework.stereotype.Service;


@Service
public interface MessageService {


    Result<?> add(MessageDto messageDto);

    Result<?> edit(Long id, MessageDto dto);

    Result<?> delete(Long id, String operatorId, String buyerCookie);

    Result<?> hasRead(Long id);
}
