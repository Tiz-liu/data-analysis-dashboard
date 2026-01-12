// 通用类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 仪表板相关类型
export interface Dashboard {
  id: string
  guid: string
  name: string
  description?: string
  charts: Chart[]
  createdAt: string
  updatedAt: string
  createdBy: string
}

export interface Chart {
  id: string
  guid: string
  dashboardId: string
  name: string
  type: 'line' | 'bar' | 'pie' | 'table' | 'gauge'
  config: ChartConfig
  dataSourceId: string
  position: {
    x: number
    y: number
    w: number
    h: number
  }
}

export interface ChartConfig {
  title?: string
  xAxis?: string
  yAxis?: string[]
  series?: SeriesConfig[]
  options?: Record<string, any>
}

export interface SeriesConfig {
  name: string
  type: string
  data: any[]
  [key: string]: any
}

// 数据源相关类型
export interface DataSource {
  id: string
  guid: string
  name: string
  type: 'mysql' | 'excel' | 'api'
  config: DataSourceConfig
  status: 'connected' | 'disconnected' | 'error'
  createdAt: string
  updatedAt: string
}

export interface DataSourceConfig {
  host?: string
  port?: number
  database?: string
  username?: string
  password?: string
  filePath?: string
  [key: string]: any
}

// 任务相关类型
export interface Task {
  id: string
  taskId: string
  taskType: 'EXPORT' | 'IMPORT' | 'DATA_REFRESH'
  status: 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED' | 'CANCELLED'
  progress: number
  totalSteps: number
  currentStep: number
  errorMessage?: string
  createdAt: string
  completedAt?: string
  resultData?: any
}

export interface ExportParam {
  dashboardIds: string[]
  taskId: string
}

export interface ImportParam {
  file: File
  strategy: 'AUTO_RENAME' | 'SKIP' | 'OVERRIDE' | 'USE_EXISTING'
  taskId: string
}

// AI分析相关类型
export interface AiAnalysisRequest {
  dashboardId: string
  analysisType?: 'trend' | 'anomaly' | 'correlation' | 'full'
}

export interface AiAnalysisResult {
  summary: string
  trends: TrendAnalysis[]
  anomalies: AnomalyDetection[]
  correlations: CorrelationAnalysis[]
  recommendations: string[]
  report?: string
}

export interface TrendAnalysis {
  field: string
  direction: 'up' | 'down' | 'stable'
  changeRate: number
  description: string
}

export interface AnomalyDetection {
  field: string
  value: any
  timestamp: string
  description: string
  severity: 'low' | 'medium' | 'high'
}

export interface CorrelationAnalysis {
  field1: string
  field2: string
  coefficient: number
  description: string
}
