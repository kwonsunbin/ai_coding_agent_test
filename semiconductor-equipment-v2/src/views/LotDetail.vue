<template>
  <div class="space-y-6 animate-fade-in-up">
    <!-- 페이지 헤더 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div>
        <div class="flex items-center space-x-2 mb-2">
          <button @click="goBack" class="text-blue-600 hover:text-blue-700">
            <ArrowLeftIcon class="w-5 h-5" />
          </button>
          <h1 class="text-2xl font-bold gradient-text">랏 상세 정보</h1>
        </div>
        <p class="mt-1 text-secondary-600">{{ lotData.lotId }} 처리 상세 내역</p>
      </div>
      <div class="mt-4 sm:mt-0 flex space-x-3">
        <button class="btn-secondary">
          <DocumentArrowDownIcon class="w-4 h-4 mr-2" />
          리포트 다운로드
        </button>
        <button class="btn-primary">
          <PrinterIcon class="w-4 h-4 mr-2" />
          인쇄
        </button>
      </div>
    </div>

    <!-- 랏 기본 정보 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="lg:col-span-2 card">
        <div class="card-header">
          <h3 class="text-lg font-semibold">기본 정보</h3>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">랏 ID</label>
              <p class="text-lg font-semibold text-secondary-900">{{ lotData.lotId }}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">제품 타입</label>
              <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-blue-100 text-blue-800">
                {{ lotData.productType }}
              </span>
            </div>
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">웨이퍼 수</label>
              <p class="text-lg text-secondary-900">{{ lotData.waferCount }}개</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">처리 설비</label>
              <router-link :to="`/equipment/${lotData.equipmentId}`" class="text-blue-600 hover:text-blue-700 font-medium">
                {{ lotData.equipmentName }}
              </router-link>
            </div>
          </div>
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">상태</label>
              <span class="status-badge" :class="getLotStatusClass(lotData.status)">
                {{ getLotStatusText(lotData.status) }}
              </span>
            </div>
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">우선순위</label>
              <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium" 
                    :class="getPriorityClass(lotData.priority)">
                {{ getPriorityText(lotData.priority) }}
              </span>
            </div>
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">레시피</label>
              <p class="text-lg text-secondary-900">{{ lotData.recipe }}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">담당자</label>
              <p class="text-lg text-secondary-900">{{ lotData.operator }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 처리 현황 -->
      <div class="card">
        <div class="card-header">
          <h3 class="text-lg font-semibold">처리 현황</h3>
        </div>
        <div class="space-y-4">
          <div class="text-center">
            <div class="relative w-32 h-32 mx-auto mb-4">
              <svg class="w-32 h-32 transform -rotate-90" viewBox="0 0 36 36">
                <path
                  d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                  fill="none"
                  stroke="#e2e8f0"
                  stroke-width="2"
                />
                <path
                  d="M18 2.0845 a 15.9155 15.9155 0 0 1 0 31.831 a 15.9155 15.9155 0 0 1 0 -31.831"
                  fill="none"
                  :stroke="getProgressColor(lotData.progress)"
                  stroke-width="2"
                  :stroke-dasharray="`${lotData.progress}, 100`"
                />
              </svg>
              <div class="absolute inset-0 flex items-center justify-center">
                <span class="text-2xl font-bold text-secondary-900">{{ lotData.progress }}%</span>
              </div>
            </div>
            <p class="text-sm text-secondary-600">처리 진행률</p>
          </div>
          
          <div class="space-y-3">
            <div class="flex justify-between">
              <span class="text-sm text-secondary-600">시작 시간</span>
              <span class="text-sm font-medium text-secondary-900">{{ formatDateTime(lotData.startTime) }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-sm text-secondary-600">예상 완료</span>
              <span class="text-sm font-medium text-secondary-900">{{ formatDateTime(lotData.estimatedEndTime) }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-sm text-secondary-600">실제 완료</span>
              <span class="text-sm font-medium text-secondary-900">
                {{ lotData.endTime ? formatDateTime(lotData.endTime) : '-' }}
              </span>
            </div>
            <div class="flex justify-between">
              <span class="text-sm text-secondary-600">처리 시간</span>
              <span class="text-sm font-medium text-secondary-900">{{ lotData.processingTime || '-' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 공정 단계 -->
    <div class="card">
      <div class="card-header">
        <h3 class="text-lg font-semibold">공정 단계</h3>
      </div>
      <div class="space-y-4">
        <div v-for="(step, index) in lotData.processSteps" :key="index" 
             class="flex items-center p-4 rounded-lg border transition-all duration-200"
             :class="getStepClass(step.status)">
          <div class="flex-shrink-0 mr-4">
            <div class="w-10 h-10 rounded-full flex items-center justify-center"
                 :class="getStepIconClass(step.status)">
              <component :is="getStepIcon(step.status)" class="w-5 h-5" />
            </div>
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-center justify-between">
              <h4 class="text-sm font-medium text-secondary-900">{{ step.name }}</h4>
              <span class="text-xs text-secondary-500">{{ step.duration }}</span>
            </div>
            <p class="text-sm text-secondary-600 mt-1">{{ step.description }}</p>
            <div class="flex items-center mt-2 space-x-4">
              <span class="text-xs text-secondary-500">
                시작: {{ formatDateTime(step.startTime) }}
              </span>
              <span class="text-xs text-secondary-500">
                완료: {{ step.endTime ? formatDateTime(step.endTime) : '진행 중' }}
              </span>
            </div>
          </div>
          <div class="flex-shrink-0 ml-4">
            <span class="status-badge" :class="getStepStatusClass(step.status)">
              {{ getStepStatusText(step.status) }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 품질 데이터 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div class="card">
        <div class="card-header">
          <h3 class="text-lg font-semibold">품질 지표</h3>
        </div>
        <div class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div class="text-center p-4 bg-gradient-to-br from-success-50 to-success-100 rounded-lg">
              <p class="text-2xl font-bold text-success-900">{{ lotData.yield }}%</p>
              <p class="text-sm text-success-700">수율</p>
            </div>
            <div class="text-center p-4 bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg">
              <p class="text-2xl font-bold text-blue-900">{{ lotData.defectRate }}%</p>
              <p class="text-sm text-blue-700">불량률</p>
            </div>
          </div>
          
          <div class="space-y-3">
            <div v-for="metric in lotData.qualityMetrics" :key="metric.name" class="flex items-center justify-between">
              <span class="text-sm text-secondary-600">{{ metric.name }}</span>
              <div class="flex items-center space-x-2">
                <span class="text-sm font-medium text-secondary-900">{{ metric.value }}</span>
                <span class="text-xs text-secondary-500">{{ metric.unit }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <h3 class="text-lg font-semibold">센서 데이터</h3>
        </div>
        <div class="h-64 flex items-center justify-center bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg">
          <div class="text-center">
            <ChartBarIcon class="w-16 h-16 text-blue-400 mx-auto mb-2" />
            <p class="text-blue-600 font-medium">실시간 센서 차트 (Chart.js)</p>
            <p class="text-sm text-blue-500 mt-1">온도, 압력, 유량 데이터</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 이벤트 로그 -->
    <div class="card">
      <div class="card-header">
        <h3 class="text-lg font-semibold">이벤트 로그</h3>
      </div>
      <div class="space-y-3">
        <div v-for="event in lotData.eventLog" :key="event.id" 
             class="flex items-start p-3 rounded-lg border border-secondary-200 hover:bg-secondary-50 transition-colors">
          <div class="flex-shrink-0 mr-3">
            <div class="w-8 h-8 rounded-full flex items-center justify-center"
                 :class="getEventIconClass(event.type)">
              <component :is="getEventIcon(event.type)" class="w-4 h-4" />
            </div>
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-center justify-between">
              <p class="text-sm font-medium text-secondary-900">{{ event.message }}</p>
              <span class="text-xs text-secondary-500">{{ formatDateTime(event.timestamp) }}</span>
            </div>
            <p class="text-xs text-secondary-600 mt-1">{{ event.details }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 첨부 파일 -->
    <div class="card">
      <div class="card-header">
        <h3 class="text-lg font-semibold">첨부 파일</h3>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div v-for="file in lotData.attachments" :key="file.id" 
             class="flex items-center p-3 border border-secondary-200 rounded-lg hover:bg-secondary-50 transition-colors">
          <DocumentIcon class="w-8 h-8 text-blue-500 mr-3" />
          <div class="flex-1 min-w-0">
            <p class="text-sm font-medium text-secondary-900 truncate">{{ file.name }}</p>
            <p class="text-xs text-secondary-500">{{ file.size }} • {{ formatDateTime(file.uploadedAt) }}</p>
          </div>
          <button class="ml-2 text-blue-600 hover:text-blue-700">
            <ArrowDownTrayIcon class="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { format } from 'date-fns'
import { ko } from 'date-fns/locale'
import {
  ArrowLeftIcon,
  DocumentArrowDownIcon,
  PrinterIcon,
  ChartBarIcon,
  CheckCircleIcon,
  ClockIcon,
  XCircleIcon,
  PlayIcon,
  PauseIcon,
  InformationCircleIcon,
  ExclamationTriangleIcon,
  DocumentIcon,
  ArrowDownTrayIcon
} from '@heroicons/vue/24/outline'

export default {
  name: 'LotDetail',
  components: {
    ArrowLeftIcon,
    DocumentArrowDownIcon,
    PrinterIcon,
    ChartBarIcon,
    CheckCircleIcon,
    ClockIcon,
    XCircleIcon,
    PlayIcon,
    PauseIcon,
    InformationCircleIcon,
    ExclamationTriangleIcon,
    DocumentIcon,
    ArrowDownTrayIcon
  },
  setup() {
    const route = useRoute()
    const router = useRouter()
    const lotId = ref(route.params.id)

    const lotData = ref({
      lotId: 'LOT-2025052301',
      productType: 'DRAM',
      status: 'completed',
      waferCount: 25,
      equipmentId: 'cvd-001',
      equipmentName: 'CVD-001',
      priority: 'high',
      recipe: 'DRAM_CVD_Recipe_v2.1',
      operator: '김엔지니어',
      progress: 100,
      startTime: '2025-05-23T08:30:00',
      estimatedEndTime: '2025-05-23T10:30:00',
      endTime: '2025-05-23T10:15:00',
      processingTime: '1시간 45분',
      yield: 98.5,
      defectRate: 1.5,
      qualityMetrics: [
        { name: '두께 균일도', value: '±2.5', unit: 'nm' },
        { name: '표면 거칠기', value: '0.8', unit: 'nm RMS' },
        { name: '입자 밀도', value: '< 0.1', unit: 'ea/cm²' },
        { name: '저항률', value: '1.2', unit: 'Ω·cm' }
      ],
      processSteps: [
        {
          name: '웨이퍼 로딩',
          description: '웨이퍼를 챔버에 로딩',
          status: 'completed',
          startTime: '2025-05-23T08:30:00',
          endTime: '2025-05-23T08:35:00',
          duration: '5분'
        },
        {
          name: '챔버 진공화',
          description: '챔버 내부 진공 상태 조성',
          status: 'completed',
          startTime: '2025-05-23T08:35:00',
          endTime: '2025-05-23T08:45:00',
          duration: '10분'
        },
        {
          name: '가스 주입',
          description: '반응 가스 주입 및 안정화',
          status: 'completed',
          startTime: '2025-05-23T08:45:00',
          endTime: '2025-05-23T08:50:00',
          duration: '5분'
        },
        {
          name: 'CVD 증착',
          description: '화학기상증착 공정 진행',
          status: 'completed',
          startTime: '2025-05-23T08:50:00',
          endTime: '2025-05-23T10:05:00',
          duration: '1시간 15분'
        },
        {
          name: '웨이퍼 언로딩',
          description: '처리 완료된 웨이퍼 언로딩',
          status: 'completed',
          startTime: '2025-05-23T10:05:00',
          endTime: '2025-05-23T10:15:00',
          duration: '10분'
        }
      ],
      eventLog: [
        {
          id: 1,
          type: 'info',
          message: '랏 처리 시작',
          details: '웨이퍼 25개 로딩 완료',
          timestamp: '2025-05-23T08:30:00'
        },
        {
          id: 2,
          type: 'warning',
          message: '온도 임계값 근접',
          details: '챔버 온도 395°C (임계값: 400°C)',
          timestamp: '2025-05-23T09:15:00'
        },
        {
          id: 3,
          type: 'info',
          message: '공정 파라미터 조정',
          details: '가스 유량 5% 증가',
          timestamp: '2025-05-23T09:30:00'
        },
        {
          id: 4,
          type: 'success',
          message: '랏 처리 완료',
          details: '수율 98.5% 달성',
          timestamp: '2025-05-23T10:15:00'
        }
      ],
      attachments: [
        {
          id: 1,
          name: 'CVD_Process_Report.pdf',
          size: '2.4 MB',
          uploadedAt: '2025-05-23T10:20:00'
        },
        {
          id: 2,
          name: 'Quality_Analysis.xlsx',
          size: '856 KB',
          uploadedAt: '2025-05-23T10:25:00'
        },
        {
          id: 3,
          name: 'Sensor_Data_Log.csv',
          size: '1.2 MB',
          uploadedAt: '2025-05-23T10:30:00'
        }
      ]
    })

    const goBack = () => {
      router.go(-1)
    }

    const getLotStatusClass = (status) => {
      const classes = {
        completed: 'status-normal',
        'in-progress': 'status-warning',
        failed: 'status-critical',
        queued: 'status-maintenance'
      }
      return classes[status] || 'status-normal'
    }

    const getLotStatusText = (status) => {
      const texts = {
        completed: '완료',
        'in-progress': '진행 중',
        failed: '실패',
        queued: '대기'
      }
      return texts[status] || '알 수 없음'
    }

    const getPriorityClass = (priority) => {
      const classes = {
        high: 'bg-danger-100 text-danger-800',
        medium: 'bg-warning-100 text-warning-800',
        low: 'bg-success-100 text-success-800'
      }
      return classes[priority] || 'bg-secondary-100 text-secondary-800'
    }

    const getPriorityText = (priority) => {
      const texts = {
        high: '높음',
        medium: '보통',
        low: '낮음'
      }
      return texts[priority] || '보통'
    }

    const getProgressColor = (progress) => {
      if (progress >= 95) return '#10b981'
      if (progress >= 70) return '#f59e0b'
      return '#ef4444'
    }

    const getStepClass = (status) => {
      const classes = {
        completed: 'border-success-200 bg-success-50',
        'in-progress': 'border-warning-200 bg-warning-50',
        pending: 'border-secondary-200 bg-secondary-50',
        failed: 'border-danger-200 bg-danger-50'
      }
      return classes[status] || 'border-secondary-200 bg-secondary-50'
    }

    const getStepIconClass = (status) => {
      const classes = {
        completed: 'bg-success-500 text-white',
        'in-progress': 'bg-warning-500 text-white',
        pending: 'bg-secondary-300 text-white',
        failed: 'bg-danger-500 text-white'
      }
      return classes[status] || 'bg-secondary-300 text-white'
    }

    const getStepIcon = (status) => {
      const icons = {
        completed: CheckCircleIcon,
        'in-progress': PlayIcon,
        pending: ClockIcon,
        failed: XCircleIcon
      }
      return icons[status] || ClockIcon
    }

    const getStepStatusClass = (status) => {
      const classes = {
        completed: 'status-normal',
        'in-progress': 'status-warning',
        pending: 'status-maintenance',
        failed: 'status-critical'
      }
      return classes[status] || 'status-maintenance'
    }

    const getStepStatusText = (status) => {
      const texts = {
        completed: '완료',
        'in-progress': '진행 중',
        pending: '대기',
        failed: '실패'
      }
      return texts[status] || '대기'
    }

    const getEventIconClass = (type) => {
      const classes = {
        info: 'bg-blue-100 text-blue-600',
        warning: 'bg-warning-100 text-warning-600',
        error: 'bg-danger-100 text-danger-600',
        success: 'bg-success-100 text-success-600'
      }
      return classes[type] || 'bg-secondary-100 text-secondary-600'
    }

    const getEventIcon = (type) => {
      const icons = {
        info: InformationCircleIcon,
        warning: ExclamationTriangleIcon,
        error: XCircleIcon,
        success: CheckCircleIcon
      }
      return icons[type] || InformationCircleIcon
    }

    const formatDateTime = (dateString) => {
      return format(new Date(dateString), 'MM.dd HH:mm', { locale: ko })
    }

    onMounted(() => {
      // 실제 구현에서는 API를 통해 랏 데이터를 로드
    })

    return {
      lotData,
      goBack,
      getLotStatusClass,
      getLotStatusText,
      getPriorityClass,
      getPriorityText,
      getProgressColor,
      getStepClass,
      getStepIconClass,
      getStepIcon,
      getStepStatusClass,
      getStepStatusText,
      getEventIconClass,
      getEventIcon,
      formatDateTime
    }
  }
}
</script>
