import request from '@/utils/request'

export interface Chart {
  id?: number
  guid?: string
  dashboardId?: number
  name: string
  description?: string
  chartType: string
  dataSourceId: number
  queryConfig: string
  chartConfig: string
  positionX?: number
  positionY?: number
  width?: number
  height?: number
}

/**
 * Get chart by ID
 */
export function getChart(id: number) {
  return request({
    url: `/charts/${id}`,
    method: 'get'
  })
}

/**
 * Create chart
 */
export function createChart(data: Chart) {
  return request({
    url: '/charts',
    method: 'post',
    data
  })
}

/**
 * Update chart
 */
export function updateChart(id: number, data: Chart) {
  return request({
    url: `/charts/${id}`,
    method: 'put',
    data
  })
}

/**
 * Delete chart
 */
export function deleteChart(id: number) {
  return request({
    url: `/charts/${id}`,
    method: 'delete'
  })
}

/**
 * Get chart data
 */
export function getChartData(id: number) {
  return request({
    url: `/charts/${id}/data`,
    method: 'get'
  })
}

/**
 * Get charts by dashboard ID
 */
export function getChartListByDashboard(dashboardId: number) {
  return request({
    url: `/dashboards/${dashboardId}/charts`,
    method: 'get'
  })
}
