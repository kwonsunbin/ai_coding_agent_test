<template>
  <div class="space-y-6">
    <!-- 페이지 헤더 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">변경점 추적</h1>
        <p class="mt-1 text-sm text-gray-500">설비 변경 이력 및 추적 관리</p>
      </div>
      <div class="mt-4 sm:mt-0 flex space-x-3">
        <button class="btn-secondary">
          <ArrowDownTrayIcon class="w-4 h-4 mr-2" />
          내보내기
        </button>
        <button class="btn-primary">
          <PlusIcon class="w-4 h-4 mr-2" />
          변경점 등록
        </button>
      </div>
    </div>

    <!-- 필터 및 검색 -->
    <div class="card">
      <div class="grid grid-cols-1 md:grid-cols-5 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">검색</label>
          <div class="relative">
            <MagnifyingGlassIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 h-4 w-4 text-gray-400" />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="설비명 또는 변경 내용 검색"
              class="pl-10 w-full border-gray-300 rounded-md text-sm"
            />
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">변경 유형</label>
          <select v-model="typeFilter" class="w-full border-gray-300 rounded-md text-sm">
            <option value="">전체 유형</option>
            <option value="configuration">설정 변경</option>
            <option value="maintenance">정비 작업</option>
            <option value="upgrade">업그레이드</option>
            <option value="repair">수리</option>
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
          <label class="block text-sm font-medium text-gray-700 mb-1">시작일</label>
          <input
            v-model="startDate"
            type="date"
            class="w-full border-gray-300 rounded-md text-sm"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">종료일</label>
          <input
            v-model="endDate"
            type="date"
            class="w-full border-gray-300 rounded-md text-sm"
          />
        </div>
      </div>
    </div>

    <!-- 변경점 통계 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
      <div class="card">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <ClockIcon class="h-8 w-8 text-primary-600" />
          </div>
          <div class="ml-5">
            <p class="text-sm font-medium text-gray-500">이번 주 변경</p>
            <p class="text-2xl font-semibold text-gray-900">{{ weeklyChanges }}</p>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <WrenchScrewdriverIcon class="h-8 w-8 text-success-600" />
          </div>
          <div class="ml-5">
            <p class="text-sm font-medium text-gray-500">정비 작업</p>
            <p class="text-2xl font-semibold text-gray-900">{{ maintenanceCount }}</p>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <Cog6ToothIcon class="h-8 w-8 text-warning-600" />
          </div>
          <div class="ml-5">
            <p class="text-sm font-medium text-gray-500">설정 변경</p>
            <p class="text-2xl font-semibold text-gray-900">{{ configChanges }}</p>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="flex items-center">
          <div class="flex-shrink-0">
            <ExclamationTriangleIcon class="h-8 w-8 text-danger-600" />
          </div>
          <div class="ml-5">
            <p class="text-sm font-medium text-gray-500">긴급 수리</p>
            <p class="text-2xl font-semibold text-gray-900">{{ emergencyRepairs }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 변경점 타임라인 -->
    <div class="card">
      <div class="flex items-center justify-between mb-6">
        <h3 class="text-lg font-medium text-gray-900">변경점 타임라인</h3>
        <div class="flex items-center space-x-2">
          <span class="text-sm text-gray-500">정렬:</span>
          <select v-model="sortBy" class="text-sm border-gray-300 rounded-md">
            <option value="date">날짜순</option>
            <option value="equipment">설비순</option>
            <option value="type">유형순</option>
          </select>
        </div>
      </div>

      <div class="flow-root">
        <ul class="-mb-8">
          <li v-for="(change, index) in paginatedChanges" :key="change.id">
            <div class="relative pb-8">
              <span v-if="index !== paginatedChanges.length - 1" class="absolute top-4 left-4 -ml-px h-full w-0.5 bg-gray-200"></span>
              <div class="relative flex space-x-3">
                <div>
                  <span class="h-8 w-8 rounded-full flex items-center justify-center ring-8 ring-white" :class="getChangeTypeColor(change.type)">
                    <component :is="getChangeTypeIcon(change.type)" class="h-5 w-5 text-white" />
                  </span>
                </div>
                <div class="flex min-w-0 flex-1 justify-between space-x-4 pt-1.5">
                  <div class="min-w-0 flex-1">
                    <p class="text-sm text-gray-500">
                      <span class="font-medium text-gray-900">{{ change.equipment }}</span>에서 
                      <span class="font-medium" :class="getChangeTypeTextColor(change.type)">{{ getChangeTypeText(change.type) }}</span>
                    </p>
                    <p class="mt-1 text-sm font-medium text-gray-900">{{ change.title }}</p>
                    <p class="mt-1 text-sm text-gray-600">{{ change.description }}</p>
                    <div class="mt-2 flex items-center space-x-4">
                      <span class="text-xs text-gray-500">담당자: {{ change.author }}</span>
                      <span class="text-xs text-gray-500">승인자: {{ change.approver }}</span>
                      <span class="status-badge" :class="getStatusClass(change.status)">
                        {{ getStatusText(change.status) }}
                      </span>
                    </div>
                  </div>
                  <div class="whitespace-nowrap text-right text-sm text-gray-500">
                    <time :datetime="change.date">{{ formatDateTime(change.date) }}</time>
                    <div class="mt-1">
                      <router-link 
                        :to="`/changes/${change.id}`"
                        class="text-primary-600 hover:text-primary-500 text-xs"
                      >
                        상세보기
                      </router-link>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </li>
        </ul>
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
              총 <span class="font-medium">{{ filteredChanges.length }}</span>개 중 
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
  ClockIcon,
  WrenchScrewdriverIcon,
  Cog6ToothIcon,
  ExclamationTriangleIcon,
  MagnifyingGlassIcon,
  PlusIcon,
  ArrowDownTrayIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
  ArrowUpIcon,
  CpuChipIcon
} from '@heroicons/vue/24/outline'

