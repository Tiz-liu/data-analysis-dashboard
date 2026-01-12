package com.dad.service.dashboard.vo;

import com.dad.service.dashboard.entity.Chart;
import com.dad.service.dashboard.entity.Dashboard;
import com.dad.service.dashboard.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Dashboard View Object (with charts and owner)
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DashboardVO extends Dashboard {

    private User owner;
    private List<Chart> charts;
}
