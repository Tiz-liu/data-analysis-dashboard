package com.dad.service.dashboard.feign;

import com.dad.common.result.Result;
import com.dad.service.dashboard.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * User Service Client (OpenFeign)
 */
@FeignClient(name = "dad-service-user")
public interface UserClient {

    @GetMapping("/api/users/{id}")
    Result<User> getById(@PathVariable("id") Long id);
}
