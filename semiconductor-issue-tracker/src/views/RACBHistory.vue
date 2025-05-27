<template>
  <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700">
    <!-- 헤더 -->
    <div class="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
      <div class="flex items-center justify-between">
        <h2 class="text-lg font-semibold text-gray-900 dark:text-white">RACB 이력</h2>
        <div class="flex items-center space-x-2">
          <button class="btn-primary">
            <PlusIcon class="w-4 h-4 mr-2" />
            새 RACB 등록
          </button>
        </div>
      </div>
    </div>

    <!-- 검색 및 필터 -->
    <div class="px-6 py-4 bg-gray-50 dark:bg-gray-700/50">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">RACB ID</label>
          <input 
            v-model="searchFilters.racbId"
            type="text" 
            placeholder="RACB ID 검색"
            class="form-input"
          >
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">상태</label>
          <select v-model="searchFilters.status" class="form-select">
            <option value="">전체</option>
            <option value="pending">대기</option>
            <option value="approved">승인</option>
            <option value="rejected">반려</option>
            <option value="implemented">구현완료</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">요청자</label>
          <input 
            v-model="searchFilters.requester"
            type="text" 
            placeholder="요청자 검색"
            class="form-input"
          >
        </div>
        <div class="flex items-end">
          <button @click="searchRACB" class="btn-secondary w-full">
            <MagnifyingGlassIcon class="w-4 h-4 mr-2" />
            검색
          </button>
        </div>
      </div>
    </div>

    <!-- RACB 목록 테이블 -->
    <div class="overflow-x-auto">
      <table class="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
        <thead class="bg-gray-50 dark:bg-gray-700">
          <tr>
            <th class="table-header">RACB ID</th>
            <th class="table-header">제목</th>
            <th class="table-header">요청자</th>
            <th class="table-header">요청일</th>
            <th class="table-header">상태</th>
            <th class="table-header">우선순위</th>
            <th class="table-header">승인자</th>
            <th class="table-header">작업</th>
          </tr>
        </thead>
        <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
          <tr v-for="racb in filteredRACBs" :key="racb.id" class="hover:bg-gray-50 dark:hover:bg-gray-700">
            <td class="table-cell">
              <span class="font-mono text-sm">{{ racb.id }}</span>
            </td>
            <td class="table-cell">
              <div class="max-w-xs truncate">
                <span class="font-medium">{{ racb.title }}</span>
              </div>
            </td>
            <td class="table-cell">{{ racb.requester }}</td>
            <td class="table-cell">{{ formatDate(racb.requestDate) }}</td>
            <td class="table-cell">
              <span :class="getStatusClass(racb.status)" class="status-badge">
                {{ getStatusText(racb.status) }}
              </span>
            </td>
            <td class="table-cell">
              <span :class="getPriorityClass(racb.priority)" class="priority-badge">
                {{ getPriorityText(racb.priority) }}
              </span>
            </td>
            <td class="table-cell">{{ racb.approver || '-' }}</td>
            <td class="table-cell">
              <div class="flex items-center space-x-2">
                <button @click="viewRACB(racb)" class="btn-icon" title="상세보기">
                  <EyeIcon class="w-4 h-4" />
                </button>
                <button @click="editRACB(racb)" class="btn-icon" title="수정">
                  <PencilIcon class="w-4 h-4" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 페이지네이션 -->
    <div class="px-6 py-4 border-t border-gray-200 dark:border-gray-700">
      <div class="flex items-center justify-between">
        <div class="text-sm text-gray-700 dark:text-gray-300">
          총 {{ totalRACBs }}개 중 {{ startIndex }}-{{ endIndex }}개 표시
        </div>
        <div class="flex items-center space-x-2">
          <button 
            @click="previousPage" 
            :disabled="currentPage === 1"
            class="btn-pagination"
          >
            이전
          </button>
          <span class="text-sm text-gray-700 dark:text-gray-300">
            {{ currentPage }} / {{ totalPages }}
          </span>
          <button 
            @click="nextPage" 
            :disabled="currentPage === totalPages"
            class="btn-pagination"
          >
            다음
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import {
  PlusIcon,
  MagnifyingGlassIcon,
  EyeIcon,
  PencilIcon
} from '@heroicons/vue/24/outline'

