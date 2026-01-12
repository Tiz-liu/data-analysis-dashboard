import { http } from './request'
import type { AiAnalysisRequest, AiAnalysisResult } from '@/types'

export const aiApi = {
  // 创建分析任务
  createAnalysis: (data: AiAnalysisRequest) => {
    return http.post<{ taskId: string }>('/ai/analysis', data)
  },

  // 获取分析结果
  getResult: (taskId: string) => {
    return http.get<AiAnalysisResult>(`/ai/analysis/${taskId}`)
  },

  // 生成报告
  generateReport: (dashboardId: string) => {
    return http.post<{ taskId: string }>('/ai/report', { dashboardId })
  },

  // 获取报告
  getReport: (taskId: string) => {
    return http.get<{ report: string }>(`/ai/report/${taskId}`)
  }
}
