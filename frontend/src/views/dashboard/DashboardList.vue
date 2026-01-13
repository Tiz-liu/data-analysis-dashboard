<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useDashboardStore } from '@/stores/dashboard'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const dashboardStore = useDashboardStore()

// 使用 store 中的数据
const { dashboards, loading } = dashboardStore

// 组件挂载时获取数据
onMounted(() => {
  fetchDashboards()
})

// 获取仪表盘列表
const fetchDashboards = async () => {
  try {
    await dashboardStore.fetchDashboards()
  } catch (error) {
    ElMessage.error('获取仪表盘列表失败')
  }
}

// 创建仪表盘
const createDashboard = async () => {
  try {
    const { value } = await ElMessageBox.prompt(
      '请输入仪表盘名称',
      '新建仪表盘',
      {
        confirmButtonText: '创建',
        cancelButtonText: '取消',
        inputPattern: /\S+/,
        inputErrorMessage: '仪表盘名称不能为空'
      }
    )

    // 调用 store 的创建方法
    await dashboardStore.createDashboard({
      name: value,
      description: ''
    })

    ElMessage.success('仪表盘创建成功')

    // 刷新列表
    await fetchDashboards()

    // 可选：跳转到编辑页面
    // router.push(`/dashboard/${newDashboard.id}/edit`)
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('创建仪表盘失败')
      console.error(error)
    }
  }
}

// 删除仪表盘
const deleteDashboard = async (id: string, name: string) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除仪表盘"${name}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    await dashboardStore.deleteDashboard(id)
    ElMessage.success('删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 跳转到查看页面
const viewDashboard = (id: string) => {
  router.push(`/dashboard/${id}`)
}

// 跳转到编辑页面
const editDashboard = (id: string) => {
  router.push(`/dashboard/${id}/edit`)
}
</script>

<template>
  <div class="dashboard-list">
    <div class="header">
      <h2>仪表板列表</h2>
      <el-button type="primary" @click="createDashboard" :loading="loading">
        <el-icon><Plus /></el-icon>
        新建仪表板
      </el-button>
    </div>

    <el-row :gutter="20" class="dashboard-cards" v-if="!loading && dashboards.length > 0">
      <el-col :span="8" v-for="dashboard in dashboards" :key="dashboard.id">
        <el-card
          class="dashboard-card"
          shadow="hover"
          @click="viewDashboard(dashboard.id)"
        >
          <div class="card-header">
            <h3>{{ dashboard.name }}</h3>
            <el-dropdown @click.stop>
              <el-icon class="more-icon"><More /></el-icon>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click.stop="viewDashboard(dashboard.id)">
                    查看
                  </el-dropdown-item>
                  <el-dropdown-item @click.stop="editDashboard(dashboard.id)">
                    编辑
                  </el-dropdown-item>
                  <el-dropdown-item>导出</el-dropdown-item>
                  <el-dropdown-item
                    divided
                    @click.stop="deleteDashboard(dashboard.id, dashboard.name)"
                  >
                    删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <p class="description">{{ dashboard.description || '暂无描述' }}</p>
          <div class="footer">
            <span class="chart-count">
              <el-icon><PieChart /></el-icon>
              {{ dashboard.charts || 0 }} 个图表
            </span>
            <span class="update-time">{{ dashboard.updatedAt }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="3" animated />
    </div>

    <!-- 空状态提示 -->
    <el-empty
      v-if="!loading && dashboards.length === 0"
      description="暂无仪表盘，点击上方按钮创建"
      :image-size="200"
    >
      <el-button type="primary" @click="createDashboard">创建第一个仪表盘</el-button>
    </el-empty>
  </div>
</template>

<style scoped>
.dashboard-list {
  height: 100%;
  padding: 20px;
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
  margin-bottom: 20px;
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

.loading-container {
  padding: 20px;
}
</style>
