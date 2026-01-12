package com.dad.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base Entity
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creator ID (not in database, used in memory)
     */
    @TableField(fill = FieldFill.INSERT, exist = false)
    private Long createdBy;

    /**
     * Create Time
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * Updater ID (not in database, used in memory)
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, exist = false)
    private Long updatedBy;

    /**
     * Update Time
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /**
     * Delete Flag (0=Normal, 1=Deleted)
     */
    @TableLogic
    private Integer deleted;
}
