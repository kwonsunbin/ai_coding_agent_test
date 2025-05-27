<template>
  <div class="space-y-6">
    <!-- 페이지 헤더 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">알림 관리</h1>
        <p class="mt-1 text-sm text-gray-500">시스템 알림 및 예측 경고 관리</p>
      </div>
      <div class="mt-4 sm:mt-0 flex space-x-3">
        <button @click="markAllAsRead" class="btn-secondary">
          <CheckIcon class="w-4 h-4 mr-2" />
          모두 읽음 처리
        </button>
        <button class="btn-primary">
          <Cog6ToothIcon class="w-4 h-4 mr-2" />
          알림 설정
        </button>
      </div>
    </div>

    <!-- 알림 통계 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
      <div class="card">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <ExclamationTriangleIcon class="h-8 w-8 text-danger-600" />
          </div>
          <div class="ml-5">
            <p class="text-sm font-medium text-gray-500">긴급 알림</p>
            <p class="text-2xl font-semibold text-gray-900">{{ criticalAlerts }}</p>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <ExclamationCircleIcon class="h-8 w-8 text-warning-600" />
          </div>
          <div class="ml-5">
            <p class="text-sm font-medium text-gray-500">경고 알림</p>
            <p class="text-2xl font-semibold text-gray-900">{{ warningAlerts }}</p>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <InformationCircleIcon class="h-8 w-8 text-primary-600" />
          </div>
          <div class="ml-5">
            <p class="text-sm font-medium text-gray-500">정보 알림</p>
            <p class="text-2xl font-semibold text-gray-900">{{ infoAlerts }}</p>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <EyeSlashIcon class="h-8 w-8 text-gray-600" />
          </div>
          <div class="ml-5">
            <p class="text-sm font-medium text-gray-500">읽지 않음</p>
            <p class="text-2xl font-semibold text-gray-900">{{ unreadAlerts }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 필터 및 검색 -->
    <div class="card">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">검색</label>
          <div class="relative">
            <MagnifyingGlassIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="알림 내용 검색"
              class="pl-10 w-full border-gray-300 rounded-md text-sm"
            />
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">심각도</label>
          <select v-model="severityFilter" class="w-full border-gray-300 rounded-md text-sm">
            <option value="">전체 심각도</option>
            <option value="critical">긴급</option>
            <option value="warning">경고</option>
            <option value="info">정보</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">설비</label>
          <select v-model="equipmentFilter" class="w-full border-gray-300 rounded-md text-sm">
            <option value="">전체 설비</option>
            <option value="CVD-001">CVD-001</option>
            <option value="ETCH-003">ETCH-003</option>
            <option value="CMP-002">CMP-002</option>
            <option value="LITHO-001">LITHO-001</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">상태</label>
          <select v-model="statusFilter" class="w-full border-gray-300 rounded-md text-sm">
            <option value="">전체 상태</option>
            <option value="unread">읽지 않음</option>
            <option value="read">읽음</option>
            <option value="acknowledged">확인됨</option>
            <option value="resolved">해결됨</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 알림 목록 -->
    <div class="card">
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center space-x-4">
          <h3 class="text-lg font-medium text-gray-900">알림 목록</h3>
          <span class="text-sm text-gray-500">총 {{ filteredAlerts.length }}개 알림</span>
        </div>
        <div class="flex items-center space-x-2">
          <span class="text-sm text-gray-500">정렬:</span>
          <select v-model="sortBy" class="text-sm border-gray-300 rounded-md">
            <option value="timestamp">시간순</option>
            <option value="severity">심각도순</option>
            <option value="equipment">설비순</option>
          </select>
        </div>
      </div>

      <div class="space-y-3">
        <div 
          v-for="alert in paginatedAlerts" 
          :key="alert.id"
          class="flex items-start space-x-4 p-4 rounded-lg border transition-colors duration-200"
          :class="[
            getAlertBorderColor(alert.severity),
            alert.status === 'unread' ? 'bg-gray-50' : 'bg-white'
          ]"
        >
          <!-- 심각도 아이콘 -->
          <div class="flex-shrink-0 mt-1">
            <component 
              :is="getAlertIcon(alert.severity)" 
              class="h-6 w-6" 
              :class="getAlertIconColor(alert.severity)" 
            />
          </div>

          <!-- 알림 내용 -->
          <div class="flex-1 min-w-0">
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <div class="flex items-center space-x-2">
                  <h4 class="text-sm font-medium text-gray-900">{{ alert.title }}</h4>
                  <span v-if="alert.status === 'unread'" class="h-2 w-2 bg-primary-500 rounded-full"></span>
                </div>
                <p class="mt-1 text-sm text-gray-600">{{ alert.message }}</p>
                <div class="mt-2 flex items-center space-x-4">
                  <span class="text-xs text-gray-500">설비: {{ alert.equipment }}</span>
                  <span class="text-xs text-gray-500">{{ formatDateTime(alert.timestamp) }}</span>
                  <span class="status-badge" :class="getStatusClass(alert.status)">
                    {{ getStatusText(alert.status) }}
                  </span>
                </div>
              </div>
              
              <!-- 액션 버튼 -->
              <div class="flex items-center space-x-2 ml-4">
                <button 
                  v-if="alert.status === 'unread'"
                  @click="markAsRead(alert.id)"
                  class="text-xs text-primary-600 hover:text-primary-500"
                >
                  읽음 처리
                </button>
                <button 
                  v-if="alert.status !== 'acknowledged' && alert.status !== 'resolved'"
                  @click="acknowledgeAlert(alert.id)"
                  class="text-xs text-success-600 hover:text-success-500"
                >
                  확인
                </button>
                <button 
                  v-if="alert.severity === 'critical' && alert.status !== 'resolved'"
                  @click="resolveAlert(alert.id)"
                  class="text-xs text-gray-600 hover:text-gray-500"
                >
                  해결
                </button>
                <button class="text-gray-400 hover:text-gray-600">
                  <EllipsisVerticalIcon class="h-4 w-4" />
                </button>
              </div>
            </div>

            <!-- AI 예측 정보 (해당하는 경우) -->
            <div v-if="alert.prediction" class="mt-3 p-3 bg-gradient-to-r from-primary-50 to-primary-100 rounded-lg border border-primary-200">
              <div class="flex items-center space-x-2">
                <CpuChipIcon class="h-4 w-4 text-primary-600" />
                <span class="text-xs font-medium text-primary-800">AI 예측 분석</span>
              </div>
              <p class="text-xs text-primary-700 mt-1">{{ alert.prediction.description }}</p>
              <div class="flex items-center space-x-4 mt-2">
                <span class="text-xs text-primary-600">신뢰도: {{ alert.prediction.confidence }}%</span>
                <span class="text-xs text-primary-600">예상 시점: {{ alert.prediction.timeframe }}</span>
              </div>
            </div>

            <!-- 권장 조치 -->
            <div v-if="alert.recommendations?.length" class="mt-3">
              <h5 class="text-xs font-medium text-gray-700 mb-2">권장 조치:</h5>
              <ul class="space-y-1">
                <li v-for="(rec, index) in alert.recommendations" :key="index" class="flex items-start space-x-2">
                  <div class="h-1.5 w-1.5 bg-gray-400 rounded-full mt-1.5 flex-shrink-0"></div>
                  <span class="text-xs text-gray-600">{{ rec }}</span>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <!-- 페이지네이션 -->
      <div class="flex items-center justify-between border-t border-gray-200 bg-white px-4 py-3 sm:px-6 mt-6">
        <div class="flex flex-1 justify-between sm:hidden">
          <button 
            @click="previousPage"
            :disabled="currentPage === 1"
            class="relative inline-flex items-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50"
          >
            이전
          </button>
          <button 
            @click="nextPage"
            :disabled="currentPage === totalPages"
            class="relative ml-3 inline-flex items-center rounded-md border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:opacity-50"
          >
            다음
          </button>
        </div>
        <div class="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
          <div>
            <p class="text-sm text-gray-700">
              총 <span class="font-medium">{{ filteredAlerts.length }}</span>개 중 
              <span class="font-medium">{{ startIndex + 1 }}</span>-<span class="font-medium">{{ endIndex }}</span>개 표시
            </p>
          </div>
          <div>
            <nav class="isolate inline-flex -space-x-px rounded-md shadow-sm">
              <button 
                @click="previousPage"
                :disabled="currentPage === 1"
                class="relative inline-flex items-center rounded-l-md px-2 py-2 text-gray-400 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 disabled:opacity-50"
              >
                <ChevronLeftIcon class="h-5 w-5" />
              </button>
              <button
                v-for="page in visiblePages"
                :key="page"
                @click="goToPage(page)"
                :class="[
                  'relative inline-flex items-center px-4 py-2 text-sm font-semibold',
                  page === currentPage
                    ? 'z-10 bg-primary-600 text-white'
                    : 'text-gray-900 ring-1 ring-inset ring-gray-300 hover:bg-gray-50'
                ]"
              >
                {{ page }}
              </button>
              <button 
                @click="nextPage"
                :disabled="currentPage === totalPages"
                class="relative inline-flex items-center rounded-r-md px-2 py-2 text-gray-400 ring-1 ring-inset ring-gray-300 hover:bg-gray-50 disabled:opacity-50"
              >
                <ChevronRightIcon class="h-5 w-5" />
              </button>
            </nav>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { format } from 'date-fns'
