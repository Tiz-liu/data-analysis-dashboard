<script setup lang="ts">
import { ref } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const dashboardId = route.params.id as string
</script>

<template>
  <div class="dashboard-view">
    <div class="header">
      <h2>仪表板预览</h2>
      <el-space>
        <el-button>编辑</el-button>
        <el-button type="primary">AI分析</el-button>
      </el-space>
    </div>

    <div class="content">
      <el-empty description="仪表板内容" />
    </div>
  </div>
</template>

<style scoped>
.dashboard-view {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background: #fff;
  border-radius: 4px;
  margin-bottom: 20px;
}

.content {
  flex: 1;
  background: #fff;
  border-radius: 4px;
  padding: 20px;
}
</style>
