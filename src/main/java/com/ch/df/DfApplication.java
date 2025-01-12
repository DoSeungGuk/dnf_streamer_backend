package com.ch.df;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.ch.df.mapper")
@MapperScan("com.ch.df.dao")
public class DfApplication {

    public static void main(String[] args) {
        SpringApplication.run(DfApplication.class, args);
    }

}
