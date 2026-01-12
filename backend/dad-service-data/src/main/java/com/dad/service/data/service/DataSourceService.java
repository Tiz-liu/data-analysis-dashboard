package com.dad.service.data.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dad.service.data.entity.DataSource;

import java.util.List;
import java.util.Map;

/**
 * Data Source Service Interface
 */
public interface DataSourceService extends IService<DataSource> {

    /**
     * Get data sources by owner
     */
    List<DataSource> getByOwnerId(Long ownerId);

    /**
     * Test data source connection
     */
    Map<String, Object> testConnection(Long dataSourceId);

    /**
     * Execute query and return data
     */
    Map<String, Object> executeQuery(Long dataSourceId, String sql, Integer pageNum, Integer pageSize);

    /**
     * Preview data (with default limit)
     */
    Map<String, Object> previewData(Long dataSourceId, String sql);
}
