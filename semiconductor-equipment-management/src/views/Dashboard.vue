<template>
  <div class="space-y-6">
    <!-- 페이지 헤더 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">대시보드</h1>
        <p class="mt-1 text-sm text-gray-500">반도체 설비 현황 및 예측 분석</p>
      </div>
      <div class="mt-4 sm:mt-0">
        <span class="text-sm text-gray-500">마지막 업데이트: {{ lastUpdate }}</span>
      </div>
    </div>

    <!-- 주요 지표 카드 -->
    <div class="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
      <div v-for="stat in stats" :key="stat.name" class="card">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <component :is="stat.icon" class="h-8 w-8" :class="stat.iconColor" />
          </div>
          <div class="ml-5 w-0 flex-1">
            <dl>
              <dt class="text-sm font-medium text-gray-500 truncate">{{ stat.name }}</dt>
              <dd class="flex items-baseline">
                <div class="text-2xl font-semibold text-gray-900">{{ stat.value }}</div>
                <div class="ml-2 flex items-baseline text-sm font-semibold" :class="stat.changeColor">
                  <component :is="stat.changeIcon" class="self-center flex-shrink-0 h-4 w-4" />
                  <span class="sr-only">{{ stat.change > 0 ? '증가' : '감소' }}</span>
                  {{ Math.abs(stat.change) }}%
                </div>
              </dd>
            </dl>
          </div>
        </div>
      </div>
    </div>

    <!-- 차트 섹션 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 설비 상태 분포 -->
      <div class="card">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-medium text-gray-900">설비 상태 분포</h3>
          <select class="text-sm border-gray-300 rounded-md">
            <option>전체 라인</option>
            <option>라인 A</option>
            <option>라인 B</option>
          </select>
        </div>
        <div class="h-64">
          <canvas ref="statusChart"></canvas>
        </div>
      </div>

      <!-- 예측 알림 트렌드 -->
      <div class="card">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-medium text-gray-900">예측 알림 트렌드</h3>
          <select class="text-sm border-gray-300 rounded-md">
            <option>최근 7일</option>
            <option>최근 30일</option>
            <option>최근 90일</option>
          </select>
        </div>
        <div class="h-64">
          <canvas ref="trendChart"></canvas>
        </div>
      </div>
    </div>

    <!-- 실시간 알림 및 설비 목록 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 실시간 알림 -->
      <div class="lg:col-span-1">
        <div class="card">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-medium text-gray-900">실시간 알림</h3>
            <router-link to="/alerts" class="text-sm text-primary-600 hover:text-primary-500">
              전체 보기
            </router-link>
          </div>
          <div class="space-y-3">
            <div v-for="alert in recentAlerts" :key="alert.id" class="flex items-start space-x-3 p-3 rounded-lg" :class="getAlertBgColor(alert.severity)">
              <component :is="getAlertIcon(alert.severity)" class="h-5 w-5 mt-0.5" :class="getAlertIconColor(alert.severity)" />
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-900">{{ alert.equipment }}</p>
                <p class="text-sm text-gray-600">{{ alert.message }}</p>
                <p class="text-xs text-gray-500 mt-1">{{ formatTime(alert.timestamp) }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 주요 설비 현황 -->
      <div class="lg:col-span-2">
        <div class="card">
          <div class="flex items-center justify-between mb-4">
            <h3 class="text-lg font-medium text-gray-900">주요 설비 현황</h3>
            <router-link to="/equipment" class="text-sm text-primary-600 hover:text-primary-500">
              전체 보기
            </router-link>
          </div>
          <div class="overflow-hidden">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">설비명</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">상태</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">가동률</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">예측</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">마지막 점검</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="equipment in keyEquipment" :key="equipment.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap">
                    <div class="flex items-center">
                      <div class="text-sm font-medium text-gray-900">{{ equipment.name }}</div>
                    </div>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span class="status-badge" :class="getStatusClass(equipment.status)">
                      {{ getStatusText(equipment.status) }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                    {{ equipment.efficiency }}%
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span class="status-badge" :class="getPredictionClass(equipment.prediction)">
                      {{ getPredictionText(equipment.prediction) }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    {{ formatDate(equipment.lastMaintenance) }}
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { format } from 'date-fns'
import { ko } from 'date-fns/locale'
import {
  CpuChipIcon,
  ExclamationTriangleIcon,
  CheckCircleIcon,
  XCircleIcon,
  ArrowUpIcon,
  ArrowDownIcon,
  ClockIcon,
  ShieldCheckIcon
} from '@heroicons/vue/24/outline'
import Chart from 'chart.js/auto'

// 차트 참조
const statusChart = ref(null)
const trendChart = ref(null)

// 데이터
const lastUpdate = ref(format(new Date(), 'yyyy년 MM월 dd일 HH:mm', { locale: ko }))

const stats = ref([
  {
    name: '총 설비 수',
    value: '24',
    change: 0,
    changeIcon: ArrowUpIcon,
    changeColor: 'text-gray-500',
    icon: CpuChipIcon,
    iconColor: 'text-primary-600'
  },
  {
    name: '정상 가동',
    value: '20',
    change: 5,
    changeIcon: ArrowUpIcon,
    changeColor: 'text-success-600',
    icon: CheckCircleIcon,
    iconColor: 'text-success-600'
  },
  {
    name: '경고 상태',
    value: '3',
    change: -12,
    changeIcon: ArrowDownIcon,
    changeColor: 'text-success-600',
    icon: ExclamationTriangleIcon,
    iconColor: 'text-warning-600'
  },
  {
    name: '점검 필요',
    value: '1',
    change: -25,
    changeIcon: ArrowDownIcon,
    changeColor: 'text-success-600',
    icon: XCircleIcon,
    iconColor: 'text-danger-600'
  }
])

const recentAlerts = ref([
  {
    id: 1,
    equipment: 'CVD-001',
    message: '온도 센서 이상 감지',
    severity: 'critical',
    timestamp: new Date(Date.now() - 5 * 60 * 1000)
  },
  {
    id: 2,
    equipment: 'ETCH-003',
    message: '압력 수치 임계값 근접',
    severity: 'warning',
    timestamp: new Date(Date.now() - 15 * 60 * 1000)
  },
  {
    id: 3,
    equipment: 'CMP-002',
    message: '예방 정비 일정 알림',
    severity: 'info',
    timestamp: new Date(Date.now() - 30 * 60 * 1000)
  }
])

const keyEquipment = ref([
  {
    id: 1,
    name: 'CVD-001',
    status: 'critical',
    efficiency: 85,
    prediction: 'high_risk',
    lastMaintenance: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000)
  },
  {
    id: 2,
    name: 'ETCH-003',
    status: 'warning',
    efficiency: 92,
    prediction: 'medium_risk',
    lastMaintenance: new Date(Date.now() - 14 * 24 * 60 * 60 * 1000)
  },
  {
    id: 3,
    name: 'CMP-002',
    status: 'normal',
    efficiency: 98,
    prediction: 'low_risk',
    lastMaintenance: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000)
  },
  {
    id: 4,
    name: 'LITHO-001',
    status: 'normal',
    efficiency: 96,
    prediction: 'low_risk',
    lastMaintenance: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000)
  }
])

