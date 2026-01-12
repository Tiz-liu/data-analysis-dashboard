<script setup lang="ts">
import { ref } from 'vue'

const tasks = ref([
  {
    id: '1',
    taskId: 'task-001',
    taskType: 'EXPORT',
    status: 'SUCCESS',
    progress: 100,
    createdAt: '2026-01-12 10:00:00',
    completedAt: '2026-01-12 10:05:00'
  },
  {
    id: '2',
    taskId: 'task-002',
    taskType: 'IMPORT',
    status: 'RUNNING',
    progress: 45,
    createdAt: '2026-01-12 10:10:00',
    completedAt: null
  }
])

const exportDialogVisible = ref(false)
const importDialogVisible = ref(false)

const openExportDialog = () => {
  exportDialogVisible.value = true
}

const openImportDialog = () => {
  importDialogVisible.value = true
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    SUCCESS: 'success',
    RUNNING: 'warning',
    FAILED: 'danger',
    PENDING: 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    SUCCESS: '成功',
    RUNNING: '运行中',
    FAILED: '失败',
    PENDING: '等待中'
  }
  return map[status] || status
}
</script>

<template>
  <div class="task">
    <div class="header">
      <h2>任务管理</h2>
      <el-space>
        <el-button type="primary" @click="openExportDialog">
          <el-icon><icon-download /></el-icon>
          导出
        </el-button>
        <el-button type="success" @click="openImportDialog">
          <el-icon><icon-upload /></el-icon>
          导入
        </el-button>
      </el-space>
    </div>

    <el-table :data="tasks" border>
      <el-table-column prop="taskId" label="任务ID" />
      <el-table-column prop="taskType" label="类型">
        <template #default="{ row }">
          <el-tag :type="row.taskType === 'EXPORT' ? 'primary' : 'success'">
            {{ row.taskType === 'EXPORT' ? '导出' : '导入' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="progress" label="进度">
        <template #default="{ row }">
          <el-progress :percentage="row.progress" />
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" />
      <el-table-column prop="completedAt" label="完成时间">
        <template #default="{ row }">
          {{ row.completedAt || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button v-if="row.status === 'RUNNING'" link type="danger">取消</el-button>
          <el-button v-if="row.status === 'FAILED'" link type="primary">重试</el-button>
          <el-button v-if="row.taskType === 'EXPORT' && row.status === 'SUCCESS'" link type="primary">下载</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 导出对话框 -->
    <el-dialog v-model="exportDialogVisible" title="导出仪表板" width="500px">
      <el-form label-width="100px">
        <el-form-item label="选择仪表板">
          <el-select placeholder="请选择仪表板" multiple>
            <el-option label="销售数据分析" value="1" />
            <el-option label="用户行为分析" value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="exportDialogVisible = false">取消</el-button>
        <el-button type="primary">开始导出</el-button>
      </template>
    </el-dialog>

    <!-- 导入对话框 -->
    <el-dialog v-model="importDialogVisible" title="导入仪表板" width="500px">
      <el-form label-width="100px">
        <el-form-item label="选择文件">
          <el-upload
            drag
            action="#"
            :auto-upload="false"
            accept=".zip"
          >
            <el-icon class="el-icon--upload"><icon-upload-filled /></el-icon>
            <div class="el-upload__text">
              拖拽文件到此处或 <em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                仅支持 .zip 格式的导出文件
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="冲突解决">
          <el-radio-group>
            <el-radio label="AUTO_RENAME">自动重命名</el-radio>
            <el-radio label="SKIP">跳过</el-radio>
            <el-radio label="OVERRIDE">覆盖</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="importDialogVisible = false">取消</el-button>
        <el-button type="primary">开始导入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.task {
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
</style>
