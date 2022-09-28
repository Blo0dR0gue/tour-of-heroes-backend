package com.example.demo.hero;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/hero")
public class HeroController {
    
    private final HeroService service;

    @Autowired
    public HeroController(HeroService service){
        this.service = service;
    }

    @GetMapping
    public List<Hero> getAll(){
        return this.service.getAll();
    } 

}
