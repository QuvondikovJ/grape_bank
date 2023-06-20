package com.example.uzum.controller;

import com.example.uzum.dto.message.MessageDto;
import com.example.uzum.dto.Result;
import com.example.uzum.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    private MessageService messageService;


    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody MessageDto dto) {
        return messageService.add(dto);
    }

    @PutMapping("/edit/{id}")
    public Result<?> edit(@PathVariable Long id, @Valid @RequestBody MessageDto dto) {
        return messageService.edit(id, dto);
    }

    @PutMapping("/hasRead/{id}")
public Result<?> hasRead(@PathVariable Long id){
        return messageService.hasRead(id);
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id,
                            @RequestParam String operatorId,
                            @RequestParam String buyerCookie) {
        return messageService.delete(id, operatorId, buyerCookie);
    }
}
