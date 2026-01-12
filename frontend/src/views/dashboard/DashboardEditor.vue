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
      <el-empty v-if="!charts || charts.length === 0" description="暂无图表，点击"添加图表"开始创建" />
      <div v-else class="chart-grid">
        <div
          v-for="chart in charts"
          :key="chart.id"
          class="chart-item"
          :style="{
            gridColumn: `span ${chart.width || 6}`,
            gridRow: `span ${chart.height || 4}`
          }"
        >
          <ChartContainer
            :chart="chart"
            @edit="handleEditChart"
            @delete="handleDeleteChart"
          />
        </div>
      </div>
    </div>

    <!-- Chart Config Drawer -->
    <el-drawer v-model="configDrawerVisible" title="图表配置" size="50%" :close-on-click-modal="false">
      <ChartConfig
        v-if="configDrawerVisible"
        :chart="currentChart"
        :dashboard-id="dashboardId"
        @save="handleSaveChart"
        @cancel="configDrawerVisible = false"
      />
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import { getDashboardDetail, updateDashboard, publishDashboard, type Dashboard, type DashboardVO } from '@/api/dashboard'
import { getChartListByDashboard, type Chart } from '@/api/chart'
import ChartContainer from './components/ChartContainer.vue'
import ChartConfig from './components/ChartConfig.vue'

const route = useRoute()
const router = useRouter()
const dashboardId = ref<number>(parseInt(route.params.id as string))
const dashboard = ref<DashboardVO>()
const charts = ref<Chart[]>([])
const configDrawerVisible = ref(false)
const currentChart = ref<Chart>()

// Load dashboard
const loadDashboard = async () => {
  try {
    const res = await getDashboardDetail(dashboardId.value)
    dashboard.value = res.data
    charts.value = res.data.charts || []
  } catch (error) {
    console.error('Failed to load dashboard:', error)
  }
}

// Add chart
const handleAddChart = () => {
  currentChart.value = {
    name: '',
    chartType: 'LINE',
    dataSourceId: 0,
    queryConfig: '',
    chartConfig: '',
    width: 6,
    height: 4
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
    if (chart.id) {
      // Update
      await updateChart(chart.id, chart)
      ElMessage.success('更新成功')
    } else {
      // Create
      chart.dashboardId = dashboardId.value
      await createChart(chart)
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
    await updateDashboard(dashboardId.value, dashboard.value!)
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('Failed to save dashboard:', error)
  }
}

// Publish dashboard
const handlePublish = async () => {
  try {
    await publishDashboard(dashboardId.value)
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

    .chart-grid {
      display: grid;
      grid-template-columns: repeat(12, 1fr);
      grid-auto-rows: 80px;
      gap: 16px;

      .chart-item {
        background: #fff;
        border-radius: 4px;
        overflow: hidden;
      }
    }
  }
}
</style>
