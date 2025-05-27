<template>
  <div class="space-y-6">
    <!-- 페이지 헤더 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div class="flex items-center space-x-4">
        <button @click="$router.go(-1)" class="p-2 text-gray-400 hover:text-gray-600">
          <ArrowLeftIcon class="w-5 h-5" />
        </button>
        <div>
          <h1 class="text-2xl font-bold text-gray-900">변경점 상세</h1>
          <p class="mt-1 text-sm text-gray-500">{{ change?.equipment }} - {{ change?.title }}</p>
        </div>
      </div>
      <div class="mt-4 sm:mt-0 flex space-x-3">
        <button class="btn-secondary">
          <DocumentDuplicateIcon class="w-4 h-4 mr-2" />
          복사
        </button>
        <button class="btn-primary">
          <PencilIcon class="w-4 h-4 mr-2" />
          편집
        </button>
      </div>
    </div>

    <!-- 변경점 기본 정보 -->
    <div class="card">
      <div class="flex items-center justify-between mb-6">
        <h3 class="text-lg font-medium text-gray-900">기본 정보</h3>
        <span class="status-badge" :class="getStatusClass(change?.status)">
          {{ getStatusText(change?.status) }}
        </span>
      </div>
      
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-500">변경 ID</label>
            <p class="mt-1 text-sm text-gray-900">CHG-{{ String(change?.id).padStart(4, '0') }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-500">설비명</label>
            <p class="mt-1 text-sm text-gray-900">{{ change?.equipment }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-500">변경 유형</label>
            <div class="mt-1 flex items-center space-x-2">
              <component :is="getChangeTypeIcon(change?.type)" class="h-4 w-4" :class="getChangeTypeIconColor(change?.type)" />
              <span class="text-sm text-gray-900">{{ getChangeTypeText(change?.type) }}</span>
            </div>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-500">우선순위</label>
            <span class="mt-1 status-badge" :class="getPriorityClass(change?.priority)">
              {{ getPriorityText(change?.priority) }}
            </span>
          </div>
        </div>
        
        <div class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-500">요청자</label>
            <p class="mt-1 text-sm text-gray-900">{{ change?.author }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-500">승인자</label>
            <p class="mt-1 text-sm text-gray-900">{{ change?.approver }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-500">요청일시</label>
            <p class="mt-1 text-sm text-gray-900">{{ formatDateTime(change?.requestDate) }}</p>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-500">완료일시</label>
            <p class="mt-1 text-sm text-gray-900">
              {{ change?.completedDate ? formatDateTime(change.completedDate) : '-' }}
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- 변경 내용 -->
    <div class="card">
      <h3 class="text-lg font-medium text-gray-900 mb-4">변경 내용</h3>
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-500 mb-2">제목</label>
          <p class="text-sm text-gray-900">{{ change?.title }}</p>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-500 mb-2">상세 설명</label>
          <p class="text-sm text-gray-900 whitespace-pre-line">{{ change?.description }}</p>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-500 mb-2">변경 사유</label>
          <p class="text-sm text-gray-900">{{ change?.reason }}</p>
        </div>
      </div>
    </div>

    <!-- 변경 전후 비교 -->
    <div class="card" v-if="change?.beforeAfter">
      <h3 class="text-lg font-medium text-gray-900 mb-4">변경 전후 비교</h3>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div>
          <h4 class="text-sm font-medium text-gray-700 mb-3 flex items-center">
            <XMarkIcon class="h-4 w-4 text-danger-500 mr-2" />
            변경 전
          </h4>
          <div class="bg-danger-50 border border-danger-200 rounded-lg p-4">
            <pre class="text-sm text-gray-900 whitespace-pre-wrap">{{ change.beforeAfter.before }}</pre>
          </div>
        </div>
        <div>
          <h4 class="text-sm font-medium text-gray-700 mb-3 flex items-center">
            <CheckIcon class="h-4 w-4 text-success-500 mr-2" />
            변경 후
          </h4>
          <div class="bg-success-50 border border-success-200 rounded-lg p-4">
            <pre class="text-sm text-gray-900 whitespace-pre-wrap">{{ change.beforeAfter.after }}</pre>
          </div>
        </div>
      </div>
    </div>

    <!-- 영향 분석 -->
    <div class="card">
      <h3 class="text-lg font-medium text-gray-900 mb-4">영향 분석</h3>
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div>
          <h4 class="text-sm font-medium text-gray-700 mb-3">영향 범위</h4>
          <div class="space-y-2">
            <div v-for="scope in change?.impact?.scope" :key="scope" class="flex items-center space-x-2">
              <div class="h-2 w-2 bg-primary-400 rounded-full"></div>
              <span class="text-sm text-gray-900">{{ scope }}</span>
            </div>
          </div>
        </div>
        <div>
          <h4 class="text-sm font-medium text-gray-700 mb-3">위험도</h4>
          <span class="status-badge" :class="getRiskClass(change?.impact?.risk)">
            {{ getRiskText(change?.impact?.risk) }}
          </span>
          <p class="text-sm text-gray-600 mt-2">{{ change?.impact?.riskDescription }}</p>
        </div>
        <div>
          <h4 class="text-sm font-medium text-gray-700 mb-3">예상 다운타임</h4>
          <p class="text-lg font-semibold text-gray-900">{{ change?.impact?.downtime }}</p>
          <p class="text-sm text-gray-600">{{ change?.impact?.downtimeDescription }}</p>
        </div>
      </div>
    </div>

    <!-- 체크리스트 -->
    <div class="card">
      <h3 class="text-lg font-medium text-gray-900 mb-4">작업 체크리스트</h3>
      <div class="space-y-3">
        <div v-for="(item, index) in change?.checklist" :key="index" class="flex items-start space-x-3">
          <div class="flex-shrink-0 mt-1">
            <CheckCircleIcon v-if="item.completed" class="h-5 w-5 text-success-500" />
            <div v-else class="h-5 w-5 border-2 border-gray-300 rounded-full"></div>
          </div>
          <div class="flex-1">
            <p class="text-sm font-medium text-gray-900" :class="{ 'line-through text-gray-500': item.completed }">
              {{ item.task }}
            </p>
            <p class="text-xs text-gray-500">{{ item.description }}</p>
            <div class="flex items-center space-x-4 mt-1">
              <span class="text-xs text-gray-500">담당자: {{ item.assignee }}</span>
              <span v-if="item.completedAt" class="text-xs text-gray-500">
                완료: {{ formatDateTime(item.completedAt) }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 첨부 파일 -->
    <div class="card" v-if="change?.attachments?.length">
      <h3 class="text-lg font-medium text-gray-900 mb-4">첨부 파일</h3>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div v-for="file in change.attachments" :key="file.id" class="border border-gray-200 rounded-lg p-4 hover:bg-gray-50">
          <div class="flex items-center space-x-3">
            <DocumentIcon class="h-8 w-8 text-gray-400" />
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-gray-900 truncate">{{ file.name }}</p>
              <p class="text-xs text-gray-500">{{ file.size }} • {{ formatDateTime(file.uploadedAt) }}</p>
            </div>
            <button class="text-primary-600 hover:text-primary-500">
              <ArrowDownTrayIcon class="h-4 w-4" />
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 변경 이력 -->
    <div class="card">
      <h3 class="text-lg font-medium text-gray-900 mb-4">변경 이력</h3>
      <div class="flow-root">
        <ul class="-mb-8">
          <li v-for="(history, index) in change?.history" :key="index">
            <div class="relative pb-8">
              <span v-if="index !== change.history.length - 1" class="absolute top-4 left-4 -ml-px h-full w-0.5 bg-gray-200"></span>
              <div class="relative flex space-x-3">
                <div>
                  <span class="h-8 w-8 rounded-full bg-gray-400 flex items-center justify-center ring-8 ring-white">
                    <component :is="getHistoryIcon(history.action)" class="h-4 w-4 text-white" />
                  </span>
                </div>
                <div class="flex min-w-0 flex-1 justify-between space-x-4 pt-1.5">
                  <div>
                    <p class="text-sm text-gray-500">
                      <span class="font-medium text-gray-900">{{ history.user }}</span>가 
                      <span class="font-medium">{{ getHistoryActionText(history.action) }}</span>
                    </p>
                    <p class="mt-1 text-sm text-gray-600">{{ history.comment }}</p>
                  </div>
                  <div class="whitespace-nowrap text-right text-sm text-gray-500">
                    <time>{{ formatDateTime(history.timestamp) }}</time>
                  </div>
                </div>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { format } from 'date-fns'
import { ko } from 'date-fns/locale'
import {
  ArrowLeftIcon,
  DocumentDuplicateIcon,
  PencilIcon,
  CheckCircleIcon,
  XMarkIcon,
  CheckIcon,
  DocumentIcon,
  ArrowDownTrayIcon,
  ClockIcon,
  WrenchScrewdriverIcon,
  Cog6ToothIcon,
  ExclamationTriangleIcon,
  ArrowUpIcon,
  CpuChipIcon,
  UserIcon,
  EyeIcon
} from '@heroicons/vue/24/outline'

const route = useRoute()
const changeId = route.params.id

// 변경점 상세 데이터 (실제로는 API에서 가져올 데이터)
const change = ref({
  id: 1,
  equipment: 'CVD-001',
  type: 'maintenance',
  title: '온도 센서 교체',
  description: '기존 온도 센서의 정확도 저하로 인한 신규 센서 교체 작업\n\n작업 내용:\n1. 기존 센서 제거\n2. 신규 센서 설치\n3. 캘리브레이션 수행\n4. 동작 테스트',
  reason: '온도 센서의 측정 정확도가 ±5°C에서 ±15°C로 저하되어 공정 품질에 영향을 미치고 있음',
  author: '김정비',
  approver: '이관리',
  status: 'completed',
  priority: 'high',
  requestDate: new Date(Date.now() - 48 * 60 * 60 * 1000),
  completedDate: new Date(Date.now() - 2 * 60 * 60 * 1000),
  beforeAfter: {
    before: `온도 센서: TempSensor-V1.2
측정 범위: 0-1000°C
정확도: ±15°C
상태: 불량`,
    after: `온도 센서: TempSensor-V2.0
측정 범위: 0-1200°C
정확도: ±2°C
상태: 정상`
  },
  impact: {
    scope: ['CVD-001 설비', '라인 A 전체', '품질 관리 시스템'],
    risk: 'medium',
    riskDescription: '작업 중 일시적 생산 중단 가능성',
    downtime: '2시간',
    downtimeDescription: '센서 교체 및 캘리브레이션 시간'
  },
  checklist: [
    {
      task: '작업 전 안전 점검',
      description: '전원 차단 및 안전 장비 착용 확인',
      assignee: '김정비',
      completed: true,
      completedAt: new Date(Date.now() - 4 * 60 * 60 * 1000)
    },
    {
      task: '기존 센서 제거',
      description: '기존 온도 센서 분리 및 배선 정리',
      assignee: '김정비',
      completed: true,
      completedAt: new Date(Date.now() - 3.5 * 60 * 60 * 1000)
    },
    {
      task: '신규 센서 설치',
      description: '새로운 온도 센서 설치 및 배선 연결',
      assignee: '김정비',
      completed: true,
      completedAt: new Date(Date.now() - 3 * 60 * 60 * 1000)
    },
    {
      task: '캘리브레이션 수행',
      description: '센서 정확도 보정 및 설정값 조정',
      assignee: '박캘리',
      completed: true,
      completedAt: new Date(Date.now() - 2.5 * 60 * 60 * 1000)
    },
    {
      task: '동작 테스트',
      description: '전체 시스템 동작 확인 및 성능 검증',
      assignee: '이테스트',
      completed: true,
      completedAt: new Date(Date.now() - 2 * 60 * 60 * 1000)
    }
  ],
  attachments: [
    {
      id: 1,
      name: '센서_교체_매뉴얼.pdf',
      size: '2.3MB',
      uploadedAt: new Date(Date.now() - 48 * 60 * 60 * 1000)
    },
    {
      id: 2,
      name: '작업_전_사진.jpg',
      size: '1.8MB',
      uploadedAt: new Date(Date.now() - 4 * 60 * 60 * 1000)
    },
    {
      id: 3,
      name: '작업_후_사진.jpg',
      size: '2.1MB',
      uploadedAt: new Date(Date.now() - 2 * 60 * 60 * 1000)
    }
  ],
  history: [
    {
      action: 'created',
      user: '김정비',
      comment: '온도 센서 교체 요청 생성',
      timestamp: new Date(Date.now() - 48 * 60 * 60 * 1000)
    },
    {
      action: 'approved',
      user: '이관리',
      comment: '작업 승인 - 우선순위 높음으로 설정',
      timestamp: new Date(Date.now() - 46 * 60 * 60 * 1000)
    },
    {
      action: 'started',
      user: '김정비',
      comment: '작업 시작',
      timestamp: new Date(Date.now() - 4 * 60 * 60 * 1000)
    },
    {
      action: 'completed',
      user: '김정비',
      comment: '센서 교체 작업 완료 - 정상 동작 확인',
      timestamp: new Date(Date.now() - 2 * 60 * 60 * 1000)
    }
  ]
})

// 헬퍼 함수들
const getStatusClass = (status) => {
  const classes = {
    completed: 'status-normal',
    in_progress: 'status-warning',
    pending: 'status-maintenance',
    cancelled: 'status-critical'
  }
  return classes[status] || 'status-normal'
}

const getStatusText = (status) => {
  const texts = {
    completed: '완료',
    in_progress: '진행중',
    pending: '대기',
    cancelled: '취소'
  }
  return texts[status] || '완료'
}

const getChangeTypeIcon = (type) => {
  const icons = {
    configuration: Cog6ToothIcon,
    maintenance: WrenchScrewdriverIcon,
    upgrade: ArrowUpIcon,
    repair: ExclamationTriangleIcon
  }
  return icons[type] || CpuChipIcon
}

const getChangeTypeIconColor = (type) => {
  const colors = {
    configuration: 'text-primary-600',
    maintenance: 'text-success-600',
    upgrade: 'text-warning-600',
    repair: 'text-danger-600'
  }
  return colors[type] || 'text-gray-600'
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

const getPriorityClass = (priority) => {
  const classes = {
    low: 'status-normal',
    medium: 'status-warning',
    high: 'status-critical'
  }
  return classes[priority] || 'status-normal'
}

const getPriorityText = (priority) => {
  const texts = {
    low: '낮음',
    medium: '보통',
    high: '높음'
  }
  return texts[priority] || '보통'
}

const getRiskClass = (risk) => {
  const classes = {
    low: 'status-normal',
    medium: 'status-warning',
    high: 'status-critical'
  }
  return classes[risk] || 'status-normal'
}

const getRiskText = (risk) => {
  const texts = {
    low: '낮음',
    medium: '보통',
    high: '높음'
  }
  return texts[risk] || '보통'
}

const getHistoryIcon = (action) => {
  const icons = {
    created: PencilIcon,
    approved: CheckIcon,
    started: ClockIcon,
    completed: CheckCircleIcon,
    cancelled: XMarkIcon,
    viewed: EyeIcon
  }
  return icons[action] || UserIcon
}

const getHistoryActionText = (action) => {
  const texts = {
    created: '생성했습니다',
    approved: '승인했습니다',
    started: '시작했습니다',
    completed: '완료했습니다',
    cancelled: '취소했습니다',
    viewed: '조회했습니다'
  }
  return texts[action] || '작업했습니다'
}

const formatDateTime = (date) => {
  return format(date, 'yyyy.MM.dd HH:mm', { locale: ko })
}
</script>