import { ko } from 'date-fns/locale'
import {
  ExclamationTriangleIcon,
  ExclamationCircleIcon,
  InformationCircleIcon,
  EyeSlashIcon,
  CheckIcon,
  Cog6ToothIcon,
  MagnifyingGlassIcon,
  EllipsisVerticalIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
  CpuChipIcon
} from '@heroicons/vue/24/outline'

// 필터 상태
const searchQuery = ref('')
const severityFilter = ref('')
const equipmentFilter = ref('')
const statusFilter = ref('')
const sortBy = ref('timestamp')

// 페이지네이션
const currentPage = ref(1)
const itemsPerPage = ref(10)

// 통계 데이터
const criticalAlerts = ref(3)
const warningAlerts = ref(8)
const infoAlerts = ref(12)
const unreadAlerts = ref(7)

// 알림 데이터
const alerts = ref([
  {
    id: 1,
    severity: 'critical',
    title: '온도 센서 임계값 초과',
    message: 'CVD-001 설비의 온도가 설정 임계값 800°C를 초과하여 현재 815°C를 기록하고 있습니다.',
    equipment: 'CVD-001',
    timestamp: new Date(Date.now() - 30 * 60 * 1000),
    status: 'unread',
    prediction: {
      description: '현재 온도 상승 패턴으로 볼 때 72시간 내 설비 고장 가능성이 높습니다.',
      confidence: 87,
      timeframe: '72시간 내'
    },
    recommendations: [
      '즉시 온도 설정값 확인 및 조정',
      '냉각 시스템 점검',
      '긴급 정비팀 연락'
    ]
  },
  {
    id: 2,
    severity: 'warning',
    title: '압력 수치 임계값 근접',
    message: 'ETCH-003 설비의 챔버 압력이 임계값에 근접하고 있습니다. (현재: 9.8bar, 임계값: 10bar)',
    equipment: 'ETCH-003',
    timestamp: new Date(Date.now() - 45 * 60 * 1000),
    status: 'unread',
    recommendations: [
      '압력 밸브 점검',
      '가스 공급 라인 확인'
    ]
  },
  {
    id: 3,
    severity: 'info',
    title: '정기 점검 일정 알림',
    message: 'CMP-002 설비의 월간 정기 점검이 내일(5월 24일) 예정되어 있습니다.',
    equipment: 'CMP-002',
    timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000),
    status: 'read',
    recommendations: [
      '점검 도구 준비',
      '작업 일정 확인'
    ]
  },
  {
    id: 4,
    severity: 'critical',
    title: '진동 센서 이상 감지',
    message: 'LITHO-001 설비에서 비정상적인 진동이 감지되었습니다. (현재: 2.5mm/s, 정상범위: 0-1.0mm/s)',
    equipment: 'LITHO-001',
    timestamp: new Date(Date.now() - 3 * 60 * 60 * 1000),
    status: 'acknowledged',
    prediction: {
      description: '진동 패턴 분석 결과 베어링 마모로 인한 고장 가능성이 있습니다.',
      confidence: 92,
      timeframe: '48시간 내'
    },
    recommendations: [
      '즉시 설비 가동 중단',
      '베어링 상태 점검',
      '정비팀 긴급 출동 요청'
    ]
  },
  {
    id: 5,
    severity: 'warning',
    title: '가동률 저하 감지',
    message: 'CVD-001 설비의 가동률이 지난 4시간 동안 지속적으로 감소하고 있습니다. (현재: 85%)',
    equipment: 'CVD-001',
    timestamp: new Date(Date.now() - 4 * 60 * 60 * 1000),
    status: 'read',
    recommendations: [
      '공정 파라미터 점검',
      '원료 공급 상태 확인'
    ]
  },
  {
    id: 6,
    severity: 'info',
    title: '소프트웨어 업데이트 완료',
    message: 'ETCH-003 설비의 제어 소프트웨어가 v2.2.0으로 성공적으로 업데이트되었습니다.',
    equipment: 'ETCH-003',
    timestamp: new Date(Date.now() - 6 * 60 * 60 * 1000),
    status: 'read'
  },
  {
    id: 7,
    severity: 'warning',
    title: '전력 소비 증가',
    message: 'CMP-002 설비의 전력 소비가 평소보다 15% 증가했습니다. (현재: 172kW, 평균: 150kW)',
    equipment: 'CMP-002',
    timestamp: new Date(Date.now() - 8 * 60 * 60 * 1000),
    status: 'unread',
    recommendations: [
      '모터 효율성 점검',
      '전력 시스템 진단'
    ]
  },
  {
    id: 8,
    severity: 'info',
    title: '예방 정비 완료',
    message: 'LITHO-001 설비의 예방 정비가 성공적으로 완료되었습니다.',
    equipment: 'LITHO-001',
    timestamp: new Date(Date.now() - 12 * 60 * 60 * 1000),
    status: 'resolved'
  }
])

