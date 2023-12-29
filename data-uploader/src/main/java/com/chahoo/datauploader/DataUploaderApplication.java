package com.chahoo.datauploader;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.chahoo.datauploader.util.FloorUploader;


@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.chahoo.datauploader.mapper")
public class DataUploaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataUploaderApplication.class, args);
    }

    @Bean
    public CommandLineRunner myCommandLineRunner(CSVManager csvManager,FloorUploader floorUploader) {
        return args -> {

            floorUploader.uploadFloors();

            

            
           
        };
    }
}
