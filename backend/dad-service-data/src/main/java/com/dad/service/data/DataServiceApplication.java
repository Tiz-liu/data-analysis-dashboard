package com.dad.service.data;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Data Service Application
 */
@SpringBootApplication(scanBasePackages = {"com.dad.service.data", "com.dad.common"})
@EnableDiscoveryClient
@MapperScan("com.dad.service.data.mapper")
public class DataServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataServiceApplication.class, args);
        System.out.println("\n" +
            "======================================\n" +
            "Data Service Started Successfully!\n" +
            "======================================\n");
    }
}
