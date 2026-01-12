package com.dad.service.dashboard.controller;

import com.dad.common.result.Result;
import com.dad.service.dashboard.entity.Chart;
import com.dad.service.dashboard.service.ChartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Result<Void> delete(@PathVariable Long id) {
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
}
