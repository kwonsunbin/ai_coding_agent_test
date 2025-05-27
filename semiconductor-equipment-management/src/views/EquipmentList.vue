<template>
  <div class="space-y-6">
    <!-- 페이지 헤더 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h1 class="text-2xl font-bold text-gray-900">설비 목록</h1>
        <p class="mt-1 text-sm text-gray-500">전체 반도체 설비 현황 및 관리</p>
      </div>
      <div class="mt-4 sm:mt-0 flex space-x-3">
        <button class="btn-secondary">
          <ArrowDownTrayIcon class="w-4 h-4 mr-2" />
          내보내기
        </button>
        <button class="btn-primary">
          <PlusIcon class="w-4 h-4 mr-2" />
          설비 추가
        </button>
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
              placeholder="설비명 또는 ID 검색"
              class="pl-10 w-full border-gray-300 rounded-md text-sm"
            />
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">상태</label>
          <select v-model="statusFilter" class="w-full border-gray-300 rounded-md text-sm">
            <option value="">전체 상태</option>
            <option value="normal">정상</option>
            <option value="warning">경고</option>
            <option value="critical">위험</option>
            <option value="maintenance">점검중</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">라인</label>
          <select v-model="lineFilter" class="w-full border-gray-300 rounded-md text-sm">
            <option value="">전체 라인</option>
            <option value="A">라인 A</option>
            <option value="B">라인 B</option>
            <option value="C">라인 C</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">설비 유형</label>
          <select v-model="typeFilter" class="w-full border-gray-300 rounded-md text-sm">
            <option value="">전체 유형</option>
            <option value="CVD">CVD</option>
            <option value="ETCH">ETCH</option>
            <option value="CMP">CMP</option>
            <option value="LITHO">LITHO</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 설비 목록 테이블 -->
    <div class="card">
      <div class="flex items-center justify-between mb-4">
        <div class="flex items-center space-x-4">
          <h3 class="text-lg font-medium text-gray-900">설비 현황</h3>
          <span class="text-sm text-gray-500">총 {{ filteredEquipment.length }}개 설비</span>
        </div>
        <div class="flex items-center space-x-2">
          <span class="text-sm text-gray-500">정렬:</span>
          <select v-model="sortBy" class="text-sm border-gray-300 rounded-md">
            <option value="name">설비명</option>
            <option value="status">상태</option>
            <option value="efficiency">가동률</option>
            <option value="lastMaintenance">마지막 점검</option>
          </select>
        </div>
      </div>

      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                <input type="checkbox" v-model="selectAll" @change="toggleSelectAll" class="rounded border-gray-300">
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">설비 정보</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">상태</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">가동률</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">예측 위험도</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">마지막 점검</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">담당자</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">작업</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="equipment in paginatedEquipment" :key="equipment.id" class="hover:bg-gray-50">
              <td class="px-6 py-4 whitespace-nowrap">
                <input 
                  type="checkbox" 
                  v-model="selectedEquipment" 
                  :value="equipment.id" 
                  class="rounded border-gray-300"
                >
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="flex-shrink-0 h-10 w-10">
                    <div class="h-10 w-10 rounded-lg bg-gray-100 flex items-center justify-center">
                      <CpuChipIcon class="h-6 w-6 text-gray-600" />
                    </div>
                  </div>
                  <div class="ml-4">
                    <div class="text-sm font-medium text-gray-900">{{ equipment.name }}</div>
                    <div class="text-sm text-gray-500">{{ equipment.type }} | 라인 {{ equipment.line }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="flex-shrink-0 mr-2">
                    <div class="h-2 w-2 rounded-full" :class="getStatusDotColor(equipment.status)"></div>
                  </div>
                  <span class="status-badge" :class="getStatusClass(equipment.status)">
                    {{ getStatusText(equipment.status) }}
                  </span>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="flex-1">
                    <div class="text-sm font-medium text-gray-900">{{ equipment.efficiency }}%</div>
                    <div class="w-full bg-gray-200 rounded-full h-2 mt-1">
                      <div 
                        class="h-2 rounded-full transition-all duration-300"
                        :class="getEfficiencyBarColor(equipment.efficiency)"
                        :style="{ width: equipment.efficiency + '%' }"
                      ></div>
                    </div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="status-badge" :class="getPredictionClass(equipment.prediction)">
                  {{ getPredictionText(equipment.prediction) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatDate(equipment.lastMaintenance) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ equipment.manager }}</div>
                <div class="text-sm text-gray-500">{{ equipment.team }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <div class="flex items-center space-x-2">
                  <router-link 
                    :to="`/equipment/${equipment.id}`"
                    class="text-primary-600 hover:text-primary-900"
                  >
                    상세보기
                  </router-link>
                  <button class="text-gray-400 hover:text-gray-600">
                    <EllipsisVerticalIcon class="h-4 w-4" />
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 페이지네이션 -->
      <div class="flex items-center justify-between border-t border-gray-200 bg-white px-4 py-3 sm:px-6">
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
              총 <span class="font-medium">{{ filteredEquipment.length }}</span>개 중 
              <span class="font-medium">{{ startIndex + 1 }}</span>-<span class="font-medium">{{ endIndex }}</span>개 표시
            </p>
          </div>
          <div>
            <nav class="isolate inline-flex -space-x-px rounded-md shadow-sm" aria-label="Pagination">
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
                    ? 'z-10 bg-primary-600 text-white focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary-600'
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
  CpuChipIcon,
  MagnifyingGlassIcon,
  PlusIcon,
  ArrowDownTrayIcon,
  EllipsisVerticalIcon,
  ChevronLeftIcon,
  ChevronRightIcon
} from '@heroicons/vue/24/outline'

// 필터 상태
const searchQuery = ref('')
const statusFilter = ref('')
const lineFilter = ref('')
const typeFilter = ref('')
const sortBy = ref('name')

// 페이지네이션
const currentPage = ref(1)
const itemsPerPage = ref(10)

// 선택 상태
const selectAll = ref(false)
const selectedEquipment = ref([])

// 샘플 데이터
const equipment = ref([
  {
    id: 1,
    name: 'CVD-001',
    type: 'CVD',
    line: 'A',
    status: 'critical',
    efficiency: 85,
    prediction: 'high_risk',
    lastMaintenance: new Date(Date.now() - 7 * 24 * 60 * 60 * 1000),
    manager: '김철수',
    team: '설비팀 A'
  },
  {
    id: 2,
    name: 'ETCH-003',
    type: 'ETCH',
    line: 'A',
    status: 'warning',
    efficiency: 92,
    prediction: 'medium_risk',
    lastMaintenance: new Date(Date.now() - 14 * 24 * 60 * 60 * 1000),
    manager: '이영희',
    team: '설비팀 A'
  },
  {
    id: 3,
    name: 'CMP-002',
    type: 'CMP',
    line: 'B',
    status: 'normal',
    efficiency: 98,
    prediction: 'low_risk',
    lastMaintenance: new Date(Date.now() - 3 * 24 * 60 * 60 * 1000),
    manager: '박민수',
    team: '설비팀 B'
  },
  {
    id: 4,
    name: 'LITHO-001',
    type: 'LITHO',
    line: 'C',
    status: 'normal',
    efficiency: 96,
    prediction: 'low_risk',
    lastMaintenance: new Date(Date.now() - 5 * 24 * 60 * 60 * 1000),
    manager: '정수진',
    team: '설비팀 C'
  },
  {
    id: 5,
    name: 'CVD-002',
    type: 'CVD',
    line: 'A',
    status: 'maintenance',
    efficiency: 0,
    prediction: 'low_risk',
    lastMaintenance: new Date(),
    manager: '김철수',
    team: '설비팀 A'
  },
  {
    id: 6,
    name: 'ETCH-004',
    type: 'ETCH',
    line: 'B',
    status: 'normal',
    efficiency: 94,
    prediction: 'low_risk',
    lastMaintenance: new Date(Date.now() - 10 * 24 * 60 * 60 * 1000),
    manager: '이영희',
    team: '설비팀 A'
  }
])

// 필터링된 설비 목록
const filteredEquipment = computed(() => {
  let filtered = equipment.value

  // 검색 필터
  if (searchQuery.value) {
    filtered = filtered.filter(item => 
      item.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      item.type.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }

  // 상태 필터
  if (statusFilter.value) {
    filtered = filtered.filter(item => item.status === statusFilter.value)
  }

  // 라인 필터
  if (lineFilter.value) {
    filtered = filtered.filter(item => item.line === lineFilter.value)
  }

  // 유형 필터
  if (typeFilter.value) {
    filtered = filtered.filter(item => item.type === typeFilter.value)
  }

  // 정렬
  filtered.sort((a, b) => {
    switch (sortBy.value) {
      case 'name':
        return a.name.localeCompare(b.name)
      case 'status':
        return a.status.localeCompare(b.status)
      case 'efficiency':
        return b.efficiency - a.efficiency
      case 'lastMaintenance':
        return new Date(b.lastMaintenance) - new Date(a.lastMaintenance)
      default:
        return 0
    }
  })

  return filtered
})

// 페이지네이션 계산
const totalPages = computed(() => Math.ceil(filteredEquipment.value.length / itemsPerPage.value))
const startIndex = computed(() => (currentPage.value - 1) * itemsPerPage.value)
const endIndex = computed(() => Math.min(startIndex.value + itemsPerPage.value, filteredEquipment.value.length))

const paginatedEquipment = computed(() => {
  return filteredEquipment.value.slice(startIndex.value, endIndex.value)
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

// 선택 관련 함수
const toggleSelectAll = () => {
  if (selectAll.value) {
    selectedEquipment.value = paginatedEquipment.value.map(item => item.id)
  } else {
    selectedEquipment.value = []
  }
}

// 필터 변경 시 첫 페이지로 이동
watch([searchQuery, statusFilter, lineFilter, typeFilter], () => {
  currentPage.value = 1
})

// 헬퍼 함수들
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
    maintenance: '점검중'
  }
  return texts[status] || '정상'
}

const getStatusDotColor = (status) => {
  const colors = {
    normal: 'bg-success-400',
    warning: 'bg-warning-400',
    critical: 'bg-danger-400',
    maintenance: 'bg-gray-400'
  }
  return colors[status] || 'bg-success-400'
}

const getEfficiencyBarColor = (efficiency) => {
  if (efficiency >= 95) return 'bg-success-500'
  if (efficiency >= 85) return 'bg-warning-500'
  return 'bg-danger-500'
}

const getPredictionClass = (prediction) => {
  const classes = {
    low_risk: 'status-normal',
    medium_risk: 'status-warning',
    high_risk: 'status-critical'
  }
  return classes[prediction] || 'status-normal'
}

const getPredictionText = (prediction) => {
  const texts = {
    low_risk: '안전',
    medium_risk: '주의',
    high_risk: '위험'
  }
  return texts[prediction] || '안전'
}

const formatDate = (date) => {
  return format(date, 'yyyy.MM.dd', { locale: ko })
}
</script>
