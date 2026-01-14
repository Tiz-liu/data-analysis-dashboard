<template>
  <div class="chart-card">
    <div class="chart-header">
      <el-skeleton animated>
        <template #template>
          <el-skeleton-item variant="h3" style="width: 50%; height: 24px;" />
        </template>
      </el-skeleton>
      <el-skeleton animated>
        <template #template>
          <el-skeleton-item variant="text" style="width: 30%; height: 20px;" />
        </template>
      </el-skeleton>
    </div>
    <div class="chart-body">
      <el-skeleton animated>
        <template #template>
          <el-skeleton-item
            variant="image"
            style="width: 100%; height: 100%;"
          />
        </template>
      </el-skeleton>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  width?: number
  height?: number
}

withDefaults(defineProps<Props>(), {
  width: 6,
  height: 4
})
</script>

<style scoped lang="scss">
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
  }

  .chart-body {
    flex: 1;
    min-height: 200px;
  }
}
</style>
