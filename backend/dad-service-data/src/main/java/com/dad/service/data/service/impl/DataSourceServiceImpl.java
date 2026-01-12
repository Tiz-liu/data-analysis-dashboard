package com.dad.service.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dad.common.exception.BusinessException;
import com.dad.common.util.JsonUtil;
import com.dad.service.data.entity.DataSource;
import com.dad.service.data.mapper.DataSourceMapper;
import com.dad.service.data.service.DataSourceService;
import com.dad.service.data.util.SqlExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Source Service Implementation
 */
@Slf4j
@Service
public class DataSourceServiceImpl extends ServiceImpl<DataSourceMapper, DataSource> implements DataSourceService {

    @Autowired
    private SqlExecutor sqlExecutor;

    @Override
    public List<DataSource> getByOwnerId(Long ownerId) {
        return list(new LambdaQueryWrapper<DataSource>()
            .eq(DataSource::getOwnerId, ownerId)
            .orderByDesc(DataSource::getCreatedAt));
    }

    @Override
    public Map<String, Object> testConnection(Long dataSourceId) {
        DataSource dataSource = getById(dataSourceId);
        if (dataSource == null) {
            throw new BusinessException("Data source not found");
        }

        Map<String, Object> config = JsonUtil.fromJson(dataSource.getConnectionConfig(), Map.class);
        boolean success = sqlExecutor.testConnection(dataSource.getSourceType(), config);

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("message", success ? "Connection successful" : "Connection failed");
        result.put("testTime", System.currentTimeMillis());

        // Update test result
        dataSource.setIsTested(success);
        dataSource.setLastTestAt(java.time.LocalDateTime.now());
        dataSource.setLastTestResult(JsonUtil.toJson(result));
        updateById(dataSource);

        return result;
    }

    @Override
    public Map<String, Object> executeQuery(Long dataSourceId, String sql, Integer pageNum, Integer pageSize) {
        DataSource dataSource = getById(dataSourceId);
        if (dataSource == null) {
            throw new BusinessException("Data source not found");
        }

        Map<String, Object> config = JsonUtil.fromJson(dataSource.getConnectionConfig(), Map.class);
        return sqlExecutor.executeQuery(dataSource.getSourceType(), config, sql, pageNum, pageSize);
    }

    @Override
    public Map<String, Object> previewData(Long dataSourceId, String sql) {
        // Default: return first 100 rows
        return executeQuery(dataSourceId, sql, 1, 100);
    }
}
