<template>
  <div class="chart-skeleton" :style="{ height: height }">
    <el-skeleton animated>
      <template #template>
        <div class="skeleton-header">
          <el-skeleton-item variant="h3" style="width: 50%" />
        </div>
        <div class="skeleton-body">
          <el-skeleton-item variant="image" style="width: 100%; height: 100%" />
        </div>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup lang="ts">
interface Props {
  height?: string
}

withDefaults(defineProps<Props>(), {
  height: '300px'
})
</script>

<style scoped lang="scss">
.chart-skeleton {
  width: 100%;
  background: #fff;
  border-radius: 4px;
  padding: 16px;

  .skeleton-header {
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #e8e8e8;
  }

  .skeleton-body {
    height: calc(100% - 60px);
  }
}
</style>
