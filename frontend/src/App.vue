<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<template>
  <el-container class="app-container">
    <el-aside width="200px">
      <div class="logo">BI 智能数据分析面板</div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#545c64"
        text-color="#fff"
        active-text-color="#ffd04b"
      >
        <el-menu-item index="/dashboard">
          <el-icon><icon-menu /></el-icon>
          <span>仪表板</span>
        </el-menu-item>
        <el-menu-item index="/data-source">
          <el-icon><icon-connection /></el-icon>
          <span>数据源</span>
        </el-menu-item>
        <el-menu-item index="/task">
          <el-icon><icon-list /></el-icon>
          <span>任务管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-main>
      <RouterView />
    </el-main>
  </el-container>
</template>

<style scoped>
.app-container {
  height: 100vh;
}

.el-aside {
  background-color: #545c64;
}

.logo {
  color: #fff;
  text-align: center;
  line-height: 60px;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #434a50;
}

.el-main {
  padding: 20px;
  background-color: #f0f2f5;
}
</style>
