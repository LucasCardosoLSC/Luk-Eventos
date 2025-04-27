package com.lukeventos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.lukeventos")
public class EventosApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventosApplication.class, args);
    }
}