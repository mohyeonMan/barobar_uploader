package com.chahoo.datauploader;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.chahoo.datauploader.mapper")
public class DataUploaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataUploaderApplication.class, args);
    }

    @Bean
    public CommandLineRunner myCommandLineRunner(ActionResolver selector) {
        return args -> {

            selector.selectAction();
            
        };
    }
}
