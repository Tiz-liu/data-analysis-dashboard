package com.dad.service.dashboard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dad.common.result.Result;
import com.dad.service.dashboard.entity.Chart;
import com.dad.service.dashboard.entity.Dashboard;
import com.dad.service.dashboard.entity.User;
import com.dad.service.dashboard.feign.UserClient;
import com.dad.service.dashboard.mapper.ChartMapper;
import com.dad.service.dashboard.mapper.DashboardMapper;
import com.dad.service.dashboard.service.DashboardService;
import com.dad.service.dashboard.vo.DashboardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Dashboard Service Implementation
 */
@Slf4j
@Service
public class DashboardServiceImpl extends ServiceImpl<DashboardMapper, Dashboard> implements DashboardService {

    @Autowired
    private ChartMapper chartMapper;

    @Autowired
    private UserClient userClient;

    @Override
    public DashboardVO getDetailById(Long id) {
        Dashboard dashboard = getById(id);
        if (dashboard == null) {
            return null;
        }

        DashboardVO vo = new DashboardVO();
        BeanUtils.copyProperties(dashboard, vo);

        // Get owner via Feign
        try {
            Result<User> userResult = userClient.getById(dashboard.getOwnerId());
            if (userResult != null && userResult.getCode() == 200) {
                vo.setOwner(userResult.getData());
            }
        } catch (Exception e) {
            log.error("Failed to get owner info", e);
        }

        // Get charts
        List<Chart> charts = chartMapper.selectList(new LambdaQueryWrapper<Chart>()
            .eq(Chart::getDashboardId, id)
            .orderByAsc(Chart::getPositionY)
            .orderByAsc(Chart::getPositionX));
        vo.setCharts(charts);

        return vo;
    }

    @Override
    public List<Dashboard> getByOwnerId(Long ownerId) {
        return list(new LambdaQueryWrapper<Dashboard>()
            .eq(Dashboard::getOwnerId, ownerId)
            .orderByDesc(Dashboard::getCreatedAt));
    }

    @Override
    @Transactional
    public void publish(Long id) {
        Dashboard dashboard = new Dashboard();
        dashboard.setId(id);
        dashboard.setStatus("PUBLISHED");
        dashboard.setPublishedAt(LocalDateTime.now());
        updateById(dashboard);
    }
}
