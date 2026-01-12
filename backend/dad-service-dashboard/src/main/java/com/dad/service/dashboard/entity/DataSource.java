package com.dad.service.dashboard.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Source Entity (simplified for Feign)
 */
@Data
public class DataSource {

    private Long id;
    private String guid;
    private String name;
    private String description;
    private String sourceType;
    private String connectionConfig;
    private String connectionParams;
    private String status;
    private Boolean isTested;
    private LocalDateTime lastTestAt;
    private String lastTestResult;
    private Long ownerId;
}
