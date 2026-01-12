package com.dad.service.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dad.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * User Entity
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("users")
@ApiModel(value = "User", description = "User entity")
public class User extends BaseEntity {

    @TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "User ID")
    private Long id;

    @ApiModelProperty(value = "User GUID")
    private String guid;

    @ApiModelProperty(value = "Username")
    private String username;

    @ApiModelProperty(value = "Email")
    private String email;

    @ApiModelProperty(value = "Password (encrypted)")
    private String password;

    @ApiModelProperty(value = "Nickname")
    private String nickname;

    @ApiModelProperty(value = "Avatar URL")
    private String avatar;

    @ApiModelProperty(value = "Phone")
    private String phone;

    @ApiModelProperty(value = "Status: ACTIVE, INACTIVE, LOCKED, DELETED")
    private String status;

    @ApiModelProperty(value = "Role: ADMIN, USER, GUEST")
    private String role;

    @ApiModelProperty(value = "Department")
    private String department;

    @ApiModelProperty(value = "Position")
    private String position;

    @ApiModelProperty(value = "Last login time")
    private LocalDateTime lastLoginAt;

    @ApiModelProperty(value = "Last login IP")
    private String lastLoginIp;
}
