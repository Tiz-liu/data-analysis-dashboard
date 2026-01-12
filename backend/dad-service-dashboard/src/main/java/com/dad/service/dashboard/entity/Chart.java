package com.dad.service.dashboard.entity;

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
 * Chart Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("charts")
@ApiModel(value = "Chart", description = "Chart entity")
public class Chart extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "Chart ID")
    private Long id;

    @ApiModelProperty(value = "Chart GUID")
    private String guid;

    @ApiModelProperty(value = "Dashboard ID")
    private Long dashboardId;

    @ApiModelProperty(value = "Chart name")
    private String name;

    @ApiModelProperty(value = "Description")
    private String description;

    @ApiModelProperty(value = "Chart type: LINE, BAR, PIE, etc.")
    private String chartType;

    @ApiModelProperty(value = "Data source ID")
    private Long dataSourceId;

    @ApiModelProperty(value = "Query configuration (JSON)")
    private String queryConfig;

    @ApiModelProperty(value = "Chart configuration (JSON)")
    private String chartConfig;

    @ApiModelProperty(value = "Style configuration (JSON)")
    private String styleConfig;

    @ApiModelProperty(value = "Interaction configuration (JSON)")
    private String interactionConfig;

    @ApiModelProperty(value = "Data cache seconds")
    private Integer dataCacheSeconds;

    @ApiModelProperty(value = "Position X")
    private Integer positionX;

    @ApiModelProperty(value = "Position Y")
    private Integer positionY;

    @ApiModelProperty(value = "Width (grid units)")
    private Integer width;

    @ApiModelProperty(value = "Height (grid units)")
    private Integer height;
}
