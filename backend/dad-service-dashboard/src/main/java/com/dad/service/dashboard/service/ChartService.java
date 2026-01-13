package com.dad.service.dashboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dad.service.dashboard.entity.Chart;

import java.util.List;
import java.util.Map;

/**
 * Chart Service Interface
 */
public interface ChartService extends IService<Chart> {

    /**
     * Get chart data
     */
    Map<String, Object> getChartData(Long chartId);

    /**
     * Get charts by dashboard ID
     */
    List<Chart> getChartsByDashboardId(Long dashboardId);
}
