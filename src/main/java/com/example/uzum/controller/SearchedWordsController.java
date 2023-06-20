package com.example.uzum.controller;

import com.example.uzum.dto.Result;
import com.example.uzum.service.SearchedWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.swing.plaf.PanelUI;

@RestController
@RequestMapping("/api/searchedWords")
public class SearchedWordsController {

    @Autowired
    private SearchedWordsService searchedWordsService;


    @GetMapping("/getAll")
    public Result<?> getAll(@RequestParam(defaultValue = "0") String page){
        return searchedWordsService.getAll(page);
    }

    @GetMapping("/getBySessionId")
    public Result<?> getBySessionId(@RequestParam String sessionId){
        return searchedWordsService.getBySessionId(sessionId);
    }

    @GetMapping("/getPromptWords")
    public Result<?> getPromptWordsForSearching(@RequestParam String search,
                                                @RequestParam String sessionId){
        return searchedWordsService.getPromptWordsForSearching(search, sessionId);
    }

    @DeleteMapping("/deleteBySessionId")
    public Result<?> deleteBySessionId(@RequestParam String sessionId){
        return searchedWordsService.deleteBySessionId(sessionId);
    }

    @DeleteMapping("/deleteById/{id}")
    public Result<?> deleteById(@PathVariable Long id){
        return searchedWordsService.deleteById(id);
    }

}