// 필터링된 알림 목록
const filteredAlerts = computed(() => {
  let filtered = alerts.value

  // 검색 필터
  if (searchQuery.value) {
    filtered = filtered.filter(item => 
      item.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      item.message.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }

  // 심각도 필터
  if (severityFilter.value) {
    filtered = filtered.filter(item => item.severity === severityFilter.value)
  }

  // 설비 필터
  if (equipmentFilter.value) {
    filtered = filtered.filter(item => item.equipment === equipmentFilter.value)
  }

  // 상태 필터
  if (statusFilter.value) {
    filtered = filtered.filter(item => item.status === statusFilter.value)
  }

  // 정렬
  filtered.sort((a, b) => {
    switch (sortBy.value) {
      case 'timestamp':
        return new Date(b.timestamp) - new Date(a.timestamp)
      case 'severity':
        const severityOrder = { critical: 3, warning: 2, info: 1 }
        return severityOrder[b.severity] - severityOrder[a.severity]
      case 'equipment':
        return a.equipment.localeCompare(b.equipment)
      default:
        return 0
    }
  })

  return filtered
})

// 페이지네이션 계산
const totalPages = computed(() => Math.ceil(filteredAlerts.value.length / itemsPerPage.value))
const startIndex = computed(() => (currentPage.value - 1) * itemsPerPage.value)
const endIndex = computed(() => Math.min(startIndex.value + itemsPerPage.value, filteredAlerts.value.length))

const paginatedAlerts = computed(() => {
  return filteredAlerts.value.slice(startIndex.value, endIndex.value)
})

const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(1, currentPage.value - 2)
  const end = Math.min(totalPages.value, currentPage.value + 2)
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  
  return pages
})

