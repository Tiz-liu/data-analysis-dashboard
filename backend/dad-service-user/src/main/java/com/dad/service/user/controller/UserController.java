package com.dad.service.user.controller;

import com.dad.common.result.Result;
import com.dad.service.user.entity.User;
import com.dad.service.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@Api(tags = "User Management")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    @ApiOperation("Get user by ID")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("User not found");
        }
        // Hide password
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * Get user by GUID
     */
    @GetMapping("/guid/{guid}")
    @ApiOperation("Get user by GUID")
    public Result<User> getByGuid(@PathVariable String guid) {
        User user = userService.getByGuid(guid);
        if (user == null) {
            return Result.error("User not found");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * Get user by username
     */
    @GetMapping("/username/{username}")
    @ApiOperation("Get user by username")
    public Result<User> getByUsername(@PathVariable String username) {
        User user = userService.getByUsername(username);
        if (user == null) {
            return Result.error("User not found");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * Create user
     */
    @PostMapping
    @ApiOperation("Create user")
    public Result<User> create(@RequestBody User user) {
        // Check if username exists
        User existUser = userService.getByUsername(user.getUsername());
        if (existUser != null) {
            return Result.error("Username already exists");
        }

        // Save user
        boolean success = userService.save(user);
        if (success) {
            user.setPassword(null);
            return Result.success("User created successfully", user);
        } else {
            return Result.error("Failed to create user");
        }
    }

    /**
     * Update user
     */
    @PutMapping("/{id}")
    @ApiOperation("Update user")
    public Result<User> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        boolean success = userService.updateById(user);
        if (success) {
            user.setPassword(null);
            return Result.success("User updated successfully", user);
        } else {
            return Result.error("Failed to update user");
        }
    }

    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    @ApiOperation("Delete user")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = userService.removeById(id);
        if (success) {
            return Result.success("User deleted successfully");
        } else {
            return Result.error("Failed to delete user");
        }
    }
}
