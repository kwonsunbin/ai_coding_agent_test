<template>
  <div class="space-y-6">
    <!-- 페이지 헤더 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div class="flex items-center space-x-4">
        <button @click="$router.go(-1)" class="p-2 text-gray-400 hover:text-gray-600">
          <ArrowLeftIcon class="w-5 h-5" />
        </button>
        <div>
          <h1 class="text-2xl font-bold text-gray-900">{{ equipment?.name }}</h1>
          <p class="mt-1 text-sm text-gray-500">{{ equipment?.type }} | 라인 {{ equipment?.line }}</p>
        </div>
      </div>
      <div class="mt-4 sm:mt-0 flex space-x-3">
        <button class="btn-secondary">
          <WrenchScrewdriverIcon class="w-4 h-4 mr-2" />
          정비 예약
        </button>
        <button class="btn-primary">
          <Cog6ToothIcon class="w-4 h-4 mr-2" />
          설정
        </button>
      </div>
    </div>

    <!-- 상태 개요 카드 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
      <div class="card">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <div class="h-3 w-3 rounded-full" :class="getStatusDotColor(equipment?.status)"></div>
          </div>
          <div class="ml-3">
            <p class="text-sm font-medium text-gray-500">현재 상태</p>
            <p class="text-lg font-semibold text-gray-900">{{ getStatusText(equipment?.status) }}</p>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <ChartBarIcon class="h-6 w-6 text-primary-600" />
          </div>
          <div class="ml-3">
            <p class="text-sm font-medium text-gray-500">가동률</p>
            <p class="text-lg font-semibold text-gray-900">{{ equipment?.efficiency }}%</p>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <ExclamationTriangleIcon class="h-6 w-6" :class="getPredictionIconColor(equipment?.prediction)" />
          </div>
          <div class="ml-3">
            <p class="text-sm font-medium text-gray-500">예측 위험도</p>
            <p class="text-lg font-semibold text-gray-900">{{ getPredictionText(equipment?.prediction) }}</p>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <ClockIcon class="h-6 w-6 text-gray-600" />
          </div>
          <div class="ml-3">
            <p class="text-sm font-medium text-gray-500">마지막 점검</p>
            <p class="text-lg font-semibold text-gray-900">{{ formatDate(equipment?.lastMaintenance) }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 센서 데이터 및 차트 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 실시간 센서 데이터 -->
      <div class="card">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-medium text-gray-900">실시간 센서 데이터</h3>
          <div class="flex items-center space-x-2">
            <div class="h-2 w-2 bg-green-400 rounded-full animate-pulse"></div>
            <span class="text-sm text-gray-500">실시간</span>
          </div>
        </div>
        <div class="space-y-4">
          <div v-for="sensor in sensorData" :key="sensor.name" class="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
            <div class="flex items-center space-x-3">
              <component :is="sensor.icon" class="h-5 w-5 text-gray-600" />
              <div>
                <p class="text-sm font-medium text-gray-900">{{ sensor.name }}</p>
                <p class="text-xs text-gray-500">{{ sensor.unit }}</p>
              </div>
            </div>
            <div class="text-right">
              <p class="text-lg font-semibold" :class="getSensorValueColor(sensor.status)">
                {{ sensor.value }}
              </p>
              <p class="text-xs text-gray-500">{{ sensor.range }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 성능 트렌드 차트 -->
      <div class="card">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-medium text-gray-900">성능 트렌드</h3>
          <select v-model="chartPeriod" class="text-sm border-gray-300 rounded-md">
            <option value="24h">최근 24시간</option>
            <option value="7d">최근 7일</option>
            <option value="30d">최근 30일</option>
          </select>
        </div>
        <div class="h-64">
          <canvas ref="performanceChart"></canvas>
        </div>
      </div>
    </div>

    <!-- AI 예측 분석 -->
    <div class="card">
      <div class="flex items-center justify-between mb-4">
        <h3 class="text-lg font-medium text-gray-900">AI 예측 분석</h3>
        <span class="text-sm text-gray-500">마지막 분석: {{ formatDateTime(lastAnalysis) }}</span>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div class="bg-gradient-to-r from-danger-50 to-danger-100 p-4 rounded-lg border border-danger-200">
          <div class="flex items-center space-x-3">
            <ExclamationTriangleIcon class="h-6 w-6 text-danger-600" />
            <div>
              <p class="text-sm font-medium text-danger-800">고장 예측</p>
              <p class="text-lg font-bold text-danger-900">72시간 내</p>
            </div>
          </div>
          <p class="text-sm text-danger-700 mt-2">온도 센서 이상 패턴 감지</p>
        </div>
        <div class="bg-gradient-to-r from-warning-50 to-warning-100 p-4 rounded-lg border border-warning-200">
          <div class="flex items-center space-x-3">
            <WrenchScrewdriverIcon class="h-6 w-6 text-warning-600" />
            <div>
              <p class="text-sm font-medium text-warning-800">정비 권장</p>
              <p class="text-lg font-bold text-warning-900">5일 내</p>
            </div>
          </div>
          <p class="text-sm text-warning-700 mt-2">압력 밸브 교체 필요</p>
        </div>
        <div class="bg-gradient-to-r from-primary-50 to-primary-100 p-4 rounded-lg border border-primary-200">
          <div class="flex items-center space-x-3">
            <ChartBarIcon class="h-6 w-6 text-primary-600" />
            <div>
              <p class="text-sm font-medium text-primary-800">효율성 개선</p>
              <p class="text-lg font-bold text-primary-900">+3.2%</p>
            </div>
          </div>
          <p class="text-sm text-primary-700 mt-2">공정 파라미터 최적화 가능</p>
        </div>
      </div>
    </div>

    <!-- 최근 이벤트 및 정비 이력 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 최근 이벤트 -->
      <div class="card">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-medium text-gray-900">최근 이벤트</h3>
          <router-link to="/alerts" class="text-sm text-primary-600 hover:text-primary-500">
            전체 보기
          </router-link>
        </div>
        <div class="space-y-3">
          <div v-for="event in recentEvents" :key="event.id" class="flex items-start space-x-3 p-3 rounded-lg" :class="getEventBgColor(event.type)">
            <component :is="getEventIcon(event.type)" class="h-5 w-5 mt-0.5" :class="getEventIconColor(event.type)" />
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-gray-900">{{ event.title }}</p>
              <p class="text-sm text-gray-600">{{ event.description }}</p>
              <p class="text-xs text-gray-500 mt-1">{{ formatDateTime(event.timestamp) }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 정비 이력 -->
      <div class="card">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-medium text-gray-900">정비 이력</h3>
          <button class="text-sm text-primary-600 hover:text-primary-500">
            이력 추가
          </button>
        </div>
        <div class="space-y-3">
          <div v-for="maintenance in maintenanceHistory" :key="maintenance.id" class="border-l-4 border-primary-200 pl-4 py-2">
            <div class="flex items-center justify-between">
              <p class="text-sm font-medium text-gray-900">{{ maintenance.type }}</p>
              <span class="text-xs text-gray-500">{{ formatDate(maintenance.date) }}</span>
            </div>
            <p class="text-sm text-gray-600">{{ maintenance.description }}</p>
            <p class="text-xs text-gray-500">담당자: {{ maintenance.technician }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 설비 사양 -->
    <div class="card">
      <h3 class="text-lg font-medium text-gray-900 mb-4">설비 사양</h3>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div>
          <h4 class="text-sm font-medium text-gray-500 mb-2">기본 정보</h4>
          <dl class="space-y-1">
            <div class="flex justify-between">
              <dt class="text-sm text-gray-600">모델명</dt>
              <dd class="text-sm font-medium text-gray-900">{{ equipment?.model }}</dd>
            </div>
            <div class="flex justify-between">
              <dt class="text-sm text-gray-600">제조사</dt>
              <dd class="text-sm font-medium text-gray-900">{{ equipment?.manufacturer }}</dd>
            </div>
            <div class="flex justify-between">
              <dt class="text-sm text-gray-600">설치일</dt>
              <dd class="text-sm font-medium text-gray-900">{{ formatDate(equipment?.installDate) }}</dd>
            </div>
          </dl>
        </div>
        <div>
          <h4 class="text-sm font-medium text-gray-500 mb-2">운영 정보</h4>
          <dl class="space-y-1">
            <div class="flex justify-between">
              <dt class="text-sm text-gray-600">담당자</dt>
              <dd class="text-sm font-medium text-gray-900">{{ equipment?.manager }}</dd>
            </div>
            <div class="flex justify-between">
              <dt class="text-sm text-gray-600">팀</dt>
              <dd class="text-sm font-medium text-gray-900">{{ equipment?.team }}</dd>
            </div>
            <div class="flex justify-between">
              <dt class="text-sm text-gray-600">연락처</dt>
              <dd class="text-sm font-medium text-gray-900">{{ equipment?.contact }}</dd>
            </div>
          </dl>
        </div>
        <div>
          <h4 class="text-sm font-medium text-gray-500 mb-2">기술 사양</h4>
          <dl class="space-y-1">
            <div class="flex justify-between">
              <dt class="text-sm text-gray-600">최대 온도</dt>
              <dd class="text-sm font-medium text-gray-900">{{ equipment?.maxTemp }}°C</dd>
            </div>
            <div class="flex justify-between">
              <dt class="text-sm text-gray-600">최대 압력</dt>
              <dd class="text-sm font-medium text-gray-900">{{ equipment?.maxPressure }} bar</dd>
            </div>
            <div class="flex justify-between">
              <dt class="text-sm text-gray-600">전력 소비</dt>
              <dd class="text-sm font-medium text-gray-900">{{ equipment?.powerConsumption }} kW</dd>
            </div>
          </dl>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { format } from 'date-fns'
import { ko } from 'date-fns/locale'
import {
  ArrowLeftIcon,
  ChartBarIcon,
  ClockIcon,
  ExclamationTriangleIcon,
  WrenchScrewdriverIcon,
  Cog6ToothIcon,
  FireIcon,
  BeakerIcon,
  BoltIcon,
  CheckCircleIcon,
  XCircleIcon
} from '@heroicons/vue/24/outline'
import Chart from 'chart.js/auto'

const route = useRoute()
const equipmentId = route.params.id

// 차트 참조
const performanceChart = ref(null)
const chartPeriod = ref('24h')
const lastAnalysis = ref(new Date())

// 설비 데이터 (실제로는 API에서 가져올 데이터)
const equipment = ref({
  id: 1,
  name: 'CVD-001',
  type: 'CVD',
  line: 'A',
  status: 'critical',
  efficiency: 85,
  prediction: 'high_risk',
  lastMaintenance: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
  manager: '김철수',
  team: '설비팀 A',
  contact: '02-1234-5678',
  model: 'CVD-2000X',
  manufacturer: 'TechCorp',
  installDate: new Date(2020, 2, 15),
  maxTemp: 800,
  maxPressure: 10,
  powerConsumption: 150
})

// 센서 데이터
const sensorData = ref([
  {
    name: '온도',
    value: '785°C',
    unit: '°C',
    range: '700-800°C',
    status: 'warning',
    icon: FireIcon
  },
  {
    name: '압력',
    value: '8.5 bar',
    unit: 'bar',
    range: '8-10 bar',
    status: 'normal',
    icon: BeakerIcon
  },
  {
    name: '전력',
    value: '142 kW',
    unit: 'kW',
    range: '120-150 kW',
    status: 'normal',
    icon: BoltIcon
  },
  {
    name: '진동',
    value: '0.8 mm/s',
    unit: 'mm/s',
    range: '0-1.0 mm/s',
    status: 'normal',
    icon: ChartBarIcon
  }
])

// 최근 이벤트
const recentEvents = ref([
  {
    id: 1,
    type: 'alert',
    title: '온도 임계값 초과',
    description: '설정 온도 800°C를 초과하여 785°C 기록',
    timestamp: new Date(Date.now() - 30 * 60 * 1000)
  },
  {
    id: 2,
    type: 'maintenance',
    title: '정기 점검 완료',
    description: '월간 정기 점검이 정상적으로 완료되었습니다',
    timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000)
  },
  {
    id: 3,
    type: 'info',
    title: '공정 파라미터 변경',
    description: '온도 설정값이 780°C에서 800°C로 변경됨',
    timestamp: new Date(Date.now() - 4 * 60 * 60 * 1000)
  }
])

// 정비 이력
const maintenanceHistory = ref([
  {
    id: 1,
    type: '예방 정비',
    description: '온도 센서 교정 및 압력 밸브 점검',
    date: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
    technician: '이정비'
  },
  {
    id: 2,
    type: '긴급 수리',
    description: '냉각 시스템 펌프 교체',
    date: new Date(Date.now() - 21 * 24 * 60 * 60 * 1000),
    technician: '박수리'
  },
  {
    id: 3,
    type: '정기 점검',
    description: '전체 시스템 점검 및 청소',
    date: new Date(Date.now() - 35 * 24 * 60 * 60 * 1000),
    technician: '김점검'
  }
])

// 헬퍼 함수들
const getStatusDotColor = (status) => {
  const colors = {
    normal: 'bg-success-400',
    warning: 'bg-warning-400',
    critical: 'bg-danger-400',
    maintenance: 'bg-gray-400'
  }
  return colors[status] || 'bg-success-400'
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

const getPredictionText = (prediction) => {
  const texts = {
    low_risk: '안전',
    medium_risk: '주의',
    high_risk: '위험'
  }
  return texts[prediction] || '안전'
}

const getPredictionIconColor = (prediction) => {
  const colors = {
    low_risk: 'text-success-600',
    medium_risk: 'text-warning-600',
    high_risk: 'text-danger-600'
  }
  return colors[prediction] || 'text-success-600'
}

const getSensorValueColor = (status) => {
  const colors = {
    normal: 'text-success-600',
    warning: 'text-warning-600',
    critical: 'text-danger-600'
  }
  return colors[status] || 'text-success-600'
}

const getEventIcon = (type) => {
  const icons = {
    alert: ExclamationTriangleIcon,
    maintenance: WrenchScrewdriverIcon,
    info: CheckCircleIcon
  }
  return icons[type] || CheckCircleIcon
}

const getEventIconColor = (type) => {
  const colors = {
    alert: 'text-danger-500',
    maintenance: 'text-primary-500',
    info: 'text-success-500'
  }
  return colors[type] || 'text-success-500'
}

const getEventBgColor = (type) => {
  const colors = {
    alert: 'bg-danger-50',
    maintenance: 'bg-primary-50',
    info: 'bg-success-50'
  }
  return colors[type] || 'bg-success-50'
}

const formatDate = (date) => {
  return format(date, 'yyyy.MM.dd', { locale: ko })
}

const formatDateTime = (date) => {
  return format(date, 'MM.dd HH:mm', { locale: ko })
}

// 차트 초기화
onMounted(() => {
  // 성능 트렌드 차트
  new Chart(performanceChart.value, {
    type: 'line',
    data: {
      labels: ['00:00', '04:00', '08:00', '12:00', '16:00', '20:00', '24:00'],
      datasets: [
        {
          label: '가동률 (%)',
          data: [88, 85, 90, 87, 85, 82, 85],
          borderColor: '#3b82f6',
          backgroundColor: 'rgba(59, 130, 246, 0.1)',
          tension: 0.4,
          yAxisID: 'y'
        },
        {
          label: '온도 (°C)',
          data: [780, 785, 775, 790, 785, 795, 785],
          borderColor: '#ef4444',
          backgroundColor: 'rgba(239, 68, 68, 0.1)',
          tension: 0.4,
          yAxisID: 'y1'
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
          type: 'linear',
          display: true,
          position: 'left',
          min: 0,
          max: 100
        },
        y1: {
          type: 'linear',
          display: true,
          position: 'right',
          min: 700,
          max: 800,
          grid: {
            drawOnChartArea: false,
          },
        }
      }
    }
  })
})
</script>
