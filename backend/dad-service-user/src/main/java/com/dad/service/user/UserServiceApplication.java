package com.dad.service.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * User Service Application
 */
@SpringBootApplication(scanBasePackages = {"com.dad.service.user", "com.dad.common"})
@EnableDiscoveryClient
@MapperScan("com.dad.service.user.mapper")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        System.out.println("\n" +
            "======================================\n" +
            "User Service Started Successfully!\n" +
            "======================================\n");
    }
}
