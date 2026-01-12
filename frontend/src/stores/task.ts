import { defineStore } from 'pinia'
import { ref } from 'vue'
import { taskApi } from '@/api'
import type { Task } from '@/types'

export const useTaskStore = defineStore('task', () => {
  const tasks = ref<Task[]>([])
  const currentTask = ref<Task | null>(null)
  const loading = ref(false)
  const websocket = ref<WebSocket | null>(null)

  // 获取任务列表
  const fetchTasks = async () => {
    loading.value = true
    try {
      const data = await taskApi.getList()
      tasks.value = data
    } finally {
      loading.value = false
    }
  }

  // 获取任务详情
  const fetchTask = async (taskId: string) => {
    loading.value = true
    try {
      const data = await taskApi.getDetail(taskId)
      currentTask.value = data
      return data
    } finally {
      loading.value = false
    }
  }

  // 创建导出任务
  const createExportTask = async (dashboardIds: string[]) => {
    const task = await taskApi.createExport({
      dashboardIds,
      taskId: generateTaskId()
    })
    tasks.value.unshift(task)
    initWebSocket(task.taskId)
    return task
  }

  // 创建导入任务
  const createImportTask = async (file: File, strategy: string) => {
    const task = await taskApi.createImport({
      file,
      strategy: strategy as any,
      taskId: generateTaskId()
    })
    tasks.value.unshift(task)
    initWebSocket(task.taskId)
    return task
  }

  // 取消任务
  const cancelTask = async (taskId: string) => {
    await taskApi.cancel(taskId)
    const task = tasks.value.find(t => t.taskId === taskId)
    if (task) {
      task.status = 'CANCELLED'
    }
    closeWebSocket()
  }

  // 重试任务
  const retryTask = async (taskId: string) => {
    await taskApi.retry(taskId)
    const task = tasks.value.find(t => t.taskId === taskId)
    if (task) {
      task.status = 'PENDING'
      initWebSocket(taskId)
    }
  }

  // 初始化 WebSocket
  const initWebSocket = (taskId: string) => {
    closeWebSocket()
    const ws = new WebSocket(`ws://localhost:8080/ws/task/${taskId}`)
    ws.onmessage = (event) => {
      const data = JSON.parse(event.data)
      updateTaskProgress(data)
    }
    ws.onerror = () => {
      console.error('WebSocket error')
    }
    ws.onclose = () => {
      console.log('WebSocket closed')
    }
    websocket.value = ws
  }

  // 关闭 WebSocket
  const closeWebSocket = () => {
    if (websocket.value) {
      websocket.value.close()
      websocket.value = null
    }
  }

  // 更新任务进度
  const updateTaskProgress = (data: Partial<Task>) => {
    const task = tasks.value.find(t => t.taskId === data.taskId)
    if (task) {
      Object.assign(task, data)
    }
    if (currentTask.value?.taskId === data.taskId) {
      Object.assign(currentTask.value, data)
    }
  }

  // 生成任务ID
  const generateTaskId = () => {
    return `task-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`
  }

  return {
    tasks,
    currentTask,
    loading,
    fetchTasks,
    fetchTask,
    createExportTask,
    createImportTask,
    cancelTask,
    retryTask,
    closeWebSocket
  }
})
