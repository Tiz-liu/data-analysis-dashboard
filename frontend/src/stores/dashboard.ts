import { defineStore } from 'pinia'
import { ref } from 'vue'
import { dashboardApi } from '@/api'
import type { Dashboard } from '@/types'

export const useDashboardStore = defineStore('dashboard', () => {
  const dashboards = ref<Dashboard[]>([])
  const currentDashboard = ref<Dashboard | null>(null)
  const loading = ref(false)

  // 获取仪表板列表
  const fetchDashboards = async () => {
    loading.value = true
    try {
      const data = await dashboardApi.getList()
      dashboards.value = data
    } finally {
      loading.value = false
    }
  }

  // 获取仪表板详情
  const fetchDashboard = async (id: string) => {
    loading.value = true
    try {
      const data = await dashboardApi.getDetail(id)
      currentDashboard.value = data
      return data
    } finally {
      loading.value = false
    }
  }

  // 创建仪表板
  const createDashboard = async (data: Partial<Dashboard>) => {
    const dashboard = await dashboardApi.create(data)
    dashboards.value.push(dashboard)
    return dashboard
  }

  // 更新仪表板
  const updateDashboard = async (id: string, data: Partial<Dashboard>) => {
    const dashboard = await dashboardApi.update(id, data)
    const index = dashboards.value.findIndex(d => d.id === id)
    if (index !== -1) {
      dashboards.value[index] = dashboard
    }
    if (currentDashboard.value?.id === id) {
      currentDashboard.value = dashboard
    }
    return dashboard
  }

  // 删除仪表板
  const deleteDashboard = async (id: string) => {
    await dashboardApi.delete(id)
    dashboards.value = dashboards.value.filter(d => d.id !== id)
    if (currentDashboard.value?.id === id) {
      currentDashboard.value = null
    }
  }

  return {
    dashboards,
    currentDashboard,
    loading,
    fetchDashboards,
    fetchDashboard,
    createDashboard,
    updateDashboard,
    deleteDashboard
  }
})
