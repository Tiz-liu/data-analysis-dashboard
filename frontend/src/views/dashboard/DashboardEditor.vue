<template>
  <div class="dashboard-editor-container">
    <!-- Toolbar -->
    <div class="toolbar">
      <div class="left">
        <el-button @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <el-divider direction="vertical" />
        <h2>{{ dashboard?.name || '新建仪表板' }}</h2>
      </div>
      <div class="right">
        <el-button @click="handleAddChart">
          <el-icon><Plus /></el-icon>
          添加图表
        </el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
        <el-button type="success" @click="handlePublish">发布</el-button>
      </div>
    </div>

    <!-- Canvas -->
    <div class="canvas">
      <el-empty v-if="!charts || charts.length === 0" description="暂无图表，点击'添加图表'开始创建" />

      <grid-layout
        v-else
        v-model:layout="chartLayout"
        :col-num="12"
        :row-height="80"
        :is-draggable="true"
        :is-resizable="true"
        :is-mirrored="false"
        :vertical-compact="true"
        :margin="[16, 16]"
        :use-css-transforms="true"
        @layout-updated="handleLayoutUpdate"
      >
        <grid-item
          v-for="item in chartLayout"
          :key="item.i"
          :x="item.x"
          :y="item.y"
          :w="item.w"
          :h="item.h"
          :i="item.i"
          :min-w="3"
          :min-h="3"
        >
          <ChartContainer
            v-if="getChartById(item.i)"
            :chart="getChartById(item.i)!"
            :is-editing="true"
            @edit="() => handleEditChart(getChartById(item.i)!)"
            @delete="() => handleDeleteChart(getChartById(item.i)!)"
          />
        </grid-item>
      </grid-layout>
    </div>

    <!-- Chart Config Drawer -->
    <el-drawer v-model="configDrawerVisible" title="图表配置" size="50%" :close-on-click-modal="false">
      <ChartConfig
        v-if="configDrawerVisible"
        :chart="currentChart"
        :dashboard-id="parseInt(dashboardId)"
        @save="handleSaveChart"
        @cancel="configDrawerVisible = false"
      />
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import { GridLayout, GridItem } from 'grid-layout-plus'
import { dashboardApi } from '@/api/dashboard'
import { createChart, updateChart, deleteChart, type Chart as ApiChart } from '@/api/chart'
import type { Chart, GridLayoutItem } from '@/types/chart'
import ChartContainer from '@/components/charts/ChartContainer.vue'
import ChartConfig from './components/ChartConfig.vue'

const route = useRoute()
const router = useRouter()
const dashboardId = ref<string>(route.params.id as string)
const dashboard = ref<any>()
const charts = ref<Chart[]>([])
const configDrawerVisible = ref(false)
const currentChart = ref<Chart>()

// Chart -> GridLayoutItem 转换
const chartLayout = computed<GridLayoutItem[]>({
  get: () => {
    return charts.value.map(chart => ({
      i: String(chart.id!),
      x: chart.positionX || 0,
      y: chart.positionY || 0,
      w: chart.width || 6,
      h: chart.height || 4,
      minW: 3,
      minH: 3
    }))
  },
  set: (newLayout) => {
    newLayout.forEach(item => {
      const chart = charts.value.find(c => String(c.id) === item.i)
      if (chart) {
        chart.positionX = item.x
        chart.positionY = item.y
        chart.width = item.w
        chart.height = item.h
      }
    })
  }
})

// Load dashboard
const loadDashboard = async () => {
  try {
    const dashboardData = await dashboardApi.getDetail(dashboardId.value)
    dashboard.value = dashboardData
    // 加载仪表板下的图表
    const chartsData = await dashboardApi.getCharts(dashboardId.value)
    // 类型转换：从 API 的 Chart 类型转换为组件使用的 Chart 类型
    charts.value = (chartsData || []).map((chart: any) => ({
      id: chart.id,
      guid: chart.guid,
      dashboardId: chart.dashboardId,
      name: chart.name,
      description: chart.description,
      chartType: chart.chartType || chart.type || 'LINE',
      dataSourceId: chart.dataSourceId,
      queryConfig: chart.queryConfig || chart.config ? JSON.stringify(chart.config) : '{}',
      chartConfig: chart.chartConfig || chart.config ? JSON.stringify(chart.config) : '{}',
      positionX: chart.position?.x || chart.positionX || 0,
      positionY: chart.position?.y || chart.positionY || 0,
      width: chart.position?.w || chart.width || 6,
      height: chart.position?.h || chart.height || 4
    })) as Chart[]
  } catch (error) {
    console.error('Failed to load dashboard:', error)
  }
}

