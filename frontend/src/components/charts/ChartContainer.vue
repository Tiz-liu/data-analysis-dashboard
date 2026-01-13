<template>
  <div
    class="chart-container"
    :class="{ 'is-editing': isEditing }"
    @click="handleContainerClick"
  >
    <!-- 工具栏 (仅编辑模式显示) -->
    <div v-if="isEditing" class="chart-toolbar">
      <el-button
        type="primary"
        size="small"
        :icon="Edit"
        @click.stop="handleEdit"
      >
        编辑
      </el-button>
      <el-button
        type="danger"
        size="small"
        :icon="Delete"
        @click.stop="handleDelete"
      >
        删除
      </el-button>
    </div>

    <!-- 图表内容区域 -->
    <div class="chart-content">
      <!-- 图表标题 -->
      <h3 v-if="chart.name" class="chart-title">{{ chart.name }}</h3>

      <!-- 图表描述 -->
      <p v-if="chart.description" class="chart-description">
        {{ chart.description }}
      </p>

      <!-- 图表渲染区域 -->
      <EChartsRenderer
        v-if="chart.id"
        :chart-id="chart.id"
        :chart-type="chart.chartType"
        height="100%"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { Edit, Delete } from '@element-plus/icons-vue'
import type { Chart } from '@/types/chart'
import EChartsRenderer from './EChartsRenderer.vue'

// Props 定义
interface Props {
  chart: Chart
  isEditing?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  isEditing: false
})

// Emits 定义
const emit = defineEmits<{
  edit: []
  delete: []
}>()

// 获取图表类型标签
const getChartTypeLabel = (type: string): string => {
  const typeMap: Record<string, string> = {
    LINE: '折线图',
    BAR: '柱状图',
    PIE: '饼图',
    TABLE: '表格',
    GAUGE: '仪表盘',
    SCATTER: '散点图',
    AREA: '面积图'
  }
  return typeMap[type] || '未知图表类型'
}

// 事件处理
const handleEdit = () => {
  emit('edit')
}

const handleDelete = () => {
  emit('delete')
}

const handleContainerClick = () => {
  // 容器点击事件预留（可以用于选中图表等）
}
</script>

<style lang="scss" scoped>
.chart-container {
  height: 100%;
  background-color: #fff;
  border-radius: 4px;
  border: 2px solid transparent;
  transition: all 0.3s ease;
  cursor: default;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  &.is-editing {
    cursor: move;
  }

  &:hover {
    border-color: #409eff;
    box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.2);
  }
}

.chart-toolbar {
  padding: 8px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  background-color: #f5f7fa;
}

.chart-content {
  flex: 1;
  padding: 16px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 0; /* 确保flex子元素可以正确收缩 */
}

.chart-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex-shrink: 0; /* 防止标题被压缩 */
}

.chart-description {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  flex-shrink: 0; /* 防止描述被压缩 */
}
</style>
