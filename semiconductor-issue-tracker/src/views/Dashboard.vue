<template>
  <div class="space-y-6 animate-fade-in-up">
    <!-- 페이지 헤더 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h1 class="text-2xl font-bold gradient-text">대시보드</h1>
        <p class="mt-1 text-secondary-600">반도체 설비 현황 및 예측 분석</p>
      </div>
      <div class="mt-4 sm:mt-0 text-sm text-secondary-500">
        마지막 업데이트: {{ lastUpdate }}
      </div>
    </div>

    <!-- 주요 지표 카드 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="metric-card floating-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg">
            <CogIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">총 설비 수</p>
            <div class="flex items-baseline">
              <p class="text-2xl font-semibold text-secondary-900">{{ totalEquipment }}</p>
              <p class="ml-2 text-sm text-secondary-500">대</p>
            </div>
          </div>
        </div>
      </div>

      <div class="metric-card floating-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-success-500 to-success-600 rounded-lg">
            <CheckCircleIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">정상 가동</p>
            <div class="flex items-baseline">
              <p class="text-2xl font-semibold text-secondary-900">{{ normalEquipment }}</p>
              <p class="ml-2 text-sm text-success-600 font-medium">↑{{ normalChange }}%</p>
            </div>
          </div>
        </div>
      </div>

      <div class="metric-card floating-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-warning-500 to-warning-600 rounded-lg">
            <ExclamationTriangleIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">경고 상태</p>
            <div class="flex items-baseline">
              <p class="text-2xl font-semibold text-secondary-900">{{ warningEquipment }}</p>
              <p class="ml-2 text-sm text-warning-600 font-medium">↓{{ warningChange }}%</p>
            </div>
          </div>
        </div>
      </div>

      <div class="metric-card floating-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-danger-500 to-danger-600 rounded-lg">
            <XCircleIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">점검 필요</p>
            <div class="flex items-baseline">
              <p class="text-2xl font-semibold text-secondary-900">{{ criticalEquipment }}</p>
              <p class="ml-2 text-sm text-danger-600 font-medium">↓{{ criticalChange }}%</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 차트 및 알림 섹션 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <!-- 설비 상태 분포 차트 -->
      <div class="lg:col-span-2 card">
        <div class="card-header">
          <h3 class="text-lg font-semibold">설비 상태 분포</h3>
        </div>
        <div class="h-64 flex items-center justify-center bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg">
          <div class="text-center">
            <ChartBarIcon class="w-16 h-16 text-blue-400 mx-auto mb-2" />
            <p class="text-blue-600 font-medium">도넛 차트 (Chart.js)</p>
            <p class="text-sm text-blue-500 mt-1">실시간 설비 상태 분포</p>
          </div>
        </div>
      </div>

      <!-- 실시간 알림 -->
      <div class="card">
        <div class="flex items-center justify-between mb-4">
          <h3 class="text-lg font-semibold text-secondary-900">실시간 알림</h3>
          <router-link to="/alerts" class="btn-ghost text-sm">전체 보기</router-link>
        </div>
        <div class="space-y-3">
          <div v-for="alert in recentAlerts" :key="alert.id" class="alert-card" :class="getAlertClass(alert.type)">
            <div class="flex items-start">
              <component :is="getAlertIcon(alert.type)" class="w-5 h-5 mt-0.5 mr-3 flex-shrink-0" />
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium">{{ alert.equipment }}</p>
                <p class="text-sm opacity-90">{{ alert.message }}</p>
                <p class="text-xs opacity-75 mt-1">{{ alert.time }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 예측 알림 트렌드 -->
    <div class="card">
      <div class="card-header">
        <h3 class="text-lg font-semibold">예측 알림 트렌드</h3>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="h-48 flex items-center justify-center bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg">
          <div class="text-center">
            <ChartBarIcon class="w-12 h-12 text-blue-400 mx-auto mb-2" />
            <p class="text-blue-600 font-medium">라인 차트 (Chart.js)</p>
            <p class="text-sm text-blue-500 mt-1">최근 7일 알림 트렌드</p>
          </div>
        </div>
        <div class="space-y-4">
          <div class="flex items-center justify-between p-4 bg-gradient-to-r from-blue-50 to-blue-100 rounded-lg">
            <div>
              <p class="text-sm font-medium text-blue-800">오늘 예측 알림</p>
              <p class="text-2xl font-bold text-blue-900">{{ todayPredictions }}</p>
            </div>
            <ArrowTrendingUpIcon class="w-8 h-8 text-blue-600" />
          </div>
          <div class="flex items-center justify-between p-4 bg-gradient-to-r from-success-50 to-success-100 rounded-lg">
            <div>
              <p class="text-sm font-medium text-success-800">예방 정비 완료</p>
              <p class="text-2xl font-bold text-success-900">{{ completedMaintenance }}</p>
            </div>
            <CheckCircleIcon class="w-8 h-8 text-success-600" />
          </div>
        </div>
      </div>
    </div>

    <!-- 주요 설비 현황 -->
    <div class="card">
      <div class="flex items-center justify-between mb-6">
        <h3 class="text-lg font-semibold text-secondary-900">주요 설비 현황</h3>
        <router-link to="/equipment" class="btn-secondary">전체 보기</router-link>
      </div>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-secondary-200">
          <thead class="table-header">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">설비명</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">상태</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">가동률</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">예측</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">마지막 점검</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">액션</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-secondary-200">
            <tr v-for="equipment in keyEquipment" :key="equipment.id" class="table-row">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="text-sm font-medium text-secondary-900">{{ equipment.name }}</div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="status-badge" :class="getStatusClass(equipment.status)">
                  {{ getStatusText(equipment.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="text-sm text-secondary-900 mr-2">{{ equipment.efficiency }}%</div>
                  <div class="progress-bar w-16">
                    <div class="progress-fill" :class="getProgressClass(equipment.efficiency)" :style="`width: ${equipment.efficiency}%`"></div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="status-badge" :class="getPredictionClass(equipment.prediction)">
                  {{ getPredictionText(equipment.prediction) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-secondary-500">
                {{ equipment.lastMaintenance }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <router-link :to="`/equipment/${equipment.id}`" class="btn-ghost text-sm">
                  상세보기
                </router-link>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- AI 예측 분석 -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="card bg-gradient-to-br from-danger-50 to-danger-100 border-danger-200">
        <div class="flex items-center mb-4">
          <ExclamationTriangleIcon class="w-6 h-6 text-danger-600 mr-2" />
          <h3 class="text-lg font-semibold text-danger-800">고장 예측</h3>
        </div>
        <div class="text-center">
          <p class="text-3xl font-bold text-danger-900 mb-2">72시간 내</p>
          <p class="text-sm text-danger-700">온도 센서 이상 패턴 감지</p>
          <p class="text-xs text-danger-600 mt-2">신뢰도: 87%</p>
        </div>
      </div>

      <div class="card bg-gradient-to-br from-warning-50 to-warning-100 border-warning-200">
        <div class="flex items-center mb-4">
          <CogIcon class="w-6 h-6 text-warning-600 mr-2" />
          <h3 class="text-lg font-semibold text-warning-800">정비 권장</h3>
        </div>
        <div class="text-center">
          <p class="text-3xl font-bold text-warning-900 mb-2">5일 내</p>
          <p class="text-sm text-warning-700">압력 밸브 교체 필요</p>
          <p class="text-xs text-warning-600 mt-2">우선순위: 높음</p>
        </div>
      </div>

      <div class="card bg-gradient-to-br from-success-50 to-success-100 border-success-200">
        <div class="flex items-center mb-4">
          <ArrowTrendingUpIcon class="w-6 h-6 text-success-600 mr-2" />
          <h3 class="text-lg font-semibold text-success-800">효율성 개선</h3>
        </div>
        <div class="text-center">
          <p class="text-3xl font-bold text-success-900 mb-2">+3.2%</p>
          <p class="text-sm text-success-700">공정 파라미터 최적화 가능</p>
          <p class="text-xs text-success-600 mt-2">예상 절약: 15%</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { format } from 'date-fns'
import { ko } from 'date-fns/locale'
import {
  CogIcon,
  CheckCircleIcon,
  ExclamationTriangleIcon,
  XCircleIcon,
  ChartBarIcon,
  ArrowTrendingUpIcon,
  BellIcon
} from '@heroicons/vue/24/outline'

export default {
  name: 'Dashboard',
  components: {
    CogIcon,
    CheckCircleIcon,
    ExclamationTriangleIcon,
    XCircleIcon,
    ChartBarIcon,
    ArrowTrendingUpIcon,
    BellIcon
  },
  setup() {
    const lastUpdate = ref('')
    const totalEquipment = ref(24)
    const normalEquipment = ref(20)
    const warningEquipment = ref(3)
    const criticalEquipment = ref(1)
    const normalChange = ref(5)
    const warningChange = ref(12)
    const criticalChange = ref(25)
    const todayPredictions = ref(8)
    const completedMaintenance = ref(12)

    const recentAlerts = ref([
      {
        id: 1,
        equipment: 'CVD-001',
        message: '온도 센서 이상 감지',
        time: '09:35',
        type: 'critical'
      },
      {
        id: 2,
        equipment: 'ETCH-003',
        message: '압력 수치 임계값 근접',
        time: '09:20',
        type: 'warning'
      },
      {
        id: 3,
        equipment: 'CMP-002',
        message: '예방 정비 일정 알림',
        time: '08:35',
        type: 'info'
      }
    ])

    const keyEquipment = ref([
      {
        id: 'cvd-001',
        name: 'CVD-001',
        status: 'critical',
        efficiency: 85,
        prediction: 'critical',
        lastMaintenance: '05.16'
      },
      {
        id: 'etch-003',
        name: 'ETCH-003',
        status: 'warning',
        efficiency: 92,
        prediction: 'warning',
        lastMaintenance: '05.09'
      },
      {
        id: 'cmp-002',
        name: 'CMP-002',
        status: 'normal',
        efficiency: 98,
        prediction: 'normal',
        lastMaintenance: '05.20'
      },
      {
        id: 'litho-001',
        name: 'LITHO-001',
        status: 'normal',
        efficiency: 96,
        prediction: 'normal',
        lastMaintenance: '05.18'
      }
    ])

    const getAlertClass = (type) => {
      const classes = {
        critical: 'alert-danger',
        warning: 'alert-warning',
        info: 'alert-info',
        success: 'alert-success'
      }
      return classes[type] || 'alert-info'
    }

    const getAlertIcon = (type) => {
      const icons = {
        critical: XCircleIcon,
        warning: ExclamationTriangleIcon,
        info: BellIcon,
        success: CheckCircleIcon
      }
      return icons[type] || BellIcon
    }

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
        maintenance: '정비'
      }
      return texts[status] || '정상'
    }

    const getPredictionClass = (prediction) => {
      const classes = {
        normal: 'status-normal',
        warning: 'status-warning',
        critical: 'status-critical'
      }
      return classes[prediction] || 'status-normal'
    }

    const getPredictionText = (prediction) => {
      const texts = {
        normal: '안전',
        warning: '주의',
        critical: '위험'
      }
      return texts[prediction] || '안전'
    }

    const getProgressClass = (efficiency) => {
      if (efficiency >= 95) return 'progress-success'
      if (efficiency >= 85) return 'progress-warning'
      return 'progress-danger'
    }

    onMounted(() => {
      lastUpdate.value = format(new Date(), 'yyyy. MM. dd HH:mm', { locale: ko })
    })

    return {
      lastUpdate,
      totalEquipment,
      normalEquipment,
      warningEquipment,
      criticalEquipment,
      normalChange,
      warningChange,
      criticalChange,
      todayPredictions,
      completedMaintenance,
      recentAlerts,
      keyEquipment,
      getAlertClass,
      getAlertIcon,
      getStatusClass,
      getStatusText,
      getPredictionClass,
      getPredictionText,
      getProgressClass
    }
  }
}
</script>
