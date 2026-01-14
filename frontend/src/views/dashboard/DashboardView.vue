<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Edit } from '@element-plus/icons-vue'
import { dashboardApi } from '@/api/dashboard'
import { getBatchChartData } from '@/api/chart'
import type { Dashboard } from '@/types'
import type { Chart } from '@/types/chart'
import EChartsRenderer from '@/components/charts/EChartsRenderer.vue'
import ChartSkeleton from '@/components/charts/ChartSkeleton.vue'

const route = useRoute()
const router = useRouter()

const dashboard = ref<Dashboard>()
const charts = ref<Chart[]>([])
const chartsDataMap = ref<Map<number, any>>(new Map())
const loadingStates = ref<Map<number, boolean>>(new Map())
const loading = ref(false)

// 加载仪表盘详情
const loadDashboard = async () => {
  const id = route.params.id as string
  loading.value = true

  try {
    // 1. 并行加载仪表盘详情和图表列表
    const [dashboardData, chartsData] = await Promise.all([
      dashboardApi.getDetail(id),
      dashboardApi.getCharts(id)
    ])

    dashboard.value = dashboardData

    // 2. 准备图表列表
    const chartsList = (chartsData || []).map((chart: any) => ({
      id: chart.id,
      guid: chart.guid,
      dashboardId: chart.dashboardId,
      name: chart.name,
      description: chart.description,
      chartType: chart.chartType || chart.type || 'LINE',
      dataSourceId: chart.dataSourceId,
      queryConfig: chart.queryConfig || '{}',
      chartConfig: chart.chartConfig || '{}',
      positionX: chart.position?.x || chart.positionX || 0,
      positionY: chart.position?.y || chart.positionY || 0,
      width: chart.position?.w || chart.width || 6,
      height: chart.position?.h || chart.height || 4
    })) as Chart[]

    // 🔑 关键改进：立即设置 charts.value，显示骨架屏
    charts.value = chartsList

    // 3. 标记所有图表为"加载中"（显示骨架屏）
    chartsList.forEach(chart => {
      if (chart.id) {
        loadingStates.value.set(chart.id, true)  // true = 显示骨架屏
      }
    })

    // 4. 后台批量加载数据
    if (chartsList.length > 0) {
      const chartIds = chartsList
        .map(c => c.id)
        .filter((id): id is number => id != null)

      const batchData = await getBatchChartData(chartIds) as any

      // 5. 数据到达后，更新 Map 和状态
      chartsDataMap.value = new Map(
        Object.entries(batchData || {}).map(([key, value]) => [Number(key), value])
      )

      // 6. 逐个标记为"已完成"（显示真实图表）
      Object.keys(batchData || {}).forEach(key => {
        const chartId = Number(key)
        loadingStates.value.set(chartId, false)
      })
    }
  } catch (error) {
    ElMessage.error('加载仪表盘失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 返回列表
const handleBack = () => {
  router.push('/dashboard')
}

// 编辑仪表盘
const handleEdit = () => {
  router.push(`${route.path}/edit`)
}

onMounted(() => {
  loadDashboard()
})
</script>

<template>
  <div class="dashboard-view-container">
    <!-- Toolbar -->
    <div class="toolbar">
      <div class="left">
        <el-button @click="handleBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <el-divider direction="vertical" />
        <h2>{{ dashboard?.name || '仪表盘预览' }}</h2>
      </div>
      <div class="right">
        <el-button type="primary" @click="handleEdit">
          <el-icon><Edit /></el-icon>
          编辑
        </el-button>
      </div>
    </div>

    <!-- Canvas -->
    <div class="canvas">
      <!-- 图表网格 -->
      <div class="chart-grid" v-if="charts.length > 0">
        <div
          v-for="chart in charts"
          :key="chart.id"
          class="chart-item"
          :style="{
            gridColumn: `span ${chart.width || 6}`,
            gridRow: `span ${chart.height || 4}`
          }"
        >
          <!-- 🔑 关键：条件渲染骨架屏或真实图表 -->
          <div v-if="loadingStates.get(chart.id) === true">
            <ChartSkeleton
              :width="chart.width || 6"
              :height="chart.height || 4"
            />
          </div>

          <div v-else class="chart-card">
            <div class="chart-header" v-if="chart.name">
              <h3>{{ chart.name }}</h3>
              <el-tag size="small" v-if="chart.description">{{ chart.description }}</el-tag>
            </div>
            <div class="chart-body">
              <EChartsRenderer
                v-if="chart.id"
                :chart-id="chart.id"
                :chart-type="chart.chartType"
                :initial-data="chartsDataMap.get(chart.id)"
                height="100%"
              />
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty
        v-else-if="!loading"
        description="暂无图表，点击编辑按钮添加图表"
        :image-size="200"
      />
    </div>
  </div>
</template>

<style scoped lang="scss">
.dashboard-view-container {
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

    .chart-grid {
      display: grid;
      grid-template-columns: repeat(12, 1fr);
      grid-auto-rows: 80px;
      gap: 16px;

      .chart-item {
        .chart-card {
          height: 100%;
          background: #fff;
          border-radius: 4px;
          padding: 16px;
          box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
          display: flex;
          flex-direction: column;

          .chart-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 12px;
            padding-bottom: 12px;
            border-bottom: 1px solid #e8e8e8;
            flex-shrink: 0;

            h3 {
              margin: 0;
              font-size: 16px;
              color: #333;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }

          .chart-body {
            flex: 1;
            min-height: 0;
            overflow: hidden;
          }
        }
      }
    }
  }
}
</style>
