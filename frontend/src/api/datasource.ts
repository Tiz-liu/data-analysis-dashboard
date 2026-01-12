import { http } from './request'
import type { DataSource } from '@/types'

export const dataSourceApi = {
  // 获取数据源列表
  getList: () => {
    return http.get<DataSource[]>('/datasources')
  },

  // 获取数据源详情
  getDetail: (id: string) => {
    return http.get<DataSource>(`/datasources/${id}`)
  },

  // 创建数据源
  create: (data: Partial<DataSource>) => {
    return http.post<DataSource>('/datasources', data)
  },

  // 更新数据源
  update: (id: string, data: Partial<DataSource>) => {
    return http.put<DataSource>(`/datasources/${id}`, data)
  },

  // 删除数据源
  delete: (id: string) => {
    return http.delete(`/datasources/${id}`)
  },

  // 测试连接
  testConnection: (id: string) => {
    return http.post<{ success: boolean; message: string }>(`/datasources/${id}/test`)
  },

  // 预览数据
  previewData: (id: string, sql?: string) => {
    return http.post(`/datasources/${id}/preview`, { sql })
  },

  // 获取表列表
  getTables: (id: string) => {
    return http.get<string[]>(`/datasources/${id}/tables`)
  },

  // 获取表结构
  getTableSchema: (id: string, table: string) => {
    return http.get(`/datasources/${id}/tables/${table}/schema`)
  }
}
