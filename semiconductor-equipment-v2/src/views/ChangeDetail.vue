<template>
  <div class="space-y-6 animate-fade-in-up">
    <!-- 페이지 헤더 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div>
        <div class="flex items-center space-x-2 mb-2">
          <router-link to="/changes" class="text-blue-600 hover:text-blue-700">
            <ArrowLeftIcon class="w-5 h-5" />
          </router-link>
          <h1 class="text-2xl font-bold gradient-text">변경 상세 정보</h1>
        </div>
        <p class="mt-1 text-secondary-600">{{ changeData.title }}</p>
      </div>
      <div class="mt-4 sm:mt-0 flex space-x-3">
        <button class="btn-secondary">
          <DocumentArrowDownIcon class="w-4 h-4 mr-2" />
          리포트 다운로드
        </button>
        <button class="btn-primary">
          <PencilIcon class="w-4 h-4 mr-2" />
          편집
        </button>
      </div>
    </div>

    <!-- 변경 기본 정보 -->
    <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
      <div class="lg:col-span-2 card">
        <div class="card-header">
          <h3 class="text-lg font-semibold">기본 정보</h3>
        </div>
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">변경 ID</label>
              <p class="text-lg font-semibold text-secondary-900">{{ changeData.id }}</p>
            </div>
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">변경 타입</label>
              <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium"
                    :class="getChangeTypeClass(changeData.type)">
                {{ getChangeTypeText(changeData.type) }}
              </span>
            </div>
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">대상 설비</label>
              <router-link :to="`/equipment/${changeData.equipmentId}`" class="text-blue-600 hover:text-blue-700 font-medium">
                {{ changeData.equipment }}
              </router-link>
            </div>
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">담당자</label>
              <p class="text-lg text-secondary-900">{{ changeData.assignee }}</p>
            </div>
          </div>
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">상태</label>
              <span class="status-badge" :class="getStatusClass(changeData.status)">
                {{ getStatusText(changeData.status) }}
              </span>
            </div>
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">영향도</label>
              <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium"
                    :class="getImpactClass(changeData.impact)">
                {{ getImpactText(changeData.impact) }}
              </span>
            </div>
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">우선순위</label>
              <span class="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium"
                    :class="getPriorityClass(changeData.priority)">
                {{ getPriorityText(changeData.priority) }}
              </span>
            </div>
            <div>
              <label class="block text-sm font-medium text-secondary-700 mb-1">승인자</label>
              <p class="text-lg text-secondary-900">{{ changeData.approver }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 진행 현황 -->
      <div class="card">
        <div class="card-header">
          <h3 class="text-lg font-semibold">진행 현황</h3>
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
                  :stroke="getProgressColor(changeData.progress)"
                  stroke-width="2"
                  :stroke-dasharray="`${changeData.progress}, 100`"
                />
              </svg>
              <div class="absolute inset-0 flex items-center justify-center">
                <span class="text-2xl font-bold text-secondary-900">{{ changeData.progress }}%</span>
              </div>
            </div>
            <p class="text-sm text-secondary-600">완료율</p>
          </div>
          
          <div class="space-y-3">
            <div class="flex justify-between">
              <span class="text-sm text-secondary-600">계획 시작</span>
              <span class="text-sm font-medium text-secondary-900">{{ formatDateTime(changeData.plannedStartDate) }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-sm text-secondary-600">실제 시작</span>
              <span class="text-sm font-medium text-secondary-900">{{ formatDateTime(changeData.actualStartDate) }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-sm text-secondary-600">계획 완료</span>
              <span class="text-sm font-medium text-secondary-900">{{ formatDateTime(changeData.plannedEndDate) }}</span>
            </div>
            <div class="flex justify-between">
              <span class="text-sm text-secondary-600">실제 완료</span>
              <span class="text-sm font-medium text-secondary-900">
                {{ changeData.actualEndDate ? formatDateTime(changeData.actualEndDate) : '-' }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 변경 설명 -->
    <div class="card">
      <div class="card-header">
        <h3 class="text-lg font-semibold">변경 설명</h3>
      </div>
      <div class="prose max-w-none">
        <p class="text-secondary-700">{{ changeData.description }}</p>
        
        <div class="mt-6 grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <h4 class="text-sm font-semibold text-secondary-900 mb-2">변경 사유</h4>
            <p class="text-sm text-secondary-700">{{ changeData.reason }}</p>
          </div>
          <div>
            <h4 class="text-sm font-semibold text-secondary-900 mb-2">예상 효과</h4>
            <p class="text-sm text-secondary-700">{{ changeData.expectedBenefit }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 변경 전후 비교 -->
    <div class="card">
      <div class="card-header">
        <h3 class="text-lg font-semibold">변경 전후 비교</h3>
      </div>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <h4 class="text-sm font-semibold text-secondary-900 mb-3 text-danger-700">변경 전</h4>
          <div class="bg-danger-50 border border-danger-200 rounded-lg p-4">
            <div class="space-y-3">
              <div v-for="item in changeData.beforeState" :key="item.parameter" class="flex justify-between">
                <span class="text-sm text-secondary-600">{{ item.parameter }}</span>
                <span class="text-sm font-medium text-secondary-900">{{ item.value }}</span>
              </div>
            </div>
          </div>
        </div>
        <div>
          <h4 class="text-sm font-semibold text-secondary-900 mb-3 text-success-700">변경 후</h4>
          <div class="bg-success-50 border border-success-200 rounded-lg p-4">
            <div class="space-y-3">
              <div v-for="item in changeData.afterState" :key="item.parameter" class="flex justify-between">
                <span class="text-sm text-secondary-600">{{ item.parameter }}</span>
                <span class="text-sm font-medium text-secondary-900">{{ item.value }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 체크리스트 -->
    <div class="card">
      <div class="card-header">
        <h3 class="text-lg font-semibold">작업 체크리스트</h3>
      </div>
      <div class="space-y-3">
        <div v-for="(item, index) in changeData.checklist" :key="index" 
             class="flex items-center p-3 rounded-lg border border-secondary-200 hover:bg-secondary-50 transition-colors">
          <div class="flex-shrink-0 mr-3">
            <div class="w-6 h-6 rounded-full flex items-center justify-center"
                 :class="item.completed ? 'bg-success-500' : 'bg-secondary-300'">
              <CheckIcon v-if="item.completed" class="w-4 h-4 text-white" />
            </div>
          </div>
          <div class="flex-1">
            <p class="text-sm font-medium text-secondary-900" :class="{ 'line-through text-secondary-500': item.completed }">
              {{ item.task }}
            </p>
            <p class="text-xs text-secondary-500 mt-1">{{ item.description }}</p>
          </div>
          <div class="flex-shrink-0 ml-3">
            <span class="text-xs text-secondary-500">{{ item.assignee }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 영향 분석 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <div class="card">
        <div class="card-header">
          <h3 class="text-lg font-semibold">영향 분석</h3>
        </div>
        <div class="space-y-4">
          <div v-for="impact in changeData.impactAnalysis" :key="impact.category" 
               class="p-3 rounded-lg border border-secondary-200">
            <div class="flex items-center justify-between mb-2">
              <span class="text-sm font-medium text-secondary-900">{{ impact.category }}</span>
              <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium"
                    :class="getImpactLevelClass(impact.level)">
                {{ getImpactLevelText(impact.level) }}
              </span>
            </div>
            <p class="text-sm text-secondary-700">{{ impact.description }}</p>
          </div>
        </div>
      </div>

      <div class="card">
        <div class="card-header">
          <h3 class="text-lg font-semibold">위험 요소</h3>
        </div>
        <div class="space-y-3">
          <div v-for="risk in changeData.risks" :key="risk.id" 
               class="p-3 rounded-lg border border-secondary-200">
            <div class="flex items-center justify-between mb-2">
              <span class="text-sm font-medium text-secondary-900">{{ risk.title }}</span>
              <span class="inline-flex items-center px-2 py-1 rounded-full text-xs font-medium"
                    :class="getRiskLevelClass(risk.level)">
                {{ getRiskLevelText(risk.level) }}
              </span>
            </div>
            <p class="text-sm text-secondary-700 mb-2">{{ risk.description }}</p>
            <p class="text-xs text-secondary-600">대응방안: {{ risk.mitigation }}</p>
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
        <div v-for="file in changeData.attachments" :key="file.id" 
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

    <!-- 변경 이력 -->
    <div class="card">
      <div class="card-header">
        <h3 class="text-lg font-semibold">변경 이력</h3>
      </div>
      <div class="space-y-3">
        <div v-for="log in changeData.changeLog" :key="log.id" 
             class="flex items-start p-3 rounded-lg border border-secondary-200 hover:bg-secondary-50 transition-colors">
          <div class="flex-shrink-0 mr-3">
            <div class="w-8 h-8 rounded-full flex items-center justify-center"
                 :class="getLogTypeClass(log.type)">
              <component :is="getLogIcon(log.type)" class="w-4 h-4" />
            </div>
          </div>
          <div class="flex-1 min-w-0">
            <div class="flex items-center justify-between">
              <p class="text-sm font-medium text-secondary-900">{{ log.action }}</p>
              <span class="text-xs text-secondary-500">{{ formatDateTime(log.timestamp) }}</span>
            </div>
            <p class="text-sm text-secondary-700 mt-1">{{ log.description }}</p>
            <p class="text-xs text-secondary-500 mt-1">{{ log.user }}</p>
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
  DocumentArrowDownIcon,
  PencilIcon,
  CheckIcon,
  DocumentIcon,
  ArrowDownTrayIcon,
  InformationCircleIcon,
  ExclamationTriangleIcon,
  CheckCircleIcon,
  XCircleIcon
} from '@heroicons/vue/24/outline'

export default {
  name: 'ChangeDetail',
  components: {
    ArrowLeftIcon,
    DocumentArrowDownIcon,
    PencilIcon,
    CheckIcon,
    DocumentIcon,
    ArrowDownTrayIcon,
    InformationCircleIcon,
    ExclamationTriangleIcon,
    CheckCircleIcon,
    XCircleIcon
  },
  setup() {
    const route = useRoute()
    const changeId = ref(route.params.id)

    const changeData = ref({
      id: 'CHANGE-001',
      title: 'CVD-001 온도 제어 파라미터 조정',
      type: 'configuration',
      equipment: 'CVD-001',
      equipmentId: 'cvd-001',
      assignee: '김엔지니어',
      approver: '박팀장',
      status: 'completed',
      impact: 'medium',
      priority: 'high',
      progress: 100,
      plannedStartDate: '2025-05-23T09:00:00',
      actualStartDate: '2025-05-23T09:00:00',
      plannedEndDate: '2025-05-23T10:30:00',
      actualEndDate: '2025-05-23T10:30:00',
      description: '공정 안정성 향상을 위한 온도 제어 파라미터 최적화 작업입니다. 기존 온도 설정값에서 발생하는 변동성을 줄이고, 더 안정적인 공정 환경을 구축하기 위해 진행됩니다.',
      reason: '최근 온도 변동으로 인한 수율 저하 문제 해결 필요',
      expectedBenefit: '수율 2-3% 향상 및 공정 안정성 개선 예상',
      beforeState: [
        { parameter: '챔버 온도', value: '380°C' },
        { parameter: '온도 허용 범위', value: '±5°C' },
        { parameter: '가열 속도', value: '10°C/min' },
        { parameter: '냉각 속도', value: '8°C/min' }
      ],
      afterState: [
        { parameter: '챔버 온도', value: '385°C' },
        { parameter: '온도 허용 범위', value: '±3°C' },
        { parameter: '가열 속도', value: '8°C/min' },
        { parameter: '냉각 속도', value: '6°C/min' }
      ],
      checklist: [
        {
          task: '변경 계획서 작성 및 승인',
          description: '상세 변경 계획 문서화',
          assignee: '김엔지니어',
          completed: true
        },
        {
          task: '백업 설정 저장',
          description: '기존 파라미터 백업',
          assignee: '김엔지니어',
          completed: true
        },
        {
          task: '파라미터 변경 적용',
          description: '새로운 온도 설정 적용',
          assignee: '김엔지니어',
          completed: true
        },
        {
          task: '테스트 런 실행',
          description: '변경 후 테스트 웨이퍼 처리',
          assignee: '이기술자',
          completed: true
        },
        {
          task: '결과 검증 및 문서화',
          description: '변경 효과 검증 및 보고서 작성',
          assignee: '김엔지니어',
          completed: true
        }
      ],
      impactAnalysis: [
        {
          category: '생산성',
          level: 'positive',
          description: '수율 향상으로 인한 생산성 개선 예상'
        },
        {
          category: '품질',
          level: 'positive',
          description: '온도 안정성 향상으로 품질 개선'
        },
        {
          category: '비용',
          level: 'neutral',
          description: '추가 비용 발생 없음'
        },
        {
          category: '일정',
          level: 'neutral',
          description: '생산 일정에 미미한 영향'
        }
      ],
      risks: [
        {
          id: 1,
          title: '온도 오버슈트',
          level: 'medium',
          description: '새로운 설정에서 온도 오버슈트 발생 가능성',
          mitigation: '단계적 온도 상승 및 모니터링 강화'
        },
        {
          id: 2,
          title: '설정 복원 실패',
          level: 'low',
          description: '문제 발생 시 기존 설정 복원 실패 위험',
          mitigation: '백업 설정 다중 저장 및 복원 절차 준비'
        }
      ],
      attachments: [
        {
          id: 1,
          name: 'Change_Plan_CVD001.pdf',
          size: '1.2 MB',
          uploadedAt: '2025-05-23T08:30:00'
        },
        {
          id: 2,
          name: 'Temperature_Analysis.xlsx',
          size: '856 KB',
          uploadedAt: '2025-05-23T09:15:00'
        },
        {
          id: 3,
          name: 'Test_Results.pdf',
          size: '2.1 MB',
          uploadedAt: '2025-05-23T10:45:00'
        }
      ],
      changeLog: [
        {
          id: 1,
          type: 'info',
          action: '변경 요청 생성',
          description: '온도 제어 파라미터 변경 요청이 생성되었습니다.',
          user: '김엔지니어',
          timestamp: '2025-05-22T14:00:00'
        },
        {
          id: 2,
          type: 'success',
          action: '변경 승인',
          description: '팀장 승인이 완료되었습니다.',
          user: '박팀장',
          timestamp: '2025-05-22T16:30:00'
        },
        {
          id: 3,
          type: 'info',
          action: '변경 시작',
          description: '파라미터 변경 작업이 시작되었습니다.',
          user: '김엔지니어',
          timestamp: '2025-05-23T09:00:00'
        },
        {
          id: 4,
          type: 'success',
          action: '변경 완료',
          description: '모든 변경 작업이 성공적으로 완료되었습니다.',
          user: '김엔지니어',
          timestamp: '2025-05-23T10:30:00'
        }
      ]
    })

    const getChangeTypeClass = (type) => {
      const classes = {
        configuration: 'bg-blue-100 text-blue-800',
        maintenance: 'bg-warning-100 text-warning-800',
        upgrade: 'bg-success-100 text-success-800',
        repair: 'bg-danger-100 text-danger-800'
      }
      return classes[type] || 'bg-secondary-100 text-secondary-800'
    }

    const getChangeTypeText = (type) => {
      const texts = {
        configuration: '설정 변경',
        maintenance: '정비 작업',
        upgrade: '업그레이드',
        repair: '수리'
      }
      return texts[type] || '기타'
    }

    const getStatusClass = (status) => {
      const classes = {
        completed: 'status-normal',
        'in-progress': 'status-warning',
        scheduled: 'status-maintenance',
        cancelled: 'status-critical'
      }
      return classes[status] || 'status-normal'
    }

    const getStatusText = (status) => {
      const texts = {
        completed: '완료',
        'in-progress': '진행 중',
        scheduled: '예정',
        cancelled: '취소'
      }
      return texts[status] || '알 수 없음'
    }

    const getImpactClass = (impact) => {
      const classes = {
        high: 'bg-danger-100 text-danger-800',
        medium: 'bg-warning-100 text-warning-800',
        low: 'bg-success-100 text-success-800'
      }
      return classes[impact] || 'bg-secondary-100 text-secondary-800'
    }

    const getImpactText = (impact) => {
      const texts = {
        high: '높은 영향',
        medium: '보통 영향',
        low: '낮은 영향'
      }
      return texts[impact] || '보통'
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

    const getImpactLevelClass = (level) => {
      const classes = {
        positive: 'bg-success-100 text-success-800',
        neutral: 'bg-secondary-100 text-secondary-800',
        negative: 'bg-danger-100 text-danger-800'
      }
      return classes[level] || 'bg-secondary-100 text-secondary-800'
    }

    const getImpactLevelText = (level) => {
      const texts = {
        positive: '긍정적',
        neutral: '중립적',
        negative: '부정적'
      }
      return texts[level] || '중립적'
    }

    const getRiskLevelClass = (level) => {
      const classes = {
        high: 'bg-danger-100 text-danger-800',
        medium: 'bg-warning-100 text-warning-800',
        low: 'bg-success-100 text-success-800'
      }
      return classes[level] || 'bg-secondary-100 text-secondary-800'
    }

    const getRiskLevelText = (level) => {
      const texts = {
        high: '높음',
        medium: '보통',
        low: '낮음'
      }
      return texts[level] || '보통'
    }

    const getLogTypeClass = (type) => {
      const classes = {
        info: 'bg-blue-100 text-blue-600',
        success: 'bg-success-100 text-success-600',
        warning: 'bg-warning-100 text-warning-600',
        error: 'bg-danger-100 text-danger-600'
      }
      return classes[type] || 'bg-secondary-100 text-secondary-600'
    }

    const getLogIcon = (type) => {
      const icons = {
        info: InformationCircleIcon,
        success: CheckCircleIcon,
        warning: ExclamationTriangleIcon,
        error: XCircleIcon
      }
      return icons[type] || InformationCircleIcon
    }

    const formatDateTime = (dateString) => {
      return format(new Date(dateString), 'MM.dd HH:mm', { locale: ko })
    }

    onMounted(() => {
      // 실제 구현에서는 API를 통해 변경 데이터를 로드
    })

    return {
      changeData,
      getChangeTypeClass,
      getChangeTypeText,
      getStatusClass,
      getStatusText,
      getImpactClass,
      getImpactText,
      getPriorityClass,
      getPriorityText,
      getProgressColor,
      getImpactLevelClass,
      getImpactLevelText,
      getRiskLevelClass,
      getRiskLevelText,
      getLogTypeClass,
      getLogIcon,
      formatDateTime
    }
  }
}
</script>
