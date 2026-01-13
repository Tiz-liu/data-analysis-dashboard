import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    children: [
      {
        path: '',
        name: 'DashboardList',
        component: () => import('../views/dashboard/DashboardList.vue')
      },
      {
        path: ':id',
        name: 'DashboardView',
        component: () => import('../views/dashboard/DashboardView.vue')
      },
      {
        path: ':id/edit',
        name: 'DashboardEdit',
        component: () => import('../views/dashboard/DashboardEditor.vue')
      }
    ]
  },
  {
    path: '/data-source',
    name: 'DataSource',
    component: () => import('../views/DataSource.vue')
  },
  {
    path: '/task',
    name: 'Task',
    component: () => import('../views/Task.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
