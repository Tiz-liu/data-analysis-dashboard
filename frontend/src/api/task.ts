import { http } from './request'
import type { Task, ExportParam, ImportParam } from '@/types'

export const taskApi = {
  // 获取任务列表
  getList: (params?: { status?: string; taskType?: string }) => {
    return http.get<Task[]>('/tasks', { params })
  },

  // 获取任务详情
  getDetail: (taskId: string) => {
    return http.get<Task>(`/tasks/${taskId}`)
  },

  // 创建导出任务
  createExport: (data: ExportParam) => {
    return http.post<Task>('/tasks/export', data)
  },

  // 创建导入任务
  createImport: (data: ImportParam) => {
    return http.upload<Task>('/tasks/import', data as any)
  },

  // 取消任务
  cancel: (taskId: string) => {
    return http.post(`/tasks/${taskId}/cancel`)
  },

  // 重试任务
  retry: (taskId: string) => {
    return http.post(`/tasks/${taskId}/retry`)
  },

  // 下载导出文件
  download: (taskId: string) => {
    return `/api/tasks/${taskId}/download`
  },

  // 验证导入文件
  validateImport: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return http.upload('/tasks/validate-import', formData)
  }
}
