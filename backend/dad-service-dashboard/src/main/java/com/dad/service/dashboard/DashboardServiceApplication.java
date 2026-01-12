package com.dad.service.dashboard;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Dashboard Service Application
 */
@SpringBootApplication(scanBasePackages = {"com.dad.service.dashboard", "com.dad.common"})
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.dad.service.dashboard.mapper")
public class DashboardServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DashboardServiceApplication.class, args);
        System.out.println("\n" +
            "======================================\n" +
            "Dashboard Service Started Successfully!\n" +
            "======================================\n");
    }
}
