package com.freshlife;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.freshlife.mapper")
@SpringBootApplication
public class FreshLifeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FreshLifeApplication.class, args);
    }
}
