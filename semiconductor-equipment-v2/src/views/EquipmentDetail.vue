<template>
  <div class="space-y-6 animate-fade-in-up">
    <!-- 페이지 헤더 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div>
        <div class="flex items-center space-x-2 mb-2">
          <router-link to="/equipment" class="text-blue-600 hover:text-blue-700">
            <ArrowLeftIcon class="w-5 h-5" />
          </router-link>
          <h1 class="text-2xl font-bold gradient-text">{{ equipmentData.name }} 상세 정보</h1>
        </div>
        <p class="mt-1 text-secondary-600">설비 상태 및 센서 데이터 모니터링</p>
      </div>
      <div class="mt-4 sm:mt-0 flex space-x-3">
        <router-link :to="`/equipment/${equipmentId}/lots`" class="btn-secondary">
          <ClipboardDocumentListIcon class="w-4 h-4 mr-2" />
          랏 이력
        </router-link>
        <button class="btn-primary">
          <WrenchScrewdriverIcon class="w-4 h-4 mr-2" />
          정비 예약
        </button>
      </div>
    </div>

    <!-- 설비 상태 요약 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 rounded-lg" :class="getStatusIconClass(equipmentData.status)">
            <component :is="getStatusIcon(equipmentData.status)" class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">현재 상태</p>
            <span class="status-badge" :class="getStatusClass(equipmentData.status)">
              {{ getStatusText(equipmentData.status) }}
            </span>
          </div>
        </div>
      </div>
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg">
            <ChartBarIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">가동률</p>
            <div class="flex items-center">
              <p class="text-2xl font-semibold text-secondary-900">{{ equipmentData.efficiency }}%</p>
            </div>
          </div>
        </div>
      </div>
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-success-500 to-success-600 rounded-lg">
            <ClockIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">가동 시간</p>
            <p class="text-2xl font-semibold text-secondary-900">{{ equipmentData.uptime }}</p>
          </div>
        </div>
      </div>
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-warning-500 to-warning-600 rounded-lg">
            <ExclamationTriangleIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">예측 위험도</p>
            <span class="status-badge" :class="getPredictionClass(equipmentData.prediction)">
              {{ getPredictionText(equipmentData.prediction) }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 실시간 센서 데이터 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div class="card">
        <div class="card-header">
          <h3 class="text-lg font-semibold">실시간 센서 데이터</h3>
        </div>
        <div class="space-y-4">
          <div v-for="sensor in equipmentData.sensors" :key="sensor.name" 
               class="flex items-center justify-between p-3 rounded-lg border border-secondary-200">
            <div class="flex items-center">
              <div class="w-3 h-3 rounded-full mr-3" :class="getSensorStatusClass(sensor.status)"></div>
              <div>
                <p class="text-sm font-medium text-secondary-900">{{ sensor.name }}</p>
                <p class="text-xs text-secondary-500">{{ sensor.description }}</p>
              </div>
            </div>
            <div class="text-right">
              <p class="text-lg font-semibold text-secondary-900">{{ sensor.value }}</p>
              <p class="text-xs text-secondary-500">{{ sensor.unit }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <h3 class="text-lg font-semibold">센서 트렌드</h3>
        </div>
        <div class="h-64 flex items-center justify-center bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg">
          <div class="text-center">
            <ChartBarIcon class="w-16 h-16 text-blue-400 mx-auto mb-2" />
            <p class="text-blue-600 font-medium">실시간 센서 차트 (Chart.js)</p>
            <p class="text-sm text-blue-500 mt-1">온도, 압력, 유량 트렌드</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 설비 정보 및 사양 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div class="card">
        <div class="card-header">
          <h3 class="text-lg font-semibold">설비 정보</h3>
        </div>
        <div class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">설비명</label>
              <p class="text-sm text-secondary-900">{{ equipmentData.name }}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">타입</label>
              <p class="text-sm text-secondary-900">{{ equipmentData.type }}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">제조사</label>
              <p class="text-sm text-secondary-900">{{ equipmentData.manufacturer }}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">모델</label>
              <p class="text-sm text-secondary-900">{{ equipmentData.model }}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">설치일</label>
              <p class="text-sm text-secondary-900">{{ equipmentData.installDate }}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">위치</label>
              <p class="text-sm text-secondary-900">{{ equipmentData.location }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <h3 class="text-lg font-semibold">기술 사양</h3>
        </div>
        <div class="space-y-3">
          <div v-for="spec in equipmentData.specifications" :key="spec.name" 
               class="flex items-center justify-between">
            <span class="text-sm text-secondary-600">{{ spec.name }}</span>
            <span class="text-sm font-medium text-secondary-900">{{ spec.value }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 성능 분석 -->
    <div class="card">
      <div class="card-header">
        <h3 class="text-lg font-semibold">성능 분석</h3>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div class="text-center p-4 bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg">
          <p class="text-2xl font-bold text-blue-900 mb-2">{{ equipmentData.performance.throughput }}</p>
          <p class="text-sm text-blue-700">처리량 (웨이퍼/시간)</p>
          <p class="text-xs text-blue-600 mt-2">전주 대비 +3%</p>
        </div>
        <div class="text-center p-4 bg-gradient-to-br from-success-50 to-success-100 rounded-lg">
          <p class="text-2xl font-bold text-success-900 mb-2">{{ equipmentData.performance.yield }}%</p>
          <p class="text-sm text-success-700">평균 수율</p>
          <p class="text-xs text-success-600 mt-2">전주 대비 +1%</p>
        </div>
        <div class="text-center p-4 bg-gradient-to-br from-warning-50 to-warning-100 rounded-lg">
          <p class="text-2xl font-bold text-warning-900 mb-2">{{ equipmentData.performance.downtime }}</p>
          <p class="text-sm text-warning-700">다운타임 (시간)</p>
          <p class="text-xs text-warning-600 mt-2">전주 대비 -2시간</p>
        </div>
      </div>
    </div>

    <!-- 정비 이력 -->
    <div class="card">
      <div class="card-header">
        <h3 class="text-lg font-semibold">최근 정비 이력</h3>
      </div>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-secondary-200">
          <thead class="table-header">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">날짜</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">타입</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">작업 내용</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">담당자</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">소요 시간</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">상태</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-secondary-200">
            <tr v-for="maintenance in equipmentData.maintenanceHistory" :key="maintenance.id" class="table-row">
              <td class="px-6 py-4 whitespace-nowrap text-sm text-secondary-900">
                {{ formatDate(maintenance.date) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                      :class="getMaintenanceTypeClass(maintenance.type)">
                  {{ getMaintenanceTypeText(maintenance.type) }}
                </span>
              </td>
              <td class="px-6 py-4 text-sm text-secondary-900">
                {{ maintenance.description }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-secondary-900">
                {{ maintenance.technician }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-secondary-900">
                {{ maintenance.duration }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="status-badge" :class="getMaintenanceStatusClass(maintenance.status)">
                  {{ getMaintenanceStatusText(maintenance.status) }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- AI 예측 및 권장사항 -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
      <div class="card bg-gradient-to-br from-warning-50 to-warning-100 border-warning-200">
        <div class="flex items-center mb-4">
          <ExclamationTriangleIcon class="w-6 h-6 text-warning-600 mr-2" />
          <h3 class="text-lg font-semibold text-warning-800">AI 예측 분석</h3>
        </div>
        <div class="space-y-3">
          <div v-for="prediction in equipmentData.aiPredictions" :key="prediction.id" 
               class="p-3 bg-white/50 rounded-lg">
            <div class="flex items-center justify-between mb-2">
              <span class="text-sm font-medium text-warning-800">{{ prediction.title }}</span>
              <span class="text-xs text-warning-600">{{ prediction.confidence }}% 신뢰도</span>
            </div>
            <p class="text-sm text-warning-700">{{ prediction.description }}</p>
            <p class="text-xs text-warning-600 mt-1">예상 시점: {{ prediction.timeframe }}</p>
          </div>
        </div>
      </div>

      <div class="card bg-gradient-to-br from-blue-50 to-blue-100 border-blue-200">
        <div class="flex items-center mb-4">
          <LightBulbIcon class="w-6 h-6 text-blue-600 mr-2" />
          <h3 class="text-lg font-semibold text-blue-800">권장 조치사항</h3>
        </div>
        <div class="space-y-3">
          <div v-for="recommendation in equipmentData.recommendations" :key="recommendation.id" 
               class="p-3 bg-white/50 rounded-lg">
            <div class="flex items-center justify-between mb-2">
              <span class="text-sm font-medium text-blue-800">{{ recommendation.title }}</span>
              <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium"
                    :class="getPriorityClass(recommendation.priority)">
                {{ getPriorityText(recommendation.priority) }}
              </span>
            </div>
            <p class="text-sm text-blue-700">{{ recommendation.description }}</p>
            <p class="text-xs text-blue-600 mt-1">예상 효과: {{ recommendation.expectedBenefit }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { format } from 'date-fns'
import { ko } from 'date-fns/locale'
import {
  ArrowLeftIcon,
  ClipboardDocumentListIcon,
  WrenchScrewdriverIcon,
  ChartBarIcon,
  ClockIcon,
  ExclamationTriangleIcon,
  CheckCircleIcon,
  XCircleIcon,
  CogIcon,
  LightBulbIcon
} from '@heroicons/vue/24/outline'

export default {
  name: 'EquipmentDetail',
  components: {
    ArrowLeftIcon,
    ClipboardDocumentListIcon,
    WrenchScrewdriverIcon,
    ChartBarIcon,
    ClockIcon,
    ExclamationTriangleIcon,
    CheckCircleIcon,
    XCircleIcon,
    CogIcon,
    LightBulbIcon
  },
  setup() {
    const route = useRoute()
    const equipmentId = ref(route.params.id)

    const equipmentData = ref({
      name: 'CVD-001',
      type: 'CVD',
      status: 'critical',
      efficiency: 85,
      uptime: '142시간',
      prediction: 'critical',
      manufacturer: 'Applied Materials',
      model: 'Centura 5200',
      installDate: '2020.03.15',
      location: 'FAB1-B동 2층',
      sensors: [
        {
          name: '챔버 온도',
          description: '반응 챔버 내부 온도',
          value: '395',
          unit: '°C',
          status: 'warning'
        },
        {
          name: '챔버 압력',
          description: '반응 챔버 내부 압력',
          value: '2.5',
          unit: 'Torr',
          status: 'normal'
        },
        {
          name: '가스 유량',
          description: '반응 가스 유량',
          value: '150',
          unit: 'sccm',
          status: 'normal'
        },
        {
          name: 'RF 파워',
          description: '플라즈마 RF 파워',
          value: '2800',
          unit: 'W',
          status: 'critical'
        }
      ],
      specifications: [
        { name: '웨이퍼 크기', value: '300mm' },
        { name: '최대 온도', value: '450°C' },
        { name: '최대 압력', value: '10 Torr' },
        { name: '챔버 수', value: '4개' },
        { name: '처리 용량', value: '25 웨이퍼/배치' }
      ],
      performance: {
        throughput: '24',
        yield: '96.8',
        downtime: '8'
      },
      maintenanceHistory: [
        {
          id: 1,
          date: '2025-05-20',
          type: 'preventive',
          description: '정기 점검 및 부품 교체',
          technician: '이기술자',
          duration: '4시간',
          status: 'completed'
        },
        {
          id: 2,
          date: '2025-05-15',
          type: 'corrective',
          description: '온도 센서 교정',
          technician: '박엔지니어',
          duration: '2시간',
          status: 'completed'
        },
        {
          id: 3,
          date: '2025-05-10',
          type: 'upgrade',
          description: '소프트웨어 업데이트',
          technician: '김기술자',
          duration: '1시간',
          status: 'completed'
        }
      ],
      aiPredictions: [
        {
          id: 1,
          title: '온도 센서 고장 예측',
          description: '온도 센서의 드리프트 패턴이 감지되어 72시간 내 고장 가능성이 높습니다.',
          confidence: 87,
          timeframe: '72시간 내'
        },
        {
          id: 2,
          title: 'RF 파워 불안정',
          description: 'RF 파워 출력의 변동성이 증가하고 있어 성능 저하가 예상됩니다.',
          confidence: 73,
          timeframe: '5일 내'
        }
      ],
      recommendations: [
        {
          id: 1,
          title: '온도 센서 교체',
          description: '온도 센서를 예방적으로 교체하여 예상되는 고장을 방지하세요.',
          priority: 'high',
          expectedBenefit: '다운타임 8시간 절약'
        },
        {
          id: 2,
          title: 'RF 매칭 네트워크 점검',
          description: 'RF 매칭 네트워크를 점검하여 파워 안정성을 개선하세요.',
          priority: 'medium',
          expectedBenefit: '수율 2% 향상'
        }
      ]
    })

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

    const getStatusIcon = (status) => {
      const icons = {
        normal: CheckCircleIcon,
        warning: ExclamationTriangleIcon,
        critical: XCircleIcon,
        maintenance: CogIcon
      }
      return icons[status] || CheckCircleIcon
    }

    const getStatusIconClass = (status) => {
      const classes = {
        normal: 'bg-gradient-to-r from-success-500 to-success-600',
        warning: 'bg-gradient-to-r from-warning-500 to-warning-600',
        critical: 'bg-gradient-to-r from-danger-500 to-danger-600',
        maintenance: 'bg-gradient-to-r from-secondary-500 to-secondary-600'
      }
      return classes[status] || 'bg-gradient-to-r from-success-500 to-success-600'
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

    const getSensorStatusClass = (status) => {
      const classes = {
        normal: 'bg-success-500',
        warning: 'bg-warning-500',
        critical: 'bg-danger-500'
      }
      return classes[status] || 'bg-success-500'
    }

    const getMaintenanceTypeClass = (type) => {
      const classes = {
        preventive: 'bg-blue-100 text-blue-800',
        corrective: 'bg-warning-100 text-warning-800',
        upgrade: 'bg-success-100 text-success-800'
      }
      return classes[type] || 'bg-secondary-100 text-secondary-800'
    }

    const getMaintenanceTypeText = (type) => {
      const texts = {
        preventive: '예방정비',
        corrective: '수리정비',
        upgrade: '업그레이드'
      }
      return texts[type] || '기타'
    }

    const getMaintenanceStatusClass = (status) => {
      const classes = {
        completed: 'status-normal',
        'in-progress': 'status-warning',
        scheduled: 'status-maintenance'
      }
      return classes[status] || 'status-normal'
    }

    const getMaintenanceStatusText = (status) => {
      const texts = {
        completed: '완료',
        'in-progress': '진행 중',
        scheduled: '예정'
      }
      return texts[status] || '완료'
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

    const formatDate = (dateString) => {
      return format(new Date(dateString), 'yyyy.MM.dd', { locale: ko })
    }

    onMounted(() => {
      // 실제 구현에서는 API를 통해 설비 데이터를 로드
      const equipmentNames = {
        'cvd-001': 'CVD-001',
        'etch-003': 'ETCH-003',
        'cmp-002': 'CMP-002',
        'litho-001': 'LITHO-001'
      }
      
      if (equipmentNames[equipmentId.value]) {
        equipmentData.value.name = equipmentNames[equipmentId.value]
      }
    })

    return {
      equipmentId,
      equipmentData,
      getStatusClass,
      getStatusText,
      getStatusIcon,
      getStatusIconClass,
      getPredictionClass,
      getPredictionText,
      getSensorStatusClass,
      getMaintenanceTypeClass,
      getMaintenanceTypeText,
      getMaintenanceStatusClass,
      getMaintenanceStatusText,
      getPriorityClass,
      getPriorityText,
      formatDate
    }
  }
}
</script>