// 헬퍼 함수들
const getStatusClass = (status) => {
  const classes = {
    normal: 'status-normal',
    warning: 'status-warning',
    critical: 'status-critical',
    maintenance: 'status-maintenance'
  }
  return classes[status] || 'status-normal'
}

const getStatusText = (status) => {
  const texts = {
    normal: '정상',
    warning: '경고',
    critical: '위험',
    maintenance: '점검중'
  }
  return texts[status] || '정상'
}

const getPredictionClass = (prediction) => {
  const classes = {
    low_risk: 'status-normal',
    medium_risk: 'status-warning',
    high_risk: 'status-critical'
  }
  return classes[prediction] || 'status-normal'
}

const getPredictionText = (prediction) => {
  const texts = {
    low_risk: '안전',
    medium_risk: '주의',
    high_risk: '위험'
  }
  return texts[prediction] || '안전'
}

const getAlertIcon = (severity) => {
  const icons = {
    critical: XCircleIcon,
    warning: ExclamationTriangleIcon,
    info: ClockIcon
  }
  return icons[severity] || ClockIcon
}

const getAlertIconColor = (severity) => {
  const colors = {
    critical: 'text-danger-500',
    warning: 'text-warning-500',
    info: 'text-primary-500'
  }
  return colors[severity] || 'text-primary-500'
}

const getAlertBgColor = (severity) => {
  const colors = {
    critical: 'bg-danger-50',
    warning: 'bg-warning-50',
    info: 'bg-primary-50'
  }
  return colors[severity] || 'bg-primary-50'
}

const formatTime = (date) => {
  return format(date, 'HH:mm', { locale: ko })
}

const formatDate = (date) => {
  return format(date, 'MM/dd', { locale: ko })
}

// 차트 초기화
onMounted(() => {
  // 설비 상태 분포 차트
  new Chart(statusChart.value, {
    type: 'doughnut',
    data: {
      labels: ['정상', '경고', '위험', '점검중'],
      datasets: [{
        data: [20, 3, 1, 0],
        backgroundColor: [
          '#22c55e',
          '#f59e0b',
          '#ef4444',
          '#6b7280'
        ],
        borderWidth: 0
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'bottom'
        }
      }
    }
  })

  // 예측 알림 트렌드 차트
  new Chart(trendChart.value, {
    type: 'line',
    data: {
      labels: ['월', '화', '수', '목', '금', '토', '일'],
      datasets: [
        {
          label: '위험 예측',
          data: [2, 1, 3, 2, 1, 0, 1],
          borderColor: '#ef4444',
          backgroundColor: 'rgba(239, 68, 68, 0.1)',
          tension: 0.4
        },
        {
          label: '경고 예측',
          data: [5, 3, 4, 6, 4, 2, 3],
          borderColor: '#f59e0b',
          backgroundColor: 'rgba(245, 158, 11, 0.1)',
          tension: 0.4
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'bottom'
        }
      },
      scales: {
        y: {
          beginAtZero: true
        }
      }
    }
  })
})
</script>
