package com.dad.service.dashboard.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dad.service.dashboard.entity.Dashboard;
import com.dad.service.dashboard.vo.DashboardVO;

import java.util.List;

/**
 * Dashboard Service Interface
 */
public interface DashboardService extends IService<Dashboard> {

    /**
     * Get dashboard detail (with charts and owner)
     */
    DashboardVO getDetailById(Long id);

    /**
     * Get dashboards by owner
     */
    List<Dashboard> getByOwnerId(Long ownerId);

    /**
     * Publish dashboard
     */
    void publish(Long id);
}
