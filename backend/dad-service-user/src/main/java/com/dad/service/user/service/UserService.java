package com.dad.service.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dad.service.user.entity.User;

/**
 * User Service Interface
 */
public interface UserService extends IService<User> {

    /**
     * Get user by username
     */
    User getByUsername(String username);

    /**
     * Get user by guid
     */
    User getByGuid(String guid);

    /**
     * Update last login info
     */
    void updateLastLogin(Long userId, String ip);
}
