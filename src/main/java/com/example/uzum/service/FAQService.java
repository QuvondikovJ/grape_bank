package com.example.uzum.service;

import com.example.uzum.dto.faq.FAQDto;
import com.example.uzum.dto.Result;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Service
@Component
public interface FAQService {


    Result<?> add(FAQDto dto);

    Result<?> getAll();

    Result<?> getById(Integer id);

    Result<?> edit(Integer id, FAQDto faqDto);

    Result<?> delete(Integer id);
}
