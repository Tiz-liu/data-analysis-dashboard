package com.dad.service.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dad.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Data Source Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("data_sources")
@ApiModel(value = "DataSource", description = "Data source entity")
public class DataSource extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "Data source ID")
    private Long id;

    @ApiModelProperty(value = "Data source GUID")
    private String guid;

    @ApiModelProperty(value = "Data source name")
    private String name;

    @ApiModelProperty(value = "Description")
    private String description;

    @ApiModelProperty(value = "Source type: MYSQL, POSTGRESQL, API, EXCEL, etc.")
    private String sourceType;

    @ApiModelProperty(value = "Connection configuration (JSON)")
    private String connectionConfig;

    @ApiModelProperty(value = "Connection parameters (JSON)")
    private String connectionParams;

    @ApiModelProperty(value = "Status: ACTIVE, INACTIVE, ERROR, TESTING")
    private String status;

    @ApiModelProperty(value = "Is connection tested")
    private Boolean isTested;

    @ApiModelProperty(value = "Last test time")
    private LocalDateTime lastTestAt;

    @ApiModelProperty(value = "Last test result (JSON)")
    private String lastTestResult;

    @ApiModelProperty(value = "Owner ID")
    private Long ownerId;
}
