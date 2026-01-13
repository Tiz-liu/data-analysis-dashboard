package com.dad.service.dashboard.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dad.service.dashboard.entity.Chart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 图表缓存失效管理器
 *
 * 提供缓存清除功能，用于：
 * 1. 数据源更新时清除相关图表缓存
 * 2. 系统维护时清除所有缓存
 *
 * @author orange
 */
@Slf4j
@Component
public class ChartCacheEvictor {

    private final ChartService chartService;

    public ChartCacheEvictor(ChartService chartService) {
        this.chartService = chartService;
    }

    /**
     * 清除指定数据源相关的所有图表缓存
     *
     * 当数据源配置更新时，调用此方法清除所有使用该数据源的图表缓存
     *
     * @param dataSourceId 数据源ID
     */
    @CacheEvict(value = "chartData", allEntries = true)
    public void evictByDataSource(Long dataSourceId) {
        List<Chart> affectedCharts = chartService.list(
            new QueryWrapper<Chart>().eq("data_source_id", dataSourceId)
        );
        log.info("Evicted cache for {} charts using dataSource {}",
            affectedCharts.size(), dataSourceId);
    }

    /**
     * 清除所有图表缓存（用于系统维护）
     *
     * 在以下场景可能需要调用：
     * 1. 系统升级后
     * 2. 批量数据导入后
     * 3. 手动强制刷新
     */
    @CacheEvict(value = "chartData", allEntries = true)
    public void evictAll() {
        log.info("Evicted all chart data cache");
    }
}
