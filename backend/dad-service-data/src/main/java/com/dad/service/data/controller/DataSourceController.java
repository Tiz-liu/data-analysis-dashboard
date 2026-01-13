package com.dad.service.data.controller;

import com.dad.common.result.Result;
import com.dad.service.data.entity.DataSource;
import com.dad.service.data.service.DataSourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Data Source Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/datasources")
@Api(tags = "Data Source Management")
public class DataSourceController {

    @Autowired
    private DataSourceService dataSourceService;

    /**
     * Get data source list
     */
    @GetMapping
    @ApiOperation("Get data source list")
    public Result<List<DataSource>> list(@RequestParam(required = false) Long ownerId) {
        if (ownerId != null) {
            return Result.success(dataSourceService.getByOwnerId(ownerId));
        }
        return Result.success(dataSourceService.list());
    }

    /**
     * Get data source by ID
     */
    @GetMapping("/{id}")
    @ApiOperation("Get data source by ID")
    public Result<DataSource> getById(@PathVariable Long id) {
        DataSource dataSource = dataSourceService.getById(id);
        if (dataSource == null) {
            return Result.error("Data source not found");
        }
        return Result.success(dataSource);
    }

    /**
     * Create data source
     */
    @PostMapping
    @ApiOperation("Create data source")
    public Result<DataSource> create(@RequestBody DataSource dataSource) {
        dataSource.setGuid(java.util.UUID.randomUUID().toString().replace("-", ""));
        dataSource.setStatus("ACTIVE");
        boolean success = dataSourceService.save(dataSource);
        if (success) {
            return Result.success("Data source created successfully", dataSource);
        } else {
            return Result.error("Failed to create data source");
        }
    }

    /**
     * Update data source
     */
    @PutMapping("/{id}")
    @ApiOperation("Update data source")
    public Result<DataSource> update(@PathVariable Long id, @RequestBody DataSource dataSource) {
        dataSource.setId(id);
        boolean success = dataSourceService.updateById(dataSource);
        if (success) {
            return Result.success("Data source updated successfully", dataSource);
        } else {
            return Result.error("Failed to update data source");
        }
    }

    /**
     * Delete data source
     */
    @DeleteMapping("/{id}")
    @ApiOperation("Delete data source")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = dataSourceService.removeById(id);
        if (success) {
            return Result.success("Data source deleted successfully");
        } else {
            return Result.error("Failed to delete data source");
        }
    }

    /**
     * Test connection
     */
    @PostMapping("/{id}/test")
    @ApiOperation("Test data source connection")
    public Result<Map<String, Object>> testConnection(@PathVariable Long id) {
        Map<String, Object> result = dataSourceService.testConnection(id);
        return Result.success(result);
    }

    /**
     * Preview data
     */
    @PostMapping("/{id}/preview")
    @ApiOperation("Preview data from data source")
    public Result<Map<String, Object>> previewData(
        @PathVariable Long id,
        @RequestBody Map<String, String> request) {
        String sql = request.get("sql");
        Map<String, Object> result = dataSourceService.previewData(id, sql);
        return Result.success(result);
    }

    /**
     * Execute query
     */
    @PostMapping("/{id}/query")
    @ApiOperation("Execute query")
    public Result<Map<String, Object>> executeQuery(
        @PathVariable Long id,
        @RequestParam String sql,
        @RequestParam(defaultValue = "1") Integer pageNum,
        @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> result = dataSourceService.executeQuery(id, sql, pageNum, pageSize);
        return Result.success(result);
    }
}
