package com.example.uzum.controller;

import com.example.uzum.dto.faq.FAQDto;
import com.example.uzum.dto.Result;
import com.example.uzum.service.FAQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/faq")
public class FAQController {

    @Autowired
    @Qualifier("FAQServiceImpl")
    private FAQService faqService;



    @PostMapping("/add")
    public Result<?> add(@Valid @RequestBody FAQDto dto){
        return faqService.add(dto);
    }

   @GetMapping("/getAll")
    public Result<?> getAll(){
        return faqService.getAll();
   }

   @GetMapping("/getById{id}")
    public Result<?> getById(@PathVariable Integer id) {
       return faqService.getById(id);
   }

   @PutMapping("/edit/{id}")
   public Result<?> edit(@PathVariable Integer id, @Valid @RequestBody FAQDto faqDto){
        return faqService.edit(id, faqDto);
   }
   @DeleteMapping("/delete/{id}")
public Result<?> delete(@PathVariable Integer id){
        return faqService.delete(id);
   }

}
