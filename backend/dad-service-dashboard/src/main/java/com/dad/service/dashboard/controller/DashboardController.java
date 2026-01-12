package com.dad.service.dashboard.controller;

import com.dad.common.result.Result;
import com.dad.service.dashboard.entity.Dashboard;
import com.dad.service.dashboard.service.DashboardService;
import com.dad.service.dashboard.vo.DashboardVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Dashboard Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboards")
@Api(tags = "Dashboard Management")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Get dashboard list
     */
    @GetMapping
    @ApiOperation("Get dashboard list")
    public Result<List<Dashboard>> list(@RequestParam(required = false) Long ownerId) {
        if (ownerId != null) {
            return Result.success(dashboardService.getByOwnerId(ownerId));
        }
        return Result.success(dashboardService.list());
    }

    /**
     * Get dashboard detail
     */
    @GetMapping("/{id}")
    @ApiOperation("Get dashboard detail")
    public Result<DashboardVO> getDetail(@PathVariable Long id) {
        DashboardVO vo = dashboardService.getDetailById(id);
        if (vo == null) {
            return Result.error("Dashboard not found");
        }
        return Result.success(vo);
    }

    /**
     * Create dashboard
     */
    @PostMapping
    @ApiOperation("Create dashboard")
    public Result<Dashboard> create(@RequestBody Dashboard dashboard) {
        dashboard.setGuid(java.util.UUID.randomUUID().toString().replace("-", ""));
        dashboard.setStatus("DRAFT");
        boolean success = dashboardService.save(dashboard);
        if (success) {
            return Result.success("Dashboard created successfully", dashboard);
        } else {
            return Result.error("Failed to create dashboard");
        }
    }

    /**
     * Update dashboard
     */
    @PutMapping("/{id}")
    @ApiOperation("Update dashboard")
    public Result<Dashboard> update(@PathVariable Long id, @RequestBody Dashboard dashboard) {
        dashboard.setId(id);
        boolean success = dashboardService.updateById(dashboard);
        if (success) {
            return Result.success("Dashboard updated successfully", dashboard);
        } else {
            return Result.error("Failed to update dashboard");
        }
    }

    /**
     * Delete dashboard
     */
    @DeleteMapping("/{id}")
    @ApiOperation("Delete dashboard")
    public Result<Void> delete(@PathVariable Long id) {
        boolean success = dashboardService.removeById(id);
        if (success) {
            return Result.success("Dashboard deleted successfully");
        } else {
            return Result.error("Failed to delete dashboard");
        }
    }

    /**
     * Publish dashboard
     */
    @PostMapping("/{id}/publish")
    @ApiOperation("Publish dashboard")
    public Result<Void> publish(@PathVariable Long id) {
        dashboardService.publish(id);
        return Result.success("Dashboard published successfully");
    }
}
