package com.example.demo.student;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class StudentConfig {

    @Bean
    CommandLineRunner commandLineRunner(
            StudentRepository repository
    ){
        return args -> {
//            Student daniel = new Student(
//                    "Daniel",
//                    LocalDate.of(1999, 12, 7),
//                    "daniel@czeschner.com"
//            );
//            repository.save(daniel);
        };
    }

}
