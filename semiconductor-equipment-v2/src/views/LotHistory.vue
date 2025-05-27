<template>
  <div class="space-y-6 animate-fade-in-up">
    <!-- 페이지 헤더 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div>
        <div class="flex items-center space-x-2 mb-2">
          <router-link to="/equipment" class="text-blue-600 hover:text-blue-700">
            <ArrowLeftIcon class="w-5 h-5" />
          </router-link>
          <h1 class="text-2xl font-bold gradient-text">{{ equipmentName }} 랏 진행 이력</h1>
        </div>
        <p class="mt-1 text-secondary-600">설비별 랏 처리 현황 및 이력 관리</p>
      </div>
      <div class="mt-4 sm:mt-0 flex space-x-3">
        <button class="btn-secondary">
          <DocumentArrowDownIcon class="w-4 h-4 mr-2" />
          내보내기
        </button>
        <router-link :to="`/equipment/${equipmentId}`" class="btn-primary">
          <CogIcon class="w-4 h-4 mr-2" />
          설비 상세
        </router-link>
      </div>
    </div>

    <!-- 설비 요약 정보 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg">
            <ClipboardDocumentListIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">총 랏 수</p>
            <p class="text-2xl font-semibold text-secondary-900">{{ totalLots }}</p>
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
            <p class="text-2xl font-semibold text-secondary-900">{{ completedLots }}</p>
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
            <p class="text-2xl font-semibold text-secondary-900">{{ inProgressLots }}</p>
          </div>
        </div>
      </div>
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-danger-500 to-danger-600 rounded-lg">
            <XCircleIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">실패</p>
            <p class="text-2xl font-semibold text-secondary-900">{{ failedLots }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 필터 및 검색 -->
    <div class="card">
      <div class="grid grid-cols-1 md:grid-cols-5 gap-4">
        <div>
          <label class="block text-sm font-medium text-secondary-700 mb-2">랏 ID 검색</label>
          <div class="relative">
            <MagnifyingGlassIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-secondary-400" />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="랏 ID 검색..."
              class="input-field pl-10"
            />
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium text-secondary-700 mb-2">상태</label>
          <select v-model="statusFilter" class="select-field">
            <option value="">전체</option>
            <option value="completed">완료</option>
            <option value="in-progress">진행 중</option>
            <option value="failed">실패</option>
            <option value="queued">대기</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-secondary-700 mb-2">제품 타입</label>
          <select v-model="productFilter" class="select-field">
            <option value="">전체</option>
            <option value="DRAM">DRAM</option>
            <option value="NAND">NAND</option>
            <option value="Logic">Logic</option>
            <option value="Analog">Analog</option>
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
            <option value="startTime">시작 시간</option>
            <option value="endTime">완료 시간</option>
            <option value="lotId">랏 ID</option>
            <option value="status">상태</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 랏 처리 트렌드 차트 -->
    <div class="card">
      <div class="card-header">
        <h3 class="text-lg font-semibold">랏 처리 트렌드</h3>
      </div>
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div class="h-64 flex items-center justify-center bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg">
          <div class="text-center">
            <ChartBarIcon class="w-16 h-16 text-blue-400 mx-auto mb-2" />
            <p class="text-blue-600 font-medium">일별 랏 처리량 (Chart.js)</p>
            <p class="text-sm text-blue-500 mt-1">최근 30일 트렌드</p>
          </div>
        </div>
        <div class="h-64 flex items-center justify-center bg-gradient-to-br from-blue-50 to-blue-100 rounded-lg">
          <div class="text-center">
            <ChartBarIcon class="w-16 h-16 text-blue-400 mx-auto mb-2" />
            <p class="text-blue-600 font-medium">성공률 트렌드 (Chart.js)</p>
            <p class="text-sm text-blue-500 mt-1">주간 성공률 변화</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 랏 이력 테이블 -->
    <div class="card">
      <div class="flex items-center justify-between mb-6">
        <h3 class="text-lg font-semibold text-secondary-900">랏 진행 이력</h3>
        <div class="text-sm text-secondary-500">
          총 {{ filteredLots.length }}개 랏
        </div>
      </div>
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-secondary-200">
          <thead class="table-header">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">랏 ID</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">제품 타입</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">상태</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">웨이퍼 수</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">시작 시간</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">완료 시간</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">처리 시간</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">수율</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">액션</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-secondary-200">
            <tr v-for="lot in paginatedLots" :key="lot.id" class="table-row">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-secondary-900">{{ lot.lotId }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                  {{ lot.productType }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="status-badge" :class="getLotStatusClass(lot.status)">
                  {{ getLotStatusText(lot.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-secondary-900">
                {{ lot.waferCount }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-secondary-500">
                {{ formatDateTime(lot.startTime) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-secondary-500">
                {{ lot.endTime ? formatDateTime(lot.endTime) : '-' }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-secondary-500">
                {{ lot.processingTime || '-' }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <span class="text-sm text-secondary-900 mr-2">{{ lot.yield }}%</span>
                  <div class="progress-bar w-16">
                    <div class="progress-fill" :class="getYieldClass(lot.yield)" :style="`width: ${lot.yield}%`"></div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <router-link :to="`/lots/${lot.id}`" class="btn-ghost text-sm">
                  상세보기
                </router-link>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 페이지네이션 -->
      <div class="flex items-center justify-between px-6 py-3 border-t border-secondary-200">
        <div class="flex items-center text-sm text-secondary-500">
          <span>총 {{ filteredLots.length }}개 중 {{ startIndex + 1 }}-{{ endIndex }}개 표시</span>
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

    <!-- 성능 분석 -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="card bg-gradient-to-br from-blue-50 to-blue-100 border-blue-200">
        <div class="flex items-center mb-4">
          <ClockIcon class="w-6 h-6 text-blue-600 mr-2" />
          <h3 class="text-lg font-semibold text-blue-800">평균 처리 시간</h3>
        </div>
        <div class="text-center">
          <p class="text-3xl font-bold text-blue-900 mb-2">{{ averageProcessingTime }}</p>
          <p class="text-sm text-blue-700">분</p>
          <p class="text-xs text-blue-600 mt-2">전주 대비 -5%</p>
        </div>
      </div>

      <div class="card bg-gradient-to-br from-success-50 to-success-100 border-success-200">
        <div class="flex items-center mb-4">
          <CheckCircleIcon class="w-6 h-6 text-success-600 mr-2" />
          <h3 class="text-lg font-semibold text-success-800">평균 수율</h3>
        </div>
        <div class="text-center">
          <p class="text-3xl font-bold text-success-900 mb-2">{{ averageYield }}%</p>
          <p class="text-sm text-success-700">수율</p>
          <p class="text-xs text-success-600 mt-2">전주 대비 +2%</p>
        </div>
      </div>

      <div class="card bg-gradient-to-br from-warning-50 to-warning-100 border-warning-200">
        <div class="flex items-center mb-4">
          <ExclamationTriangleIcon class="w-6 h-6 text-warning-600 mr-2" />
          <h3 class="text-lg font-semibold text-warning-800">실패율</h3>
        </div>
        <div class="text-center">
          <p class="text-3xl font-bold text-warning-900 mb-2">{{ failureRate }}%</p>
          <p class="text-sm text-warning-700">실패율</p>
          <p class="text-xs text-warning-600 mt-2">전주 대비 -1%</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { format } from 'date-fns'
import { ko } from 'date-fns/locale'
import {
  ArrowLeftIcon,
  CogIcon,
  ClipboardDocumentListIcon,
  CheckCircleIcon,
  ClockIcon,
  XCircleIcon,
  ExclamationTriangleIcon,
  MagnifyingGlassIcon,
  DocumentArrowDownIcon,
  ChartBarIcon
} from '@heroicons/vue/24/outline'

export default {
  name: 'LotHistory',
  components: {
    ArrowLeftIcon,
    CogIcon,
    ClipboardDocumentListIcon,
    CheckCircleIcon,
    ClockIcon,
    XCircleIcon,
    ExclamationTriangleIcon,
    MagnifyingGlassIcon,
    DocumentArrowDownIcon,
    ChartBarIcon
  },
  setup() {
    const route = useRoute()
    const equipmentId = ref(route.params.id)
    const equipmentName = ref('')
    
    const searchQuery = ref('')
    const statusFilter = ref('')
    const productFilter = ref('')
    const dateFilter = ref('')
    const sortBy = ref('startTime')
    const currentPage = ref(1)
    const itemsPerPage = ref(15)

    const lots = ref([
      {
        id: 'lot-001',
        lotId: 'LOT-2025052301',
        productType: 'DRAM',
        status: 'completed',
        waferCount: 25,
        startTime: '2025-05-23T08:30:00',
        endTime: '2025-05-23T10:15:00',
        processingTime: '1시간 45분',
        yield: 98.5
      },
      {
        id: 'lot-002',
        lotId: 'LOT-2025052302',
        productType: 'NAND',
        status: 'in-progress',
        waferCount: 25,
        startTime: '2025-05-23T09:00:00',
        endTime: null,
        processingTime: null,
        yield: 0
      },
      {
        id: 'lot-003',
        lotId: 'LOT-2025052201',
        productType: 'Logic',
        status: 'completed',
        waferCount: 25,
        startTime: '2025-05-22T14:20:00',
        endTime: '2025-05-22T16:05:00',
        processingTime: '1시간 45분',
        yield: 96.8
      },
      {
        id: 'lot-004',
        lotId: 'LOT-2025052202',
        productType: 'DRAM',
        status: 'failed',
        waferCount: 25,
        startTime: '2025-05-22T16:30:00',
        endTime: '2025-05-22T17:15:00',
        processingTime: '45분',
        yield: 45.2
      },
      {
        id: 'lot-005',
        lotId: 'LOT-2025052203',
        productType: 'Analog',
        status: 'completed',
        waferCount: 25,
        startTime: '2025-05-22T10:00:00',
        endTime: '2025-05-22T11:50:00',
        processingTime: '1시간 50분',
        yield: 99.2
      }
    ])

    const filteredLots = computed(() => {
      let filtered = lots.value

      // 검색 필터
      if (searchQuery.value) {
        filtered = filtered.filter(lot =>
          lot.lotId.toLowerCase().includes(searchQuery.value.toLowerCase())
        )
      }

      // 상태 필터
      if (statusFilter.value) {
        filtered = filtered.filter(lot => lot.status === statusFilter.value)
      }

      // 제품 타입 필터
      if (productFilter.value) {
        filtered = filtered.filter(lot => lot.productType === productFilter.value)
      }

      // 정렬
      filtered.sort((a, b) => {
        switch (sortBy.value) {
          case 'startTime':
            return new Date(b.startTime) - new Date(a.startTime)
          case 'endTime':
            if (!a.endTime) return 1
            if (!b.endTime) return -1
            return new Date(b.endTime) - new Date(a.endTime)
          case 'lotId':
            return a.lotId.localeCompare(b.lotId)
          case 'status':
            return a.status.localeCompare(b.status)
          default:
            return 0
        }
      })

      return filtered
    })

    const totalPages = computed(() => {
      return Math.ceil(filteredLots.value.length / itemsPerPage.value)
    })

    const startIndex = computed(() => {
      return (currentPage.value - 1) * itemsPerPage.value
    })

    const endIndex = computed(() => {
      return Math.min(startIndex.value + itemsPerPage.value, filteredLots.value.length)
    })

    const paginatedLots = computed(() => {
      return filteredLots.value.slice(startIndex.value, endIndex.value)
    })

    const totalLots = computed(() => lots.value.length)
    const completedLots = computed(() => lots.value.filter(lot => lot.status === 'completed').length)
    const inProgressLots = computed(() => lots.value.filter(lot => lot.status === 'in-progress').length)
    const failedLots = computed(() => lots.value.filter(lot => lot.status === 'failed').length)

    const averageProcessingTime = computed(() => {
      const completedLotsWithTime = lots.value.filter(lot => lot.status === 'completed')
      return completedLotsWithTime.length > 0 ? '105' : '0'
    })

    const averageYield = computed(() => {
      const completedLotsData = lots.value.filter(lot => lot.status === 'completed')
      if (completedLotsData.length === 0) return '0'
      const totalYield = completedLotsData.reduce((sum, lot) => sum + lot.yield, 0)
      return (totalYield / completedLotsData.length).toFixed(1)
    })

    const failureRate = computed(() => {
      if (lots.value.length === 0) return '0'
      return ((failedLots.value / lots.value.length) * 100).toFixed(1)
    })

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

    const getYieldClass = (yield_value) => {
      if (yield_value >= 95) return 'progress-success'
      if (yield_value >= 80) return 'progress-warning'
      return 'progress-danger'
    }

    const formatDateTime = (dateString) => {
      return format(new Date(dateString), 'MM.dd HH:mm', { locale: ko })
    }

    onMounted(() => {
      // 설비 정보 로드
      const equipmentNames = {
        'cvd-001': 'CVD-001',
        'etch-003': 'ETCH-003',
        'cmp-002': 'CMP-002',
        'litho-001': 'LITHO-001'
      }
      equipmentName.value = equipmentNames[equipmentId.value] || '알 수 없는 설비'
    })

    return {
      equipmentId,
      equipmentName,
      searchQuery,
      statusFilter,
      productFilter,
      dateFilter,
      sortBy,
      currentPage,
      itemsPerPage,
      filteredLots,
      paginatedLots,
      totalPages,
      startIndex,
      endIndex,
      totalLots,
      completedLots,
      inProgressLots,
      failedLots,
      averageProcessingTime,
      averageYield,
      failureRate,
      getLotStatusClass,
      getLotStatusText,
      getYieldClass,
      formatDateTime
    }
  }
}
</script>
