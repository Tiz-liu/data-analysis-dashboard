<template>
  <div class="chart-config">
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-width="100px"
      @submit.prevent
    >
      <!-- 基础信息 -->
      <el-divider content-position="left">基础信息</el-divider>
      <el-form-item label="图表名称" prop="name">
        <el-input
          v-model="form.name"
          placeholder="请输入图表名称"
          clearable
        />
      </el-form-item>

      <el-form-item label="图表类型" prop="chartType">
        <el-select
          v-model="form.chartType"
          placeholder="请选择图表类型"
          clearable
        >
          <el-option
            v-for="type in chartTypes"
            :key="type.value"
            :label="type.label"
            :value="type.value"
          />
        </el-select>
      </el-form-item>

      <!-- 数据源选择 -->
      <el-divider content-position="left">数据源配置</el-divider>
      <el-form-item label="数据源" prop="dataSourceId">
        <el-select
          v-model="form.dataSourceId"
          placeholder="请选择数据源"
          clearable
          filterable
          @change="handleDataSourceChange"
        >
          <el-option
            v-for="ds in dataSources"
            :key="ds.id"
            :label="ds.name"
            :value="ds.id"
          />
        </el-select>
      </el-form-item>

      <!-- SQL查询配置 -->
      <el-divider content-position="left">SQL查询</el-divider>
      <el-form-item label="SQL语句" prop="sql">
        <el-input
          v-model="form.sql"
          type="textarea"
          :rows="6"
          placeholder="请输入SQL查询语句，例如：SELECT * FROM users LIMIT 100"
        />
      </el-form-item>

      <el-form-item>
        <el-button
          type="primary"
          :loading="previewLoading"
          :disabled="!form.dataSourceId || !form.sql"
          @click="handlePreview"
        >
          预览数据
        </el-button>
      </el-form-item>

      <!-- 预览结果表格 -->
      <div v-if="previewData" class="preview-section">
        <el-divider content-position="left">预览结果</el-divider>
        <el-table
          :data="previewData.data"
          border
          stripe
          max-height="300"
          style="width: 100%; margin-bottom: 20px"
        >
          <el-table-column
            v-for="column in previewData.columns"
            :key="column"
            :prop="column"
            :label="column"
            min-width="120"
          />
        </el-table>

        <!-- 维度/指标配置 -->
        <el-divider content-position="left">图表配置</el-divider>
        <el-form-item label="维度（X轴）" prop="dimension">
          <el-select
            v-model="form.dimension"
            placeholder="请选择维度字段"
            clearable
          >
            <el-option
              v-for="column in previewData.columns"
              :key="column"
              :label="column"
              :value="column"
            />
          </el-select>
          <span class="form-tip">用于X轴或分组的字段</span>
        </el-form-item>

        <el-form-item label="指标（Y轴）" prop="measures">
          <el-select
            v-model="form.measures"
            placeholder="请选择指标字段"
            clearable
            multiple
            collapse-tags
          >
            <el-option
              v-for="column in previewData.columns"
              :key="column"
              :label="column"
              :value="column"
            />
          </el-select>
          <span class="form-tip">用于Y轴或数值的字段，可多选</span>
        </el-form-item>
      </div>

      <!-- 操作按钮 -->
      <el-form-item style="margin-top: 30px">
        <el-space>
          <el-button @click="handleCancel">取消</el-button>
          <el-button
            type="primary"
            :disabled="!previewData"
            @click="handleSave"
          >
            保存
          </el-button>
        </el-space>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { dataSourceApi } from '@/api/datasource'
import type { DataSource } from '@/types'
import type { Chart, ChartType, QueryConfig, ChartConfigObject } from '@/types/chart'

/**
 * 组件Props
 */
interface Props {
  chart?: Chart
  dashboardId: number
}

const props = withDefaults(defineProps<Props>(), {
  chart: undefined
})

/**
 * 组件Emits
 */
interface Emits {
  (e: 'save', chart: Chart): void
  (e: 'cancel'): void
}

const emit = defineEmits<Emits>()

/**
 * 图表类型选项
 */
const chartTypes = [
  { label: '折线图', value: 'LINE' },
  { label: '柱状图', value: 'BAR' },
  { label: '饼图', value: 'PIE' },
  { label: '表格', value: 'TABLE' },
  { label: '仪表盘', value: 'GAUGE' }
]

/**
 * 表单数据
 */
interface FormData {
  name: string
  chartType: ChartType
  dataSourceId: number | null
  sql: string
  dimension: string
  measures: string[]
}

const form = reactive<FormData>({
  name: '',
  chartType: 'LINE',
  dataSourceId: null,
  sql: '',
  dimension: '',
  measures: []
})

/**
 * 表单验证规则
 */
const rules: FormRules<FormData> = {
  name: [
    { required: true, message: '请输入图表名称', trigger: 'blur' }
  ],
  chartType: [
    { required: true, message: '请选择图表类型', trigger: 'change' }
  ],
  dataSourceId: [
    { required: true, message: '请选择数据源', trigger: 'change' }
  ],
  sql: [
    { required: true, message: '请输入SQL查询语句', trigger: 'blur' }
  ],
  dimension: [
    { required: true, message: '请选择维度字段', trigger: 'change' }
  ],
  measures: [
    {
      required: true,
      message: '请选择至少一个指标字段',
      trigger: 'change',
      validator: (_rule, value, callback) => {
        if (!value || value.length === 0) {
          callback(new Error('请选择至少一个指标字段'))
        } else {
          callback()
        }
      }
    }
  ]
}

