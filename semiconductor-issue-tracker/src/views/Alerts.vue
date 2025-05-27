<template>
  <div class="space-y-6 animate-fade-in-up">
    <!-- 페이지 헤더 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h1 class="text-2xl font-bold gradient-text">알림 관리</h1>
        <p class="mt-1 text-secondary-600">실시간 알림 및 AI 예측 분석 결과</p>
      </div>
      <div class="mt-4 sm:mt-0 flex space-x-3">
        <button class="btn-secondary">
          <CheckIcon class="w-4 h-4 mr-2" />
          모두 읽음 처리
        </button>
        <button class="btn-primary">
          <Cog6ToothIcon class="w-4 h-4 mr-2" />
          알림 설정
        </button>
      </div>
    </div>

    <!-- 필터 및 검색 -->
    <div class="card">
      <div class="grid grid-cols-1 md:grid-cols-5 gap-4">
        <div>
          <label class="block text-sm font-medium text-secondary-700 mb-2">검색</label>
          <div class="relative">
            <MagnifyingGlassIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-secondary-400" />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="알림 내용 검색..."
              class="input-field pl-10"
            />
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium text-secondary-700 mb-2">심각도</label>
          <select v-model="severityFilter" class="select-field">
            <option value="">전체</option>
            <option value="critical">긴급</option>
            <option value="warning">경고</option>
            <option value="info">정보</option>
            <option value="success">성공</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-secondary-700 mb-2">설비</label>
          <select v-model="equipmentFilter" class="select-field">
            <option value="">전체</option>
            <option value="CVD-001">CVD-001</option>
            <option value="ETCH-003">ETCH-003</option>
            <option value="CMP-002">CMP-002</option>
            <option value="LITHO-001">LITHO-001</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-secondary-700 mb-2">상태</label>
          <select v-model="statusFilter" class="select-field">
            <option value="">전체</option>
            <option value="unread">읽지 않음</option>
            <option value="read">읽음</option>
            <option value="acknowledged">확인됨</option>
            <option value="resolved">해결됨</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-secondary-700 mb-2">정렬</label>
          <select v-model="sortBy" class="select-field">
            <option value="timestamp">시간</option>
            <option value="severity">심각도</option>
            <option value="equipment">설비</option>
            <option value="status">상태</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 알림 통계 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg">
            <BellIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">총 알림</p>
            <p class="text-2xl font-semibold text-secondary-900">{{ totalAlerts }}</p>
          </div>
        </div>
      </div>
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-danger-500 to-danger-600 rounded-lg">
            <ExclamationTriangleIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">긴급</p>
            <p class="text-2xl font-semibold text-secondary-900">{{ criticalAlerts }}</p>
          </div>
        </div>
      </div>
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-warning-500 to-warning-600 rounded-lg">
            <ExclamationCircleIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">경고</p>
            <p class="text-2xl font-semibold text-secondary-900">{{ warningAlerts }}</p>
          </div>
        </div>
      </div>
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-secondary-500 to-secondary-600 rounded-lg">
            <EyeSlashIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">읽지 않음</p>
            <p class="text-2xl font-semibold text-secondary-900">{{ unreadAlerts }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- AI 예측 알림 -->
    <div class="card bg-gradient-to-br from-blue-50 to-blue-100 border-blue-200">
      <div class="card-header bg-gradient-to-r from-blue-500 to-blue-600">
        <div class="flex items-center">
          <SparklesIcon class="w-6 h-6 text-white mr-2" />
          <h3 class="text-lg font-semibold text-white">AI 예측 분석</h3>
        </div>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div class="bg-white/50 rounded-lg p-4">
          <div class="flex items-center mb-3">
            <ExclamationTriangleIcon class="w-5 h-5 text-danger-600 mr-2" />
            <h4 class="font-semibold text-danger-800">고장 예측</h4>
          </div>
          <p class="text-2xl font-bold text-danger-900 mb-2">72시간 내</p>
          <p class="text-sm text-danger-700 mb-2">CVD-001 온도 센서 이상 패턴</p>
          <div class="flex items-center justify-between">
            <span class="text-xs text-danger-600">신뢰도: 87%</span>
            <button class="btn-ghost text-xs text-danger-700">상세보기</button>
          </div>
        </div>
        
        <div class="bg-white/50 rounded-lg p-4">
          <div class="flex items-center mb-3">
            <WrenchScrewdriverIcon class="w-5 h-5 text-warning-600 mr-2" />
            <h4 class="font-semibold text-warning-800">정비 권장</h4>
          </div>
          <p class="text-2xl font-bold text-warning-900 mb-2">5일 내</p>
          <p class="text-sm text-warning-700 mb-2">ETCH-003 압력 밸브 교체</p>
          <div class="flex items-center justify-between">
            <span class="text-xs text-warning-600">우선순위: 높음</span>
            <button class="btn-ghost text-xs text-warning-700">상세보기</button>
          </div>
        </div>
        
        <div class="bg-white/50 rounded-lg p-4">
          <div class="flex items-center mb-3">
            <ArrowTrendingUpIcon class="w-5 h-5 text-success-600 mr-2" />
            <h4 class="font-semibold text-success-800">효율성 개선</h4>
          </div>
          <p class="text-2xl font-bold text-success-900 mb-2">+3.2%</p>
          <p class="text-sm text-success-700 mb-2">공정 파라미터 최적화 가능</p>
          <div class="flex items-center justify-between">
            <span class="text-xs text-success-600">예상 절약: 15%</span>
            <button class="btn-ghost text-xs text-success-700">상세보기</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 알림 목록 -->
    <div class="card">
      <div class="card-header">
        <h3 class="text-lg font-semibold">알림 목록</h3>
      </div>
      <div class="space-y-3">
        <div v-for="alert in paginatedAlerts" :key="alert.id" 
             class="flex items-start p-4 rounded-lg border transition-all duration-200 hover:shadow-md cursor-pointer"
             :class="getAlertCardClass(alert)"
             @click="toggleAlert(alert)">
          <div class="flex-shrink-0 mr-4">
            <div class="w-10 h-10 rounded-full flex items-center justify-center"
                 :class="getAlertIconClass(alert.severity)">
              <component :is="getAlertIcon(alert.severity)" class="w-5 h-5 text-white" />
            </div>
          </div>
          
          <div class="flex-1 min-w-0">
            <div class="flex items-center justify-between mb-2">
              <div class="flex items-center space-x-3">
                <h4 class="text-sm font-semibold text-secondary-900" :class="{ 'font-bold': alert.status === 'unread' }">
                  {{ alert.title }}
                </h4>
                <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium"
                      :class="getSeverityClass(alert.severity)">
                  {{ getSeverityText(alert.severity) }}
                </span>
                <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                  {{ alert.equipment }}
                </span>
              </div>
              <div class="flex items-center space-x-2">
                <span class="text-xs text-secondary-500">{{ formatDateTime(alert.timestamp) }}</span>
                <span class="status-badge" :class="getStatusClass(alert.status)">
                  {{ getStatusText(alert.status) }}
                </span>
              </div>
            </div>
            
            <p class="text-sm text-secondary-700 mb-3" :class="{ 'font-medium': alert.status === 'unread' }">
              {{ alert.message }}
            </p>
            
            <div v-if="alert.aiAnalysis" class="bg-blue-50 border border-blue-200 rounded-lg p-3 mb-3">
              <div class="flex items-center mb-2">
                <SparklesIcon class="w-4 h-4 text-blue-600 mr-2" />
                <span class="text-sm font-medium text-blue-800">AI 분석 결과</span>
              </div>
              <p class="text-sm text-blue-700 mb-2">{{ alert.aiAnalysis.description }}</p>
              <div class="flex items-center justify-between">
                <span class="text-xs text-blue-600">신뢰도: {{ alert.aiAnalysis.confidence }}%</span>
                <span class="text-xs text-blue-600">예상 시점: {{ alert.aiAnalysis.timeframe }}</span>
              </div>
            </div>
            
            <div v-if="alert.recommendations && alert.recommendations.length > 0" class="mb-3">
              <h5 class="text-sm font-medium text-secondary-900 mb-2">권장 조치사항</h5>
              <ul class="space-y-1">
                <li v-for="rec in alert.recommendations" :key="rec" class="text-sm text-secondary-700 flex items-start">
                  <ChevronRightIcon class="w-3 h-3 text-secondary-400 mt-0.5 mr-1 flex-shrink-0" />
                  {{ rec }}
                </li>
              </ul>
            </div>
            
            <div class="flex items-center justify-between">
              <div class="flex items-center space-x-4 text-xs text-secondary-500">
                <span>생성: {{ formatDateTime(alert.createdAt) }}</span>
                <span v-if="alert.acknowledgedAt">확인: {{ formatDateTime(alert.acknowledgedAt) }}</span>
                <span v-if="alert.resolvedAt">해결: {{ formatDateTime(alert.resolvedAt) }}</span>
              </div>
              <div class="flex items-center space-x-2">
                <button v-if="alert.status === 'unread'" 
                        @click.stop="markAsRead(alert)"
                        class="btn-ghost text-xs">
                  읽음 처리
                </button>
                <button v-if="alert.status === 'read'" 
                        @click.stop="acknowledge(alert)"
                        class="btn-ghost text-xs">
                  확인
                </button>
                <button v-if="alert.status === 'acknowledged'" 
                        @click.stop="resolve(alert)"
                        class="btn-ghost text-xs">
                  해결
                </button>
                <button class="btn-ghost text-xs">상세보기</button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 페이지네이션 -->
      <div class="flex items-center justify-between px-6 py-3 border-t border-secondary-200 mt-6">
        <div class="flex items-center text-sm text-secondary-500">
          <span>총 {{ filteredAlerts.length }}개 중 {{ startIndex + 1 }}-{{ endIndex }}개 표시</span>
        </div>
        <div class="flex items-center space-x-2">
          <button
            @click="currentPage--"
            :disabled="currentPage === 1"
            class="btn-ghost text-sm"
            :class="{ 'opacity-50 cursor-not-allowed': currentPage === 1 }"
          >
            이전
          </button>
          <span class="text-sm text-secondary-700">{{ currentPage }} / {{ totalPages }}</span>
          <button
            @click="currentPage++"
            :disabled="currentPage === totalPages"
            class="btn-ghost text-sm"
            :class="{ 'opacity-50 cursor-not-allowed': currentPage === totalPages }"
          >
            다음
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { format } from 'date-fns'
import { ko } from 'date-fns/locale'
import {
  CheckIcon,
  Cog6ToothIcon,
  MagnifyingGlassIcon,
  BellIcon,
  ExclamationTriangleIcon,
  ExclamationCircleIcon,
  EyeSlashIcon,
  SparklesIcon,
  WrenchScrewdriverIcon,
  ArrowTrendingUpIcon,
  ChevronRightIcon,
  InformationCircleIcon,
  CheckCircleIcon,
  XCircleIcon
} from '@heroicons/vue/24/outline'

export default {
  name: 'Alerts',
  components: {
    CheckIcon,
    Cog6ToothIcon,
    MagnifyingGlassIcon,
    BellIcon,
    ExclamationTriangleIcon,
    ExclamationCircleIcon,
    EyeSlashIcon,
    SparklesIcon,
    WrenchScrewdriverIcon,
    ArrowTrendingUpIcon,
    ChevronRightIcon,
    InformationCircleIcon,
    CheckCircleIcon,
    XCircleIcon
  },
  setup() {
    const searchQuery = ref('')
    const severityFilter = ref('')
    const equipmentFilter = ref('')
    const statusFilter = ref('')
    const sortBy = ref('timestamp')
    const currentPage = ref(1)
    const itemsPerPage = ref(10)

    const alerts = ref([
      {
        id: 'alert-001',
        title: 'CVD-001 온도 센서 이상 감지',
        message: '온도 센서에서 비정상적인 드리프트 패턴이 감지되었습니다. 72시간 내 고장 가능성이 높습니다.',
        severity: 'critical',
        equipment: 'CVD-001',
        status: 'unread',
        timestamp: '2025-05-23T09:35:00',
        createdAt: '2025-05-23T09:35:00',
        acknowledgedAt: null,
        resolvedAt: null,
        aiAnalysis: {
          description: '온도 센서의 드리프트 패턴 분석 결과, 센서 노화로 인한 고장 가능성이 높습니다.',
          confidence: 87,
          timeframe: '72시간 내'
        },
        recommendations: [
          '온도 센서 교체 준비',
          '백업 센서 활성화',
          '정비팀 긴급 연락'
        ]
      },
      {
        id: 'alert-002',
        title: 'ETCH-003 압력 수치 임계값 근접',
        message: '챔버 압력이 임계값에 근접하고 있습니다. 모니터링을 강화하고 필요시 조치하세요.',
        severity: 'warning',
        equipment: 'ETCH-003',
        status: 'read',
        timestamp: '2025-05-23T09:20:00',
        createdAt: '2025-05-23T09:20:00',
        acknowledgedAt: '2025-05-23T09:25:00',
        resolvedAt: null,
        aiAnalysis: {
          description: '압력 밸브의 성능 저하로 인한 압력 상승이 예상됩니다.',
          confidence: 73,
          timeframe: '5일 내'
        },
        recommendations: [
          '압력 밸브 점검',
          '가스 라인 확인',
          '정비 일정 조정'
        ]
      },
      {
        id: 'alert-003',
        title: 'CMP-002 예방 정비 일정 알림',
        message: '정기 예방 정비 일정이 도래했습니다. 정비 계획을 확인하고 일정을 조정하세요.',
        severity: 'info',
        equipment: 'CMP-002',
        status: 'acknowledged',
        timestamp: '2025-05-23T08:35:00',
        createdAt: '2025-05-23T08:35:00',
        acknowledgedAt: '2025-05-23T08:40:00',
        resolvedAt: null,
        recommendations: [
          '정비 계획서 검토',
          '부품 재고 확인',
          '정비팀 일정 조율'
        ]
      },
      {
        id: 'alert-004',
        title: 'LITHO-001 수율 개선 완료',
        message: '렌즈 교체 작업이 완료되어 수율이 2.5% 향상되었습니다.',
        severity: 'success',
        equipment: 'LITHO-001',
        status: 'resolved',
        timestamp: '2025-05-23T07:15:00',
        createdAt: '2025-05-23T07:15:00',
        acknowledgedAt: '2025-05-23T07:20:00',
        resolvedAt: '2025-05-23T07:30:00',
        recommendations: []
      },
      {
        id: 'alert-005',
        title: 'CVD-001 공정 파라미터 최적화 권장',
        message: 'AI 분석 결과, 공정 파라미터 최적화를 통해 효율성을 3.2% 개선할 수 있습니다.',
        severity: 'info',
        equipment: 'CVD-001',
        status: 'unread',
        timestamp: '2025-05-23T06:45:00',
        createdAt: '2025-05-23T06:45:00',
        acknowledgedAt: null,
        resolvedAt: null,
        aiAnalysis: {
          description: '온도 및 압력 파라미터 조정을 통한 효율성 개선이 가능합니다.',
          confidence: 92,
          timeframe: '즉시 적용 가능'
        },
        recommendations: [
          '온도 설정값 조정',
          '압력 제어 최적화',
          '테스트 런 실행'
        ]
      }
    ])

    const filteredAlerts = computed(() => {
      let filtered = alerts.value

      // 검색 필터
      if (searchQuery.value) {
        filtered = filtered.filter(alert =>
          alert.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          alert.message.toLowerCase().includes(searchQuery.value.toLowerCase())
        )
      }

      // 심각도 필터
      if (severityFilter.value) {
        filtered = filtered.filter(alert => alert.severity === severityFilter.value)
      }

      // 설비 필터
      if (equipmentFilter.value) {
        filtered = filtered.filter(alert => alert.equipment === equipmentFilter.value)
      }

      // 상태 필터
      if (statusFilter.value) {
        filtered = filtered.filter(alert => alert.status === statusFilter.value)
      }

      // 정렬
      filtered.sort((a, b) => {
        switch (sortBy.value) {
          case 'timestamp':
            return new Date(b.timestamp) - new Date(a.timestamp)
          case 'severity':
            const severityOrder = { critical: 4, warning: 3, info: 2, success: 1 }
            return severityOrder[b.severity] - severityOrder[a.severity]
          case 'equipment':
            return a.equipment.localeCompare(b.equipment)
          case 'status':
            const statusOrder = { unread: 4, read: 3, acknowledged: 2, resolved: 1 }
            return statusOrder[b.status] - statusOrder[a.status]
          default:
            return 0
        }
      })

      return filtered
    })

    const totalPages = computed(() => {
      return Math.ceil(filteredAlerts.value.length / itemsPerPage.value)
    })

    const startIndex = computed(() => {
      return (currentPage.value - 1) * itemsPerPage.value
    })

    const endIndex = computed(() => {
      return Math.min(startIndex.value + itemsPerPage.value, filteredAlerts.value.length)
    })

    const paginatedAlerts = computed(() => {
      return filteredAlerts.value.slice(startIndex.value, endIndex.value)
    })

    const totalAlerts = computed(() => alerts.value.length)
    const criticalAlerts = computed(() => alerts.value.filter(a => a.severity === 'critical').length)
    const warningAlerts = computed(() => alerts.value.filter(a => a.severity === 'warning').length)
    const unreadAlerts = computed(() => alerts.value.filter(a => a.status === 'unread').length)

    const getAlertIcon = (severity) => {
      const icons = {
        critical: XCircleIcon,
        warning: ExclamationTriangleIcon,
        info: InformationCircleIcon,
        success: CheckCircleIcon
      }
      return icons[severity] || InformationCircleIcon
    }

    const getAlertIconClass = (severity) => {
      const classes = {
        critical: 'bg-gradient-to-r from-danger-500 to-danger-600',
        warning: 'bg-gradient-to-r from-warning-500 to-warning-600',
        info: 'bg-gradient-to-r from-blue-500 to-blue-600',
        success: 'bg-gradient-to-r from-success-500 to-success-600'
      }
      return classes[severity] || 'bg-gradient-to-r from-secondary-500 to-secondary-600'
    }

    const getAlertCardClass = (alert) => {
      const baseClass = 'border-l-4'
      const severityClasses = {
        critical: 'border-danger-400 bg-danger-50',
        warning: 'border-warning-400 bg-warning-50',
        info: 'border-blue-400 bg-blue-50',
        success: 'border-success-400 bg-success-50'
      }
      const statusClass = alert.status === 'unread' ? 'ring-2 ring-blue-200' : ''
      return `${baseClass} ${severityClasses[alert.severity]} ${statusClass}`
    }

    const getSeverityClass = (severity) => {
      const classes = {
        critical: 'bg-danger-100 text-danger-800',
        warning: 'bg-warning-100 text-warning-800',
        info: 'bg-blue-100 text-blue-800',
        success: 'bg-success-100 text-success-800'
      }
      return classes[severity] || 'bg-secondary-100 text-secondary-800'
    }

    const getSeverityText = (severity) => {
      const texts = {
        critical: '긴급',
        warning: '경고',
        info: '정보',
        success: '성공'
      }
      return texts[severity] || '정보'
    }

    const getStatusClass = (status) => {
      const classes = {
        unread: 'bg-blue-100 text-blue-800',
        read: 'bg-secondary-100 text-secondary-800',
        acknowledged: 'bg-warning-100 text-warning-800',
        resolved: 'bg-success-100 text-success-800'
      }
      return classes[status] || 'bg-secondary-100 text-secondary-800'
    }

    const getStatusText = (status) => {
      const texts = {
        unread: '읽지 않음',
        read: '읽음',
        acknowledged: '확인됨',
        resolved: '해결됨'
      }
      return texts[status] || '읽지 않음'
    }

    const formatDateTime = (dateString) => {
      return format(new Date(dateString), 'MM.dd HH:mm', { locale: ko })
    }

    const toggleAlert = (alert) => {
      // 알림 상세 보기 토글 로직
    }

    const markAsRead = (alert) => {
      alert.status = 'read'
    }

    const acknowledge = (alert) => {
      alert.status = 'acknowledged'
      alert.acknowledgedAt = new Date().toISOString()
    }

    const resolve = (alert) => {
      alert.status = 'resolved'
      alert.resolvedAt = new Date().toISOString()
    }

    return {
      searchQuery,
      severityFilter,
      equipmentFilter,
      statusFilter,
      sortBy,
      currentPage,
      itemsPerPage,
      filteredAlerts,
      paginatedAlerts,
      totalPages,
      startIndex,
      endIndex,
      totalAlerts,
      criticalAlerts,
      warningAlerts,
      unreadAlerts,
      getAlertIcon,
      getAlertIconClass,
      getAlertCardClass,
      getSeverityClass,
      getSeverityText,
      getStatusClass,
      getStatusText,
      formatDateTime,
      toggleAlert,
      markAsRead,
      acknowledge,
      resolve
    }
  }
}
</script>
