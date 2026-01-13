/**
 * 统一的图表类型定义
 */

/**
 * 图表类型枚举
 */
export type ChartType = 'LINE' | 'BAR' | 'PIE' | 'TABLE' | 'GAUGE' | 'SCATTER' | 'AREA'

/**
 * 图表实体
 */
export interface Chart {
  id?: number
  guid?: string
  dashboardId?: number
  name: string
  description?: string
  chartType: ChartType
  dataSourceId: number
  queryConfig: string  // JSON字符串 - SQL查询配置
  chartConfig: string  // JSON字符串 - 图表配置
  positionX?: number
  positionY?: number
  width?: number
  height?: number
}

/**
 * GridLayout布局项
 */
export interface GridLayoutItem {
  i: string  // 唯一标识（chart.id）
  x: number
  y: number
  w: number
  h: number
  minW?: number
  minH?: number
  maxW?: number
  maxH?: number
}

/**
 * 查询配置（解析后的对象）
 */
export interface QueryConfig {
  sql: string
  params?: Record<string, any>
}

/**
 * 图表配置（解析后的对象）
 */
export interface ChartConfigObject {
  title?: string
  dimensions?: string[]  // 维度（X轴/分组）
  measures?: string[]    // 指标（Y轴/数值）
  options?: Record<string, any>  // ECharts配置选项
  style?: {
    color?: string[]
    theme?: 'default' | 'dark' | 'vintage'
    [key: string]: any
  }
}
