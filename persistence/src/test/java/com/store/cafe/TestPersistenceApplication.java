package com.store.cafe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class TestPersistenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestPersistenceApplication.class, args);
    }
}