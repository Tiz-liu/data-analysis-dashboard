<script setup lang="ts">
import { ref } from 'vue'

const dataSources = ref([
  {
    id: '1',
    name: 'MySQL 数据库',
    type: 'mysql',
    host: 'localhost',
    port: 3306,
    database: 'bi_dashboard',
    status: 'connected'
  }
])

const dialogVisible = ref(false)

const addDataSource = () => {
  dialogVisible.value = true
}
</script>

<template>
  <div class="data-source">
    <div class="header">
      <h2>数据源管理</h2>
      <el-button type="primary" @click="addDataSource">
        <el-icon><icon-plus /></el-icon>
        添加数据源
      </el-button>
    </div>

    <el-table :data="dataSources" border>
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="type" label="类型">
        <template #default="{ row }">
          <el-tag>{{ row.type.toUpperCase() }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="host" label="主机" />
      <el-table-column prop="port" label="端口" />
      <el-table-column prop="database" label="数据库" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 'connected' ? 'success' : 'danger'">
            {{ row.status === 'connected' ? '已连接' : '未连接' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default>
          <el-button link type="primary">测试连接</el-button>
          <el-button link type="primary">编辑</el-button>
          <el-button link type="danger">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="添加数据源" width="600px">
      <el-form label-width="100px">
        <el-form-item label="数据源名称">
          <el-input placeholder="请输入数据源名称" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select placeholder="请选择类型">
            <el-option label="MySQL" value="mysql" />
            <el-option label="Excel" value="excel" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.data-source {
  height: 100%;
  display: flex;
  flex-direction: column;
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
