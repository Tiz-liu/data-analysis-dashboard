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
 * Dashboard Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dashboards")
@ApiModel(value = "Dashboard", description = "Dashboard entity")
public class Dashboard extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "Dashboard ID")
    private Long id;

    @ApiModelProperty(value = "Dashboard GUID")
    private String guid;

    @ApiModelProperty(value = "Dashboard name")
    private String name;

    @ApiModelProperty(value = "Description")
    private String description;

    @ApiModelProperty(value = "Thumbnail URL")
    private String thumbnail;

    @ApiModelProperty(value = "Layout configuration (JSON)")
    private String layoutConfig;

    @ApiModelProperty(value = "Style configuration (JSON)")
    private String styleConfig;

    @ApiModelProperty(value = "Filter configuration (JSON)")
    private String filterConfig;

    @ApiModelProperty(value = "Auto refresh interval (seconds)")
    private Integer refreshInterval;

    @ApiModelProperty(value = "Is public")
    private Boolean isPublic;

    @ApiModelProperty(value = "Is favorite")
    private Boolean isFavorite;

    @ApiModelProperty(value = "Category")
    private String category;

    @ApiModelProperty(value = "Tags (JSON)")
    private String tags;

    @ApiModelProperty(value = "Status: DRAFT, PUBLISHED, ARCHIVED")
    private String status;

    @ApiModelProperty(value = "Owner ID")
    private Long ownerId;

    @ApiModelProperty(value = "Published at")
    private LocalDateTime publishedAt;
}