// 페이지네이션 함수
const goToPage = (page) => {
  currentPage.value = page
}

const previousPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

// 알림 액션 함수
const markAsRead = (alertId) => {
  const alert = alerts.value.find(a => a.id === alertId)
  if (alert) {
    alert.status = 'read'
    unreadAlerts.value--
  }
}

const acknowledgeAlert = (alertId) => {
  const alert = alerts.value.find(a => a.id === alertId)
  if (alert) {
    alert.status = 'acknowledged'
    if (alert.status === 'unread') {
      unreadAlerts.value--
    }
  }
}

const resolveAlert = (alertId) => {
  const alert = alerts.value.find(a => a.id === alertId)
  if (alert) {
    alert.status = 'resolved'
    if (alert.status === 'unread') {
      unreadAlerts.value--
    }
  }
}

const markAllAsRead = () => {
  alerts.value.forEach(alert => {
    if (alert.status === 'unread') {
      alert.status = 'read'
    }
  })
  unreadAlerts.value = 0
}

// 필터 변경 시 첫 페이지로 이동
watch([searchQuery, severityFilter, equipmentFilter, statusFilter], () => {
  currentPage.value = 1
})

// 헬퍼 함수들
const getAlertIcon = (severity) => {
  const icons = {
    critical: ExclamationTriangleIcon,
    warning: ExclamationCircleIcon,
    info: InformationCircleIcon
  }
  return icons[severity] || InformationCircleIcon
}

const getAlertIconColor = (severity) => {
  const colors = {
    critical: 'text-danger-500',
    warning: 'text-warning-500',
    info: 'text-primary-500'
  }
  return colors[severity] || 'text-primary-500'
}

const getAlertBorderColor = (severity) => {
  const colors = {
    critical: 'border-danger-200',
    warning: 'border-warning-200',
    info: 'border-primary-200'
  }
  return colors[severity] || 'border-gray-200'
}

const getStatusClass = (status) => {
  const classes = {
    unread: 'status-warning',
    read: 'status-normal',
    acknowledged: 'status-normal',
    resolved: 'status-normal'
  }
  return classes[status] || 'status-normal'
}

const getStatusText = (status) => {
  const texts = {
    unread: '읽지 않음',
    read: '읽음',
    acknowledged: '확인됨',
    resolved: '해결됨'
  }
  return texts[status] || '읽음'
}

const formatDateTime = (date) => {
  return format(date, 'MM.dd HH:mm', { locale: ko })
}
</script>
