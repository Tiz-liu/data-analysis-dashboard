import { http } from './request'
import type { Dashboard, Chart } from '@/types'

export const dashboardApi = {
  // 获取仪表板列表
  getList: () => {
    return http.get<Dashboard[]>('/dashboards')
  },

  // 获取仪表板详情
  getDetail: (id: string) => {
    return http.get<Dashboard>(`/dashboards/${id}`)
  },

  // 创建仪表板
  create: (data: Partial<Dashboard>) => {
    return http.post<Dashboard>('/dashboards', data)
  },

  // 更新仪表板
  update: (id: string, data: Partial<Dashboard>) => {
    return http.put<Dashboard>(`/dashboards/${id}`, data)
  },

  // 删除仪表板
  delete: (id: string) => {
    return http.delete(`/dashboards/${id}`)
  },

  // 获取仪表板下的图表
  getCharts: (dashboardId: string) => {
    return http.get<Chart[]>(`/dashboards/${dashboardId}/charts`)
  },

  // 添加图表
  addChart: (dashboardId: string, chart: Partial<Chart>) => {
    return http.post<Chart>(`/dashboards/${dashboardId}/charts`, chart)
  },

  // 更新图表
  updateChart: (dashboardId: string, chartId: string, chart: Partial<Chart>) => {
    return http.put<Chart>(`/dashboards/${dashboardId}/charts/${chartId}`, chart)
  },

  // 删除图表
  deleteChart: (dashboardId: string, chartId: string) => {
    return http.delete(`/dashboards/${dashboardId}/charts/${chartId}`)
  }
}
