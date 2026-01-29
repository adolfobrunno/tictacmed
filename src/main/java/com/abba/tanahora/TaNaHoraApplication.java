package com.abba.tanahora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TaNaHoraApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaNaHoraApplication.class, args);
    }
}