// 필터 상태
const searchQuery = ref('')
const typeFilter = ref('')
const equipmentFilter = ref('')
const startDate = ref('')
const endDate = ref('')
const sortBy = ref('date')

// 페이지네이션
const currentPage = ref(1)
const itemsPerPage = ref(10)

// 통계 데이터
const weeklyChanges = ref(12)
const maintenanceCount = ref(8)
const configChanges = ref(15)
const emergencyRepairs = ref(2)

// 변경점 데이터
const changes = ref([
  {
    id: 1,
    equipment: 'CVD-001',
    type: 'maintenance',
    title: '온도 센서 교체',
    description: '기존 온도 센서의 정확도 저하로 인한 신규 센서 교체 작업',
    author: '김정비',
    approver: '이관리',
    status: 'completed',
    date: new Date(Date.now() - 2 * 60 * 60 * 1000)
  },
  {
    id: 2,
    equipment: 'ETCH-003',
    type: 'configuration',
    title: '공정 파라미터 변경',
    description: '에칭 시간을 120초에서 135초로 변경하여 품질 개선',
    author: '박공정',
    approver: '최팀장',
    status: 'completed',
    date: new Date(Date.now() - 4 * 60 * 60 * 1000)
  },
  {
    id: 3,
    equipment: 'CMP-002',
    type: 'repair',
    title: '펌프 모터 수리',
    description: '냉각수 순환 펌프 모터 베어링 교체 및 정비',
    author: '이수리',
    approver: '김관리',
    status: 'in_progress',
    date: new Date(Date.now() - 6 * 60 * 60 * 1000)
  },
  {
    id: 4,
    equipment: 'LITHO-001',
    type: 'upgrade',
    title: '소프트웨어 업데이트',
    description: '제어 소프트웨어를 v2.1.3에서 v2.2.0으로 업그레이드',
    author: '정소프트',
    approver: '한팀장',
    status: 'pending',
    date: new Date(Date.now() - 8 * 60 * 60 * 1000)
  },
  {
    id: 5,
    equipment: 'CVD-001',
    type: 'configuration',
    title: '압력 설정값 조정',
    description: '챔버 압력 설정을 8.5bar에서 9.0bar로 조정',
    author: '김공정',
    approver: '이관리',
    status: 'completed',
    date: new Date(Date.now() - 12 * 60 * 60 * 1000)
  },
  {
    id: 6,
    equipment: 'ETCH-003',
    type: 'repair',
    title: '가스 라인 누설 수리',
    description: '아르곤 가스 라인 연결부 누설 발견 및 수리 완료',
    author: '박수리',
    approver: '최팀장',
    status: 'completed',
    date: new Date(Date.now() - 24 * 60 * 60 * 1000)
  },
  {
    id: 7,
    equipment: 'CMP-002',
    type: 'maintenance',
    title: '정기 청소 및 점검',
    description: '월간 정기 청소 및 전체 시스템 점검 수행',
    author: '이정비',
    approver: '김관리',
    status: 'completed',
    date: new Date(Date.now() - 48 * 60 * 60 * 1000)
  },
  {
    id: 8,
    equipment: 'LITHO-001',
    type: 'configuration',
    title: '노광 시간 최적화',
    description: '노광 시간을 15초에서 12초로 단축하여 처리량 증대',
    author: '정공정',
    approver: '한팀장',
    status: 'completed',
    date: new Date(Date.now() - 72 * 60 * 60 * 1000)
  }
])

