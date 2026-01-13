<template>
  <div ref="chartRef" class="echarts-renderer" :style="{ height: height }"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, onBeforeUnmount, computed } from 'vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'
import { getChartData } from '@/api/chart'

interface Props {
  chartId: number
  chartType: string
  height?: string
  initialData?: any  // 预加载的数据
}

const props = withDefaults(defineProps<Props>(), {
  height: '400px'
})

const chartRef = ref<HTMLDivElement>()
let chartInstance: echarts.ECharts | null = null
const loading = ref(false)

// 初始化图表
const initChart = () => {
  if (!chartRef.value) return

  chartInstance = echarts.init(chartRef.value)
  loadChartData()
}

// 加载图表数据
const loadChartData = async () => {
  if (!chartInstance || !props.chartId) return

  // 优先使用预加载数据
  if (props.initialData && props.initialData.rows) {
    const option = generateChartOption(props.initialData)
    chartInstance.setOption(option)
    return
  }

  // 没有预加载数据时才发起请求
  loading.value = true
  try {
    const response = await getChartData(props.chartId) as any

    if (response && response.rows) {
      const option = generateChartOption(response)
      chartInstance.setOption(option)
    }
  } catch (error) {
    console.error('Failed to load chart data:', error)
  } finally {
    loading.value = false
  }
}

// 监听预加载数据变化
watch(() => props.initialData, (newData) => {
  if (newData && chartInstance && newData.rows) {
    const option = generateChartOption(newData)
    chartInstance.setOption(option)
  }
}, { immediate: true })

// 生成 ECharts 配置
const generateChartOption = (data: any): EChartsOption => {
  const { columns = [], rows = [] } = data

  if (rows.length === 0) {
    return {
      title: {
        text: '暂无数据',
        left: 'center',
        top: 'center',
        textStyle: {
          color: '#999'
        }
      }
    }
  }

  // 提取维度和指标
  let dimensionCols: string[] = []
  let measureCols: string[] = []

  if (columns.length > 0 && typeof columns[0] === 'string') {
    // columns 是字符串数组，需要推断维度和指标
    const firstRow = rows[0] || {}

    // 字符串类型的字段作为维度候选
    dimensionCols = columns.filter((col: string) => {
      const value = firstRow[col]
      return typeof value === 'string'
    })

    // 数值类型的字段作为指标候选
    measureCols = columns.filter((col: string) => {
      const value = firstRow[col]
      return typeof value === 'number'
    })

    // 如果没有推断出维度，使用第一列
    if (dimensionCols.length === 0 && columns.length > 0) {
      dimensionCols = [columns[0]]
    }
    // 如果没有推断出指标，查找数值字段
    if (measureCols.length === 0) {
      const numericCol = columns.find((col: string) => {
        const value = firstRow[col]
        return typeof value === 'number' || !isNaN(Number(value))
      })
      if (numericCol) {
        measureCols = [numericCol]
      }
    }
  } else {
    // columns 是对象数组，有 type 字段
    dimensionCols = columns
      .filter((col: any) => col.type === 'dimension' || !col.type)
      .map((col: any) => col.name || col)
    measureCols = columns
      .filter((col: any) => col.type === 'measure')
      .map((col: any) => col.name || col)
  }

  // 使用第一列作为 X 轴数据
  const xAxisData = rows.map((row: any) => row[dimensionCols[0]] || '')

  switch (props.chartType) {
    case 'LINE':
      return {
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: xAxisData
        },
        yAxis: {
          type: 'value'
        },
        series: measureCols.map((colName: string) => ({
          name: colName,
          type: 'line',
          data: rows.map((row: any) => row[colName]),
          smooth: true
        }))
      }

    case 'BAR':
      return {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        xAxis: {
          type: 'category',
          data: xAxisData
        },
        yAxis: {
          type: 'value'
        },
        series: measureCols.map((colName: string) => ({
          name: colName,
          type: 'bar',
          data: rows.map((row: any) => row[colName])
        }))
      }

    case 'PIE':
      return {
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            type: 'pie',
            radius: '50%',
            data: rows.map((row: any) => ({
              value: row[measureCols[0]] || 0,
              name: row[dimensionCols[0]] || 'Unknown'
            })),
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }

    case 'TABLE':
      return {
        title: {
          text: '表格视图'
        },
        tooltip: {},
        xAxis: {
          type: 'category',
          data: columns.map((col: any) => typeof col === 'string' ? col : col.name),
          axisLabel: {
            interval: 0,
            rotate: 45
          }
        },
        yAxis: {
          type: 'category',
          data: rows.map((_: any, index: number) => `Row ${index + 1}`)
        },
        visualMap: {
          orient: 'horizontal',
          left: 'center',
          min: 0,
          max: Math.max(...rows.map((row: any) => Object.values(row).filter((v: any) => typeof v === 'number').map(Number)).flat()),
          text: ['High', 'Low'],
          dimension: 0,
          inRange: {
            color: ['#65B18F', '#F6E0B5', '#D9534F']
          }
        },
        series: [
          {
            type: 'heatmap',
            data: rows.flatMap((row: any, rowIndex: number) =>
              columns.map((col: any, colIndex: number) => {
                const colName = typeof col === 'string' ? col : col.name
                return [
                  colIndex,
                  rowIndex,
                  typeof row[colName] === 'number' ? row[colName] : 0
                ]
              })
            ),
            label: {
              show: true
            },
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }

    default:
      return {
        title: {
          text: '不支持的图表类型'
        }
      }
  }
}

// 监听图表类型变化
watch(() => props.chartType, () => {
  if (chartInstance) {
    loadChartData()
  }
})

// 监听窗口大小变化
const handleResize = () => {
  chartInstance?.resize()
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped>
.echarts-renderer {
  width: 100%;
  min-height: 300px;
}
</style>
