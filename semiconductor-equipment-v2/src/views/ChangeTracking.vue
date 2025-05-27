<template>
  <div class="space-y-6 animate-fade-in-up">
    <!-- 페이지 헤더 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h1 class="text-2xl font-bold gradient-text">변경 추적</h1>
        <p class="mt-1 text-secondary-600">설비 변경 이력 및 추적 관리</p>
      </div>
      <div class="mt-4 sm:mt-0">
        <button class="btn-primary">
          <PlusIcon class="w-4 h-4 mr-2" />
          변경 기록 추가
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
              placeholder="변경 내용 검색..."
              class="input-field pl-10"
            />
          </div>
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
          <label class="block text-sm font-medium text-secondary-700 mb-2">변경 타입</label>
          <select v-model="typeFilter" class="select-field">
            <option value="">전체</option>
            <option value="configuration">설정 변경</option>
            <option value="maintenance">정비 작업</option>
            <option value="upgrade">업그레이드</option>
            <option value="repair">수리</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-secondary-700 mb-2">기간</label>
          <select v-model="dateFilter" class="select-field">
            <option value="">전체</option>
            <option value="today">오늘</option>
            <option value="week">최근 7일</option>
            <option value="month">최근 30일</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-secondary-700 mb-2">정렬</label>
          <select v-model="sortBy" class="select-field">
            <option value="date">날짜</option>
            <option value="equipment">설비</option>
            <option value="type">타입</option>
            <option value="impact">영향도</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 변경 통계 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg">
            <ClockIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">총 변경</p>
            <p class="text-2xl font-semibold text-secondary-900">{{ totalChanges }}</p>
          </div>
        </div>
      </div>
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-success-500 to-success-600 rounded-lg">
            <CheckCircleIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">완료</p>
            <p class="text-2xl font-semibold text-secondary-900">{{ completedChanges }}</p>
          </div>
        </div>
      </div>
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-warning-500 to-warning-600 rounded-lg">
            <ClockIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">진행 중</p>
            <p class="text-2xl font-semibold text-secondary-900">{{ inProgressChanges }}</p>
          </div>
        </div>
      </div>
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-danger-500 to-danger-600 rounded-lg">
            <ExclamationTriangleIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">높은 영향</p>
            <p class="text-2xl font-semibold text-secondary-900">{{ highImpactChanges }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 변경 트렌드 차트 -->
    <div class="card">
      <div class="card-header">
        <h3 class="text-lg font-semibold">변경 트렌드</h3>
      </div>
      <div class="h-64 flex items-center justify-center bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg">
        <div class="text-center">
          <ChartBarIcon class="w-16 h-16 text-blue-400 mx-auto mb-2" />
          <p class="text-blue-600 font-medium">월별 변경 트렌드 (Chart.js)</p>
          <p class="text-sm text-blue-500 mt-1">최근 12개월 변경 현황</p>
        </div>
      </div>
    </div>

    <!-- 변경 타임라인 -->
    <div class="card">
      <div class="card-header">
        <h3 class="text-lg font-semibold">변경 타임라인</h3>
      </div>
      <div class="space-y-6">
        <div v-for="change in paginatedChanges" :key="change.id" class="relative">
          <!-- 타임라인 라인 -->
          <div class="absolute left-6 top-12 bottom-0 w-0.5 bg-secondary-200" v-if="change.id !== paginatedChanges[paginatedChanges.length - 1].id"></div>
          
          <div class="flex items-start space-x-4">
            <!-- 타임라인 아이콘 -->
            <div class="flex-shrink-0">
              <div class="w-12 h-12 rounded-full flex items-center justify-center border-4 border-white shadow-md"
                   :class="getChangeIconClass(change.type)">
                <component :is="getChangeIcon(change.type)" class="w-6 h-6 text-white" />
              </div>
            </div>
            
            <!-- 변경 내용 -->
            <div class="flex-1 min-w-0 bg-white rounded-lg border border-secondary-200 p-6 shadow-sm hover:shadow-md transition-shadow">
              <div class="flex items-center justify-between mb-3">
                <div class="flex items-center space-x-3">
                  <h4 class="text-lg font-semibold text-secondary-900">{{ change.title }}</h4>
                  <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                        :class="getChangeTypeClass(change.type)">
                    {{ getChangeTypeText(change.type) }}
                  </span>
                  <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium"
                        :class="getImpactClass(change.impact)">
                    {{ getImpactText(change.impact) }}
                  </span>
                </div>
                <div class="text-sm text-secondary-500">
                  {{ formatDateTime(change.date) }}
                </div>
              </div>
              
              <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
                <div>
                  <label class="block text-xs font-medium text-secondary-500 mb-1">설비</label>
                  <p class="text-sm text-secondary-900">{{ change.equipment }}</p>
                </div>
                <div>
                  <label class="block text-xs font-medium text-secondary-500 mb-1">담당자</label>
                  <p class="text-sm text-secondary-900">{{ change.assignee }}</p>
                </div>
                <div>
                  <label class="block text-xs font-medium text-secondary-500 mb-1">상태</label>
                  <span class="status-badge" :class="getStatusClass(change.status)">
                    {{ getStatusText(change.status) }}
                  </span>
                </div>
              </div>
              
              <p class="text-sm text-secondary-700 mb-4">{{ change.description }}</p>
              
              <div class="flex items-center justify-between">
                <div class="flex items-center space-x-4 text-xs text-secondary-500">
                  <span>시작: {{ formatDateTime(change.startDate) }}</span>
                  <span>완료: {{ change.endDate ? formatDateTime(change.endDate) : '진행 중' }}</span>
                </div>
                <router-link :to="`/changes/${change.id}`" class="btn-ghost text-sm">
                  상세보기
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 페이지네이션 -->
      <div class="flex items-center justify-between px-6 py-3 border-t border-secondary-200 mt-6">
        <div class="flex items-center text-sm text-secondary-500">
          <span>총 {{ filteredChanges.length }}개 중 {{ startIndex + 1 }}-{{ endIndex }}개 표시</span>
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
  PlusIcon,
  MagnifyingGlassIcon,
  ClockIcon,
  CheckCircleIcon,
  ExclamationTriangleIcon,
  ChartBarIcon,
  CogIcon,
  WrenchScrewdriverIcon,
  ArrowUpIcon,
  DocumentTextIcon
} from '@heroicons/vue/24/outline'

