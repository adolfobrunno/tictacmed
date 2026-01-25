package com.abba.tictacmed;

import org.springframework.boot.SpringApplication;

public class TestTictacmedApplication {

    public static void main(String[] args) {
        SpringApplication.from(TictacmedApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
