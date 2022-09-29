package com.example.demo.hero;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.AppConstants;

@RestController
@RequestMapping(path = AppConstants.HERO_URL)
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

    @GetMapping(path = "{heroId}")
    public Hero getHero(@PathVariable("heroId") int id){
        return this.service.getById(id);
    }

}