export default {
  name: 'ChangeTracking',
  components: {
    PlusIcon,
    MagnifyingGlassIcon,
    ClockIcon,
    CheckCircleIcon,
    ExclamationTriangleIcon,
    ChartBarIcon,
    CogIcon,
    WrenchScrewdriverIcon,
    ArrowUpIcon,
    DocumentTextIcon
  },
  setup() {
    const searchQuery = ref('')
    const equipmentFilter = ref('')
    const typeFilter = ref('')
    const dateFilter = ref('')
    const sortBy = ref('date')
    const currentPage = ref(1)
    const itemsPerPage = ref(5)

    const changes = ref([
      {
        id: 'change-001',
        title: 'CVD-001 온도 제어 파라미터 조정',
        type: 'configuration',
        equipment: 'CVD-001',
        assignee: '김엔지니어',
        status: 'completed',
        impact: 'medium',
        description: '공정 안정성 향상을 위한 온도 제어 파라미터 최적화 작업',
        date: '2025-05-23T10:30:00',
        startDate: '2025-05-23T09:00:00',
        endDate: '2025-05-23T10:30:00'
      },
      {
        id: 'change-002',
        title: 'ETCH-003 정기 정비 작업',
        type: 'maintenance',
        equipment: 'ETCH-003',
        assignee: '박기술자',
        status: 'in-progress',
        impact: 'high',
        description: '정기 정비 일정에 따른 주요 부품 교체 및 점검',
        date: '2025-05-23T08:00:00',
        startDate: '2025-05-23T08:00:00',
        endDate: null
      },
      {
        id: 'change-003',
        title: 'CMP-002 소프트웨어 업그레이드',
        type: 'upgrade',
        equipment: 'CMP-002',
        assignee: '이엔지니어',
        status: 'completed',
        impact: 'low',
        description: '제조사 권장 소프트웨어 버전 업그레이드',
        date: '2025-05-22T16:00:00',
        startDate: '2025-05-22T15:30:00',
        endDate: '2025-05-22T16:45:00'
      },
      {
        id: 'change-004',
        title: 'LITHO-001 렌즈 교체 작업',
        type: 'repair',
        equipment: 'LITHO-001',
        assignee: '최기술자',
        status: 'completed',
        impact: 'high',
        description: '노후화된 렌즈 교체로 인한 해상도 개선',
        date: '2025-05-22T10:00:00',
        startDate: '2025-05-22T09:00:00',
        endDate: '2025-05-22T12:00:00'
      },
      {
        id: 'change-005',
        title: 'CVD-001 가스 라인 점검',
        type: 'maintenance',
        equipment: 'CVD-001',
        assignee: '정기술자',
        status: 'scheduled',
        impact: 'medium',
        description: '가스 라인 누설 점검 및 밸브 교체',
        date: '2025-05-21T14:00:00',
        startDate: '2025-05-21T14:00:00',
        endDate: '2025-05-21T16:30:00'
      }
    ])

    const filteredChanges = computed(() => {
      let filtered = changes.value

      // 검색 필터
      if (searchQuery.value) {
        filtered = filtered.filter(change =>
          change.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          change.description.toLowerCase().includes(searchQuery.value.toLowerCase())
        )
      }

      // 설비 필터
      if (equipmentFilter.value) {
        filtered = filtered.filter(change => change.equipment === equipmentFilter.value)
      }

      // 타입 필터
      if (typeFilter.value) {
        filtered = filtered.filter(change => change.type === typeFilter.value)
      }

      // 정렬
      filtered.sort((a, b) => {
        switch (sortBy.value) {
          case 'date':
            return new Date(b.date) - new Date(a.date)
          case 'equipment':
            return a.equipment.localeCompare(b.equipment)
          case 'type':
            return a.type.localeCompare(b.type)
          case 'impact':
            const impactOrder = { high: 3, medium: 2, low: 1 }
            return impactOrder[b.impact] - impactOrder[a.impact]
          default:
            return 0
        }
      })

      return filtered
    })

    const totalPages = computed(() => {
      return Math.ceil(filteredChanges.value.length / itemsPerPage.value)
    })

    const startIndex = computed(() => {
      return (currentPage.value - 1) * itemsPerPage.value
    })

    const endIndex = computed(() => {
      return Math.min(startIndex.value + itemsPerPage.value, filteredChanges.value.length)
    })

    const paginatedChanges = computed(() => {
      return filteredChanges.value.slice(startIndex.value, endIndex.value)
    })

    const totalChanges = computed(() => changes.value.length)
    const completedChanges = computed(() => changes.value.filter(c => c.status === 'completed').length)
    const inProgressChanges = computed(() => changes.value.filter(c => c.status === 'in-progress').length)
    const highImpactChanges = computed(() => changes.value.filter(c => c.impact === 'high').length)

    const getChangeIcon = (type) => {
      const icons = {
        configuration: CogIcon,
        maintenance: WrenchScrewdriverIcon,
        upgrade: ArrowUpIcon,
        repair: DocumentTextIcon
      }
      return icons[type] || CogIcon
    }

    const getChangeIconClass = (type) => {
      const classes = {
        configuration: 'bg-gradient-to-r from-blue-500 to-blue-600',
        maintenance: 'bg-gradient-to-r from-warning-500 to-warning-600',
        upgrade: 'bg-gradient-to-r from-success-500 to-success-600',
        repair: 'bg-gradient-to-r from-danger-500 to-danger-600'
      }
      return classes[type] || 'bg-gradient-to-r from-secondary-500 to-secondary-600'
    }

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

    const formatDateTime = (dateString) => {
      return format(new Date(dateString), 'MM.dd HH:mm', { locale: ko })
    }

    return {
      searchQuery,
      equipmentFilter,
      typeFilter,
      dateFilter,
      sortBy,
      currentPage,
      itemsPerPage,
      filteredChanges,
      paginatedChanges,
      totalPages,
      startIndex,
      endIndex,
      totalChanges,
      completedChanges,
      inProgressChanges,
      highImpactChanges,
      getChangeIcon,
      getChangeIconClass,
      getChangeTypeClass,
      getChangeTypeText,
      getImpactClass,
      getImpactText,
      getStatusClass,
      getStatusText,
      formatDateTime
    }
  }
}
</script>
