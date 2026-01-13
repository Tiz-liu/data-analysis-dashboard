package com.dad.service.dashboard.controller;

import com.dad.common.result.Result;
import com.dad.service.dashboard.entity.Chart;
import com.dad.service.dashboard.service.ChartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Chart Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/charts")
@Api(tags = "Chart Management")
public class ChartController {

    @Autowired
    private ChartService chartService;

    /**
     * Get chart by ID
     */
    @GetMapping("/{id}")
    @ApiOperation("Get chart by ID")
    public Result<Chart> getById(@PathVariable Long id) {
        Chart chart = chartService.getById(id);
        if (chart == null) {
            return Result.error("Chart not found");
        }
        return Result.success(chart);
    }

    /**
     * Create chart
     */
    @PostMapping
    @ApiOperation("Create chart")
    public Result<Chart> create(@RequestBody Chart chart) {
        chart.setGuid(java.util.UUID.randomUUID().toString().replace("-", ""));

        // 初始化默认配置字段
        if (chart.getQueryConfig() == null || chart.getQueryConfig().trim().isEmpty()) {
            chart.setQueryConfig("{\"sql\":\"\"}");
        }
        if (chart.getChartConfig() == null || chart.getChartConfig().trim().isEmpty()) {
            chart.setChartConfig("{\"dimensions\":[],\"measures\":[]}");
        }
        if (chart.getStyleConfig() == null || chart.getStyleConfig().trim().isEmpty()) {
            chart.setStyleConfig("{}");
        }
        if (chart.getInteractionConfig() == null || chart.getInteractionConfig().trim().isEmpty()) {
            chart.setInteractionConfig("{}");
        }

        boolean success = chartService.save(chart);
        if (success) {
            return Result.success("Chart created successfully", chart);
        } else {
            return Result.error("Failed to create chart");
        }
    }

    /**
     * Update chart
     */
    @PutMapping("/{id}")
    @ApiOperation("Update chart")
    public Result<Chart> update(@PathVariable Long id, @RequestBody Chart chart) {
        // 获取数据库中的现有记录
        Chart existingChart = chartService.getById(id);
        if (existingChart == null) {
            return Result.error("Chart not found");
        }

        // 保留配置字段（如果前端没有传递）
        if (chart.getQueryConfig() == null) {
            chart.setQueryConfig(existingChart.getQueryConfig());
        }
        if (chart.getChartConfig() == null) {
            chart.setChartConfig(existingChart.getChartConfig());
        }
        if (chart.getStyleConfig() == null) {
            chart.setStyleConfig(existingChart.getStyleConfig());
        }
        if (chart.getInteractionConfig() == null) {
            chart.setInteractionConfig(existingChart.getInteractionConfig());
        }

        chart.setId(id);
        boolean success = chartService.updateById(chart);
        if (success) {
            return Result.success("Chart updated successfully", chart);
        } else {
            return Result.error("Failed to update chart");
        }
    }

    /**
     * Delete chart
     */
    @DeleteMapping("/{id}")
    @ApiOperation("Delete chart")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = chartService.removeById(id);
        if (success) {
            return Result.success("Chart deleted successfully");
        } else {
            return Result.error("Failed to delete chart");
        }
    }

    /**
     * Get chart data
     */
    @GetMapping("/{id}/data")
    @ApiOperation("Get chart data")
    public Result<Map<String, Object>> getData(@PathVariable Long id) {
        Map<String, Object> data = chartService.getChartData(id);
        return Result.success(data);
    }

    /**
     * Batch get chart data
     */
    @PostMapping("/batch/data")
    @ApiOperation("Batch get chart data")
    public Result<Map<Long, Map<String, Object>>> getBatchChartData(
            @RequestBody List<Long> chartIds) {

        if (chartIds == null || chartIds.isEmpty()) {
            return Result.error("Chart IDs cannot be empty");
        }

        if (chartIds.size() > 50) {
            return Result.error("Maximum 50 charts per batch request");
        }

        Map<Long, Map<String, Object>> resultMap = chartService.getBatchChartData(chartIds);
        return Result.success(resultMap);
    }
}
