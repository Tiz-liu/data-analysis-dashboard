import axios from 'axios'
import { ElMessage } from 'element-plus'

// Create axios instance
const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// Request interceptor
request.interceptors.request.use(
  (config) => {
    // Add token if exists
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    console.error('Request error:', error)
    return Promise.reject(error)
  }
)

// Response interceptor
request.interceptors.response.use(
  (response) => {
    const res = response.data

    // Check if code indicates success
    if (res.code === 200) {
      return res.data  // 解包，直接返回 data 字段
    } else {
      ElMessage.error(res.message || 'Request failed')
      return Promise.reject(new Error(res.message || 'Request failed'))
    }
  },
  (error) => {
    console.error('Response error:', error)

    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case 401:
          ElMessage.error('Unauthorized, please login')
          localStorage.removeItem('token')
          window.location.href = '/login'
          break
        case 403:
          ElMessage.error('Forbidden')
          break
        case 404:
          ElMessage.error('Resource not found')
          break
        case 500:
          ElMessage.error(data.message || 'Server error')
          break
        default:
          ElMessage.error(data.message || `Request failed with status ${status}`)
      }
    } else {
      ElMessage.error('Network error, please check your connection')
    }

    return Promise.reject(error)
  }
)

export default request
