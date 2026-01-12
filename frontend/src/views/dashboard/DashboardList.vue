<script setup lang="ts">
import { ref } from 'vue'

const dashboards = ref([
  {
    id: '1',
    name: '销售数据分析',
    description: '销售业绩分析仪表板',
    updatedAt: '2026-01-12 10:30:00',
    charts: 5
  },
  {
    id: '2',
    name: '用户行为分析',
    description: '用户行为数据分析仪表板',
    updatedAt: '2026-01-11 15:20:00',
    charts: 3
  }
])

const createDashboard = () => {
  console.log('创建新仪表板')
}
</script>

<template>
  <div class="dashboard-list">
    <div class="header">
      <h2>仪表板列表</h2>
      <el-button type="primary" @click="createDashboard">
        <el-icon><icon-plus /></el-icon>
        新建仪表板
      </el-button>
    </div>

    <el-row :gutter="20" class="dashboard-cards">
      <el-col :span="8" v-for="dashboard in dashboards" :key="dashboard.id">
        <el-card class="dashboard-card" shadow="hover">
          <div class="card-header">
            <h3>{{ dashboard.name }}</h3>
            <el-dropdown>
              <el-icon class="more-icon"><icon-more /></el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item>编辑</el-dropdown-item>
                  <el-dropdown-item>导出</el-dropdown-item>
                  <el-dropdown-item divided>删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <p class="description">{{ dashboard.description }}</p>
          <div class="footer">
            <span class="chart-count">
              <el-icon><icon-pie-chart /></el-icon>
              {{ dashboard.charts }} 个图表
            </span>
            <span class="update-time">{{ dashboard.updatedAt }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.dashboard-list {
  height: 100%;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
}

.dashboard-cards {
  margin-top: 20px;
}

.dashboard-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.dashboard-card:hover {
  transform: translateY(-4px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 18px;
}

.more-icon {
  cursor: pointer;
  font-size: 20px;
}

.description {
  color: #606266;
  font-size: 14px;
  margin: 10px 0;
}

.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #909399;
}

.chart-count {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
