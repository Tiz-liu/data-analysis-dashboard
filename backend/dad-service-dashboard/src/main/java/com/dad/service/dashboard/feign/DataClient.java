package com.dad.service.dashboard.feign;

import com.dad.common.result.Result;
import com.dad.service.dashboard.entity.DataSource;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Data Service Client (OpenFeign)
 */
@FeignClient(name = "dad-service-data")
public interface DataClient {

    @GetMapping("/api/datasources/{id}")
    Result<DataSource> getById(@PathVariable("id") Long id);

    @PostMapping("/api/datasources/{id}/query")
    Result<Map<String, Object>> executeQuery(
        @PathVariable("id") Long id,
        @RequestParam("sql") String sql,
        @RequestParam("pageNum") Integer pageNum,
        @RequestParam("pageSize") Integer pageSize
    );
}
