package com.example.demo.hero;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HeroService {
    
    private final HeroRepository repository;

    @Autowired
    public HeroService(HeroRepository repository){
        this.repository = repository;
    }

    public List<Hero> getAll(){
        return repository.findAll();
    }

    public Hero getById(int heroId) {
        Hero hero  = this.repository.findById(heroId).orElseThrow(() -> new EntityNotFoundException("Hero with id " + String.valueOf(heroId) + " not found"));
        return hero;
    }

}