// 필터링된 변경점 목록
const filteredChanges = computed(() => {
  let filtered = changes.value

  // 검색 필터
  if (searchQuery.value) {
    filtered = filtered.filter(item => 
      item.equipment.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      item.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      item.description.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }

  // 유형 필터
  if (typeFilter.value) {
    filtered = filtered.filter(item => item.type === typeFilter.value)
  }

  // 설비 필터
  if (equipmentFilter.value) {
    filtered = filtered.filter(item => item.equipment === equipmentFilter.value)
  }

  // 날짜 필터
  if (startDate.value) {
    const start = new Date(startDate.value)
    filtered = filtered.filter(item => new Date(item.date) >= start)
  }

  if (endDate.value) {
    const end = new Date(endDate.value)
    end.setHours(23, 59, 59, 999)
    filtered = filtered.filter(item => new Date(item.date) <= end)
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
      default:
        return 0
    }
  })

  return filtered
})

// 페이지네이션 계산
const totalPages = computed(() => Math.ceil(filteredChanges.value.length / itemsPerPage.value))
const startIndex = computed(() => (currentPage.value - 1) * itemsPerPage.value)
const endIndex = computed(() => Math.min(startIndex.value + itemsPerPage.value, filteredChanges.value.length))

const paginatedChanges = computed(() => {
  return filteredChanges.value.slice(startIndex.value, endIndex.value)
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

// 필터 변경 시 첫 페이지로 이동
watch([searchQuery, typeFilter, equipmentFilter, startDate, endDate], () => {
  currentPage.value = 1
})

// 헬퍼 함수들
const getChangeTypeIcon = (type) => {
  const icons = {
    configuration: Cog6ToothIcon,
    maintenance: WrenchScrewdriverIcon,
    upgrade: ArrowUpIcon,
    repair: ExclamationTriangleIcon
  }
  return icons[type] || CpuChipIcon
}

const getChangeTypeColor = (type) => {
  const colors = {
    configuration: 'bg-primary-500',
    maintenance: 'bg-success-500',
    upgrade: 'bg-warning-500',
    repair: 'bg-danger-500'
  }
  return colors[type] || 'bg-gray-500'
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

const getChangeTypeTextColor = (type) => {
  const colors = {
    configuration: 'text-primary-600',
    maintenance: 'text-success-600',
    upgrade: 'text-warning-600',
    repair: 'text-danger-600'
  }
  return colors[type] || 'text-gray-600'
}

const getStatusClass = (status) => {
  const classes = {
    completed: 'status-normal',
    in_progress: 'status-warning',
    pending: 'status-maintenance'
  }
  return classes[status] || 'status-normal'
}

const getStatusText = (status) => {
  const texts = {
    completed: '완료',
    in_progress: '진행중',
    pending: '대기'
  }
  return texts[status] || '완료'
}

const formatDateTime = (date) => {
  return format(date, 'MM.dd HH:mm', { locale: ko })
}
</script>
