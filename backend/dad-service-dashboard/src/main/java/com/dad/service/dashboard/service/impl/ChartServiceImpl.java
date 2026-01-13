package com.dad.service.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dad.common.result.Result;
import com.dad.common.util.JsonUtil;
import com.dad.service.dashboard.entity.Chart;
import com.dad.service.dashboard.feign.DataClient;
import com.dad.service.dashboard.mapper.ChartMapper;
import com.dad.service.dashboard.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Chart Service Implementation
 */
@Slf4j
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart> implements ChartService {

    @Autowired
    private DataClient dataClient;

    @Override
    @Cacheable(value = "chartData", key = "#chartId", unless = "#result == null || #result.isEmpty()")
    public Map<String, Object> getChartData(Long chartId) {
        log.info("Cache miss for chart {}, loading from database...", chartId);

        Chart chart = getById(chartId);
        if (chart == null) {
            throw new RuntimeException("Chart not found");
        }

        // Get query SQL from query config
        Map<String, Object> queryConfig = JsonUtil.fromJson(chart.getQueryConfig(), Map.class);
        String sql = (String) queryConfig.get("sql");

        // Execute query via Feign
        Result<Map<String, Object>> result = dataClient.executeQuery(
            chart.getDataSourceId(),
            sql,
            1,
            1000
        );

        if (result != null && result.getCode() == 200) {
            log.info("Chart {} data loaded successfully, cached for 5 minutes", chartId);
            return result.getData();
        } else {
            throw new RuntimeException("Failed to get chart data");
        }
    }

    @Override
    public Map<Long, Map<String, Object>> getBatchChartData(List<Long> chartIds) {
        log.info("Batch loading data for {} charts", chartIds.size());

        // 1. 批量查询图表配置
        List<Chart> charts = listByIds(chartIds);

        if (charts.isEmpty()) {
            log.warn("No charts found for given IDs: {}", chartIds);
            return Collections.emptyMap();
        }

        Map<Long, Map<String, Object>> resultMap = new HashMap<>();

        // 2. 并行加载每个图表的数据（利用线程池）
        charts.parallelStream().forEach(chart -> {
            try {
                // 调用getChartData方法（会自动使用缓存）
                Map<String, Object> data = getChartData(chart.getId());
                synchronized (resultMap) {
                    resultMap.put(chart.getId(), data);
                }
            } catch (Exception e) {
                log.error("Failed to load data for chart {}", chart.getId(), e);
                synchronized (resultMap) {
                    resultMap.put(chart.getId(), Collections.emptyMap());
                }
            }
        });

        int successCount = (int) resultMap.values().stream().filter(m -> !m.isEmpty()).count();
        log.info("Batch load completed: {}/{} successful", successCount, chartIds.size());

        return resultMap;
    }

    /**
     * 批量查询图表配置
     */
    private List<Chart> listByIds(List<Long> chartIds) {
        return list(new LambdaQueryWrapper<Chart>()
                .in(Chart::getId, chartIds));
    }

    @Override
    public List<Chart> getChartsByDashboardId(Long dashboardId) {
        LambdaQueryWrapper<Chart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Chart::getDashboardId, dashboardId);
        return list(queryWrapper);
    }
}
