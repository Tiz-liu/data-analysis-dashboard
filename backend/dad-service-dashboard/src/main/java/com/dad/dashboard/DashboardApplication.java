package com.dad.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Dashboard Service Application
 */
@SpringBootApplication(scanBasePackages = {"com.dad.dashboard", "com.dad.common"})
@EnableDiscoveryClient
@EnableFeignClients
public class DashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(DashboardApplication.class, args);
        System.out.println("=========================================");
        System.out.println("Dashboard Service Started Successfully!");
        System.out.println("=========================================");
    }
}
