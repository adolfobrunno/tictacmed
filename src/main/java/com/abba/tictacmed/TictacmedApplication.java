package com.abba.tictacmed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Locale;
import java.util.TimeZone;

@SpringBootApplication
public class TictacmedApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
        Locale.setDefault(Locale.forLanguageTag("pt-BR"));
        SpringApplication.run(TictacmedApplication.class, args);
    }

}
