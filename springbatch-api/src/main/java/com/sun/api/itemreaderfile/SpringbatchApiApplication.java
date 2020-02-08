package com.sun.api.itemreaderfile;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class SpringbatchApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbatchApiApplication.class, args);
    }

}
