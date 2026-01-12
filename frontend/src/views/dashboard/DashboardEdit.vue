<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const dashboardId = route.params.id as string

const charts = ref([
  { id: '1', type: 'line', title: '趋势图', x: 0, y: 0, w: 6, h: 4 }
])

const addChart = () => {
  console.log('添加图表')
}

const saveDashboard = () => {
  console.log('保存仪表板')
}
</script>

<template>
  <div class="dashboard-edit">
    <div class="toolbar">
      <el-space>
        <el-button type="primary" @click="saveDashboard">
          <el-icon><icon-check /></el-icon>
          保存
        </el-button>
        <el-button @click="addChart">
          <el-icon><icon-plus /></el-icon>
          添加图表
        </el-button>
      </el-space>
    </div>

    <div class="canvas">
      <el-empty v-if="charts.length === 0" description="暂无图表，请添加图表" />
      <div v-else class="charts">
        <div v-for="chart in charts" :key="chart.id" class="chart-item">
          <div class="chart-header">
            <span>{{ chart.title }}</span>
            <el-icon class="drag-handle"><icon-rank /></el-icon>
          </div>
          <div class="chart-content">
            图表内容
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard-edit {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.toolbar {
  padding: 10px;
  background: #fff;
  border-radius: 4px;
  margin-bottom: 20px;
}

.canvas {
  flex: 1;
  background: #fff;
  border-radius: 4px;
  padding: 20px;
}

.charts {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  grid-gap: 20px;
}

.chart-item {
  background: #f5f7fa;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 15px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.drag-handle {
  cursor: move;
}

.chart-content {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}
</style>
