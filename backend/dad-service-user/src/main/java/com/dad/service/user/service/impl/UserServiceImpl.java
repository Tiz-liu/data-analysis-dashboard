package com.dad.service.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dad.service.user.entity.User;
import com.dad.service.user.mapper.UserMapper;
import com.dad.service.user.service.UserService;
import org.springframework.stereotype.Service;

/**
 * User Service Implementation
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>()
            .eq(User::getUsername, username));
    }

    @Override
    public User getByGuid(String guid) {
        return getOne(new LambdaQueryWrapper<User>()
            .eq(User::getGuid, guid));
    }

    @Override
    public void updateLastLogin(Long userId, String ip) {
        User user = new User();
        user.setId(userId);
        user.setLastLoginAt(java.time.LocalDateTime.now());
        user.setLastLoginIp(ip);
        updateById(user);
    }
}