// 防抖处理
let layoutUpdateTimer: any = null
const handleLayoutUpdate = (newLayout: GridLayoutItem[]) => {
  clearTimeout(layoutUpdateTimer)
  layoutUpdateTimer = setTimeout(() => {
    chartLayout.value = newLayout
    saveLayout()
  }, 500)
}

// 保存布局
const saveLayout = async () => {
  try {
    for (const chart of charts.value) {
      if (chart.id) {
        // 使用正确的 updateChart API
        await updateChart(chart.id, chart as ApiChart)
      }
    }
    ElMessage.success('布局已保存')
  } catch (error) {
    console.error('Failed to save layout:', error)
  }
}

// 根据ID获取图表
const getChartById = (id: string): Chart | undefined => {
  return charts.value.find(c => String(c.id) === id)
}

// Add chart
const handleAddChart = () => {
  currentChart.value = {
    name: '',
    chartType: 'LINE',
    dataSourceId: 0,
    queryConfig: '{}',
    chartConfig: '{}',
    width: 6,
    height: 4,
    positionX: 0,
    positionY: 0
  }
  configDrawerVisible.value = true
}

// Edit chart
const handleEditChart = (chart: Chart) => {
  currentChart.value = { ...chart }
  configDrawerVisible.value = true
}

// Save chart
const handleSaveChart = async (chart: Chart) => {
  try {
    // 确保 queryConfig 和 chartConfig 是有效字符串，如果为空则使用默认值
    const getDefaultQueryConfig = () => {
      try {
        // 尝试从表单的 sql 字段构建
        const sql = (chart as any).sql || ''
        return JSON.stringify({ sql })
      } catch {
        return JSON.stringify({ sql: '' })
      }
    }

    const getDefaultChartConfig = () => {
      try {
        // 尝试从表单的 dimension 和 measures 字段构建
        const dimension = (chart as any).dimension || ''
        const measures = (chart as any).measures || []
        return JSON.stringify({ dimensions: [dimension], measures })
      } catch {
        return JSON.stringify({ dimensions: [], measures: [] })
      }
    }

    const chartData: ApiChart = {
      ...chart,
      queryConfig: chart.queryConfig && chart.queryConfig !== 'undefined'
        ? chart.queryConfig
        : getDefaultQueryConfig(),
      chartConfig: chart.chartConfig && chart.chartConfig !== 'undefined'
        ? chart.chartConfig
        : getDefaultChartConfig()
    }

    if (chart.id) {
      // Update
      await updateChart(chart.id, chartData)
      ElMessage.success('更新成功')
    } else {
      // Create
      chartData.dashboardId = parseInt(dashboardId.value)
      await createChart(chartData)
      ElMessage.success('添加成功')
    }
    configDrawerVisible.value = false
    loadDashboard()
  } catch (error) {
    console.error('Failed to save chart:', error)
  }
}

// Delete chart
const handleDeleteChart = async (chart: Chart) => {
  try {
    await deleteChart(chart.id!)
    ElMessage.success('删除成功')
    loadDashboard()
  } catch (error) {
    console.error('Failed to delete chart:', error)
  }
}

// Save dashboard
const handleSave = async () => {
  try {
    await dashboardApi.update(dashboardId.value, dashboard.value!)
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('Failed to save dashboard:', error)
  }
}

// Publish dashboard
const handlePublish = async () => {
  try {
    // 注意：dashboardApi 中没有 publishDashboard 方法，需要根据实际 API 添加
    // 这里暂时使用 update 方法来模拟
    await dashboardApi.update(dashboardId.value, { ...dashboard.value, published: true })
    ElMessage.success('发布成功')
    loadDashboard()
  } catch (error) {
    console.error('Failed to publish dashboard:', error)
  }
}

// Back
const handleBack = () => {
  router.push('/dashboard/list')
}

onMounted(() => {
  loadDashboard()
})
</script>

<style scoped lang="scss">
.dashboard-editor-container {
  height: 100vh;
  display: flex;
  flex-direction: column;

  .toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    background: #fff;
    border-bottom: 1px solid #e8e8e8;

    .left {
      display: flex;
      align-items: center;
      gap: 12px;

      h2 {
        margin: 0;
        font-size: 18px;
        color: #333;
      }
    }

    .right {
      display: flex;
      gap: 12px;
    }
  }

  .canvas {
    flex: 1;
    padding: 20px;
    background: #f5f5f5;
    overflow-y: auto;
  }
}
</style>