const formRef = ref<FormInstance>()

/**
 * 数据源列表
 */
const dataSources = ref<DataSource[]>([])

/**
 * 预览数据
 */
interface PreviewData {
  columns: string[]
  data: Record<string, any>[]
}

const previewData = ref<PreviewData | null>(null)
const previewLoading = ref(false)

/**
 * 初始化 - 加载数据源列表
 */
onMounted(async () => {
  await loadDataSources()
  // 如果是编辑模式，填充表单数据
  if (props.chart) {
    initFormData(props.chart)
  }
})

/**
 * 监听chart变化，用于编辑模式
 */
watch(
  () => props.chart,
  (newChart) => {
    if (newChart) {
      initFormData(newChart)
    }
  },
  { immediate: true }
)

/**
 * 加载数据源列表
 */
async function loadDataSources() {
  try {
    const response = await dataSourceApi.getList()
    dataSources.value = response || []
  } catch (error) {
    console.error('加载数据源列表失败:', error)
    ElMessage.error('加载数据源列表失败')
  }
}

/**
 * 数据源变化时清空预览数据
 */
function handleDataSourceChange() {
  previewData.value = null
  form.dimension = ''
  form.measures = []
}

/**
 * 初始化表单数据（编辑模式）
 */
function initFormData(chart: Chart) {
  form.name = chart.name
  form.chartType = chart.chartType
  form.dataSourceId = chart.dataSourceId

  try {
    // 安全解析queryConfig - 处理 undefined/null/"undefined"
    if (chart.queryConfig && chart.queryConfig !== 'undefined') {
      const queryConfig: QueryConfig = JSON.parse(chart.queryConfig)
      form.sql = queryConfig.sql || ''
    } else {
      form.sql = ''
    }

    // 安全解析chartConfig - 处理 undefined/null/"undefined"
    if (chart.chartConfig && chart.chartConfig !== 'undefined') {
      const chartConfig: ChartConfigObject = JSON.parse(chart.chartConfig)
      form.dimension = chartConfig.dimensions?.[0] || ''
      form.measures = chartConfig.measures || []
    } else {
      form.dimension = ''
      form.measures = []
    }
  } catch (error) {
    console.error('解析图表配置失败:', error)
    // 解析失败时设置默认值
    form.sql = ''
    form.dimension = ''
    form.measures = []
  }
}

/**
 * 预览数据
 */
async function handlePreview() {
  if (!form.dataSourceId || !form.sql) {
    ElMessage.warning('请先选择数据源并输入SQL语句')
    return
  }

  previewLoading.value = true
  try {
    const response = await dataSourceApi.previewData(
      form.dataSourceId.toString(),
      form.sql
    )

    if (response && response.success !== false) {
      // 响应拦截器已解包，直接访问 response.columns 和 response.rows
      previewData.value = {
        columns: response.columns || [],
        data: response.rows || []
      }

      // 清空之前的维度和指标选择
      form.dimension = ''
      form.measures = []

      ElMessage.success('预览成功，请配置维度和指标')
    } else {
      ElMessage.error(response?.message || '预览失败')
    }
  } catch (error: any) {
    console.error('预览数据失败:', error)
    ElMessage.error(error.message || '预览数据失败')
  } finally {
    previewLoading.value = false
  }
}

/**
 * 保存图表
 */
async function handleSave() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    if (!previewData.value) {
      ElMessage.warning('请先预览数据并配置维度和指标')
      return
    }

    // 构建QueryConfig
    const queryConfig: QueryConfig = {
      sql: form.sql
    }

    // 构建ChartConfigObject
    const chartConfigObject: ChartConfigObject = {
      title: form.name,
      dimensions: [form.dimension],
      measures: form.measures
    }

    // 构建Chart对象
    const chart: Chart = {
      id: props.chart?.id,
      guid: props.chart?.guid,
      dashboardId: props.dashboardId,
      name: form.name,
      chartType: form.chartType,
      dataSourceId: form.dataSourceId!,
      queryConfig: JSON.stringify(queryConfig),
      chartConfig: JSON.stringify(chartConfigObject),
      positionX: props.chart?.positionX,
      positionY: props.chart?.positionY,
      width: props.chart?.width,
      height: props.chart?.height
    }

    emit('save', chart)
  } catch (error) {
    console.error('表单验证失败:', error)
    ElMessage.error('请检查表单填写是否完整')
  }
}

/**
 * 取消
 */
function handleCancel() {
  emit('cancel')
}
</script>

<style scoped>
.chart-config {
  padding: 20px;
}

.preview-section {
  margin-top: 20px;
}

.form-tip {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}

:deep(.el-divider__text) {
  font-weight: 500;
  color: #303133;
}

:deep(.el-form-item__label) {
  font-weight: 400;
}
</style>
