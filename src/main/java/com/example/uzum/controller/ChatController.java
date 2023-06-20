package com.example.uzum.controller;

import com.example.uzum.dto.Result;
import com.example.uzum.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;


    @GetMapping("/getByCookie")
    public Result<?> getByCookie(@RequestBody String cookie,
                                          @RequestParam(defaultValue = "0") String page) {
        return chatService.getByCookie(cookie, page);
    }

    @GetMapping("/getById/{id}")
    public Result<?> getById(@PathVariable Long id,
                             @RequestParam(defaultValue = "0") String page) {
        return chatService.getById(id, page);
    }

    @GetMapping("/getUnreadChats")
    public Result<?> getUnreadChats(@RequestParam(defaultValue = "0") String page,
                                    @RequestParam(defaultValue = "nowWritten") String order) {
        return chatService.getUnreadChats(page, order);
    }

    @GetMapping("/getAllChats")
    public Result<?> getAllChats(@RequestParam(defaultValue = "0") String page,
                                 @RequestParam(defaultValue = "beforeAdded") String order) {
        return chatService.getAllChats(page, order);
    }

    @GetMapping("/edit/{id}")
    public Result<?> edit(@PathVariable Long id,
                          @RequestParam String name,
                          @RequestParam String isBlocked) {
        return chatService.edit(id, name, isBlocked);
    }

}
