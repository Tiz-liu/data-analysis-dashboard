package com.dad.service.dashboard.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dad.common.result.Result;
import com.dad.common.util.JsonUtil;
import com.dad.service.dashboard.entity.Chart;
import com.dad.service.dashboard.entity.DataSource;
import com.dad.service.dashboard.feign.DataClient;
import com.dad.service.dashboard.mapper.ChartMapper;
import com.dad.service.dashboard.service.ChartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Chart Service Implementation
 */
@Slf4j
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart> implements ChartService {

    @Autowired
    private DataClient dataClient;

    @Override
    public Map<String, Object> getChartData(Long chartId) {
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
            return result.getData();
        } else {
            throw new RuntimeException("Failed to get chart data");
        }
    }
}