export default {
  name: 'RACBHistory',
  components: {
    PlusIcon,
    MagnifyingGlassIcon,
    EyeIcon,
    PencilIcon
  },
  setup() {
    const searchFilters = ref({
      racbId: '',
      status: '',
      requester: ''
    })

    const currentPage = ref(1)
    const itemsPerPage = ref(10)

    // 샘플 RACB 데이터
    const racbs = ref([
      {
        id: 'RACB-2024-001',
        title: '설비 A 공정 파라미터 변경 요청',
        requester: '김설비',
        requestDate: '2024-01-15',
        status: 'approved',
        priority: 'high',
        approver: '이관리',
        description: 'CVD 설비의 온도 파라미터 조정이 필요합니다.'
      },
      {
        id: 'RACB-2024-002',
        title: '새로운 레시피 등록 요청',
        requester: '박공정',
        requestDate: '2024-01-16',
        status: 'pending',
        priority: 'medium',
        approver: null,
        description: '신제품 대응을 위한 새로운 공정 레시피 등록'
      },
      {
        id: 'RACB-2024-003',
        title: '설비 B 유지보수 스케줄 변경',
        requester: '최유지',
        requestDate: '2024-01-17',
        status: 'implemented',
        priority: 'low',
        approver: '정승인',
        description: '정기 유지보수 일정 조정 요청'
      },
      {
        id: 'RACB-2024-004',
        title: '긴급 설비 수리 승인 요청',
        requester: '한긴급',
        requestDate: '2024-01-18',
        status: 'rejected',
        priority: 'critical',
        approver: '이관리',
        description: '설비 고장으로 인한 긴급 수리 필요'
      }
    ])

    // 필터링된 RACB 목록
    const filteredRACBs = computed(() => {
      let filtered = racbs.value

      if (searchFilters.value.racbId) {
        filtered = filtered.filter(racb => 
          racb.id.toLowerCase().includes(searchFilters.value.racbId.toLowerCase())
        )
      }

      if (searchFilters.value.status) {
        filtered = filtered.filter(racb => racb.status === searchFilters.value.status)
      }

      if (searchFilters.value.requester) {
        filtered = filtered.filter(racb => 
          racb.requester.toLowerCase().includes(searchFilters.value.requester.toLowerCase())
        )
      }

      // 페이지네이션 적용
      const start = (currentPage.value - 1) * itemsPerPage.value
      const end = start + itemsPerPage.value
      return filtered.slice(start, end)
    })

    // 페이지네이션 계산
    const totalRACBs = computed(() => racbs.value.length)
    const totalPages = computed(() => Math.ceil(totalRACBs.value / itemsPerPage.value))
    const startIndex = computed(() => (currentPage.value - 1) * itemsPerPage.value + 1)
    const endIndex = computed(() => Math.min(currentPage.value * itemsPerPage.value, totalRACBs.value))

    // 메서드들
    const searchRACB = () => {
      currentPage.value = 1
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

    const viewRACB = (racb) => {
      console.log('RACB 상세보기:', racb)
      // TODO: 상세보기 모달 또는 페이지로 이동
    }

    const editRACB = (racb) => {
      console.log('RACB 수정:', racb)
      // TODO: 수정 모달 또는 페이지로 이동
    }

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('ko-KR')
    }

    const getStatusClass = (status) => {
      const classes = {
        pending: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-300',
        approved: 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-300',
        rejected: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300',
        implemented: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300'
      }
      return classes[status] || 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300'
    }

    const getStatusText = (status) => {
      const texts = {
        pending: '대기',
        approved: '승인',
        rejected: '반려',
        implemented: '구현완료'
      }
      return texts[status] || status
    }

    const getPriorityClass = (priority) => {
      const classes = {
        critical: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300',
        high: 'bg-orange-100 text-orange-800 dark:bg-orange-900 dark:text-orange-300',
        medium: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-300',
        low: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300'
      }
      return classes[priority] || 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300'
    }

    const getPriorityText = (priority) => {
      const texts = {
        critical: '긴급',
        high: '높음',
        medium: '보통',
        low: '낮음'
      }
      return texts[priority] || priority
    }

    return {
      searchFilters,
      currentPage,
      filteredRACBs,
      totalRACBs,
      totalPages,
      startIndex,
      endIndex,
      searchRACB,
      previousPage,
      nextPage,
      viewRACB,
      editRACB,
      formatDate,
      getStatusClass,
      getStatusText,
      getPriorityClass,
      getPriorityText
    }
  }
}
</script>

<style scoped>
.btn-primary {
  @apply inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500;
}

.btn-secondary {
  @apply inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-gray-300 dark:border-gray-600 dark:hover:bg-gray-600;
}

.btn-icon {
  @apply p-1 text-gray-400 hover:text-gray-600 dark:hover:text-gray-300;
}

.btn-pagination {
  @apply px-3 py-1 text-sm border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed dark:border-gray-600 dark:hover:bg-gray-700;
}

.form-input {
  @apply block w-full border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm dark:bg-gray-700 dark:border-gray-600 dark:text-white;
}

.form-select {
  @apply block w-full border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm dark:bg-gray-700 dark:border-gray-600 dark:text-white;
}

.table-header {
  @apply px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider dark:text-gray-400;
}

.table-cell {
  @apply px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-gray-300;
}

.status-badge {
  @apply inline-flex px-2 py-1 text-xs font-semibold rounded-full;
}

.priority-badge {
  @apply inline-flex px-2 py-1 text-xs font-semibold rounded-full;
}
</style>
