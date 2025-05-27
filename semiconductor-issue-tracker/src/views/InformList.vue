<template>
  <div class="space-y-6">
    <!-- 인폼 목록 헤더 -->
    <div class="flex justify-between items-center">
      <h2 class="text-lg font-semibold text-gray-900 dark:text-white">인폼 목록</h2>
      <router-link to="/inform-write" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors">
        + 새 인폼 작성
      </router-link>
    </div>

    <!-- 필터 섹션 -->
    <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-4">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">상태</label>
          <select v-model="filters.status" @change="loadInforms" class="form-select">
            <option value="">전체</option>
            <option value="ACTIVE">활성</option>
            <option value="RESOLVED">해결됨</option>
            <option value="CLOSED">종료</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">우선순위</label>
          <select v-model="filters.priority" @change="loadInforms" class="form-select">
            <option value="">전체</option>
            <option value="CRITICAL">긴급</option>
            <option value="HIGH">높음</option>
            <option value="MEDIUM">보통</option>
            <option value="LOW">낮음</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">카테고리</label>
          <select v-model="filters.category" @change="loadInforms" class="form-select">
            <option value="">전체</option>
            <option value="EQUIPMENT">설비 관련</option>
            <option value="PROCESS">공정 관련</option>
            <option value="QUALITY">품질 관련</option>
            <option value="SAFETY">안전 관련</option>
            <option value="MAINTENANCE">유지보수</option>
            <option value="OTHER">기타</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">검색</label>
          <input 
            v-model="filters.title" 
            @input="debounceSearch"
            type="text" 
            placeholder="제목으로 검색..."
            class="form-input"
          >
        </div>
      </div>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="loading" class="flex justify-center items-center py-8">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      <span class="ml-2 text-gray-600 dark:text-gray-400">데이터를 불러오는 중...</span>
    </div>

    <!-- 에러 메시지 -->
    <div v-if="error" class="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-md p-4">
      <div class="flex">
        <div class="flex-shrink-0">
          <ExclamationTriangleIcon class="h-5 w-5 text-red-400" />
        </div>
        <div class="ml-3">
          <h3 class="text-sm font-medium text-red-800 dark:text-red-200">오류가 발생했습니다</h3>
          <div class="mt-2 text-sm text-red-700 dark:text-red-300">
            {{ error }}
          </div>
          <div class="mt-3">
            <button @click="loadInforms" class="text-sm bg-red-100 dark:bg-red-800 text-red-800 dark:text-red-200 px-3 py-1 rounded-md hover:bg-red-200 dark:hover:bg-red-700">
              다시 시도
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 인폼 목록 -->
    <div v-if="!loading && !error" class="space-y-4">
      <div v-for="inform in informs" :key="inform.id" 
           class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6 hover:shadow-md transition-shadow">
        <div class="flex items-start justify-between">
          <div class="flex-1">
            <div class="flex items-center space-x-3 mb-2">
              <h3 class="text-lg font-medium text-gray-900 dark:text-white">{{ inform.title }}</h3>
              <span :class="getPriorityColor(inform.priority)" class="px-2 py-1 text-xs font-medium rounded-full">
                {{ getPriorityLabel(inform.priority) }}
              </span>
              <span :class="getStatusColor(inform.status)" class="px-2 py-1 text-xs font-medium rounded-full">
                {{ getStatusLabel(inform.status) }}
              </span>
              <span v-if="inform.urgent" class="px-2 py-1 text-xs font-medium rounded-full bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200">
                긴급
              </span>
            </div>
            
            <div class="flex items-center space-x-4 text-sm text-gray-500 dark:text-gray-400 mb-3">
              <span>ID: {{ inform.informId || inform.id }}</span>
              <span>작성자: {{ inform.author }}</span>
              <span>카테고리: {{ getCategoryLabel(inform.category) }}</span>
              <span>생성일: {{ formatDate(inform.createdAt) }}</span>
            </div>

            <p class="text-gray-700 dark:text-gray-300 mb-4 line-clamp-2">
              {{ inform.description }}
            </p>

            <div v-if="inform.gbm || inform.area || inform.line || inform.equipmentId" 
                 class="flex items-center space-x-4 text-sm text-gray-500 dark:text-gray-400">
              <span v-if="inform.gbm">GBM: {{ inform.gbm }}</span>
              <span v-if="inform.area">Area: {{ inform.area }}</span>
              <span v-if="inform.line">Line: {{ inform.line }}</span>
              <span v-if="inform.equipmentId">설비: {{ inform.equipmentId }}</span>
            </div>
          </div>

          <div class="flex items-center space-x-2 ml-4">
            <button 
              @click="viewInform(inform)"
              class="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300 text-sm font-medium"
            >
              상세보기
            </button>
            <button 
              @click="editInform(inform)"
              class="text-green-600 hover:text-green-900 dark:text-green-400 dark:hover:text-green-300 text-sm font-medium"
            >
              편집
            </button>
            <button 
              @click="deleteInform(inform.id)"
              class="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300 text-sm font-medium"
            >
              삭제
            </button>
          </div>
        </div>
      </div>

      <!-- 데이터가 없을 때 -->
      <div v-if="informs.length === 0" class="text-center py-8">
        <DocumentTextIcon class="mx-auto h-12 w-12 text-gray-400" />
        <h3 class="mt-2 text-sm font-medium text-gray-900 dark:text-white">인폼이 없습니다</h3>
        <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">새로운 인폼을 작성해보세요.</p>
        <div class="mt-6">
          <router-link to="/inform-write" class="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700">
            <PlusIcon class="-ml-1 mr-2 h-5 w-5" />
            새 인폼 작성
          </router-link>
        </div>
      </div>
    </div>

    <!-- 페이지네이션 -->
    <div v-if="totalPages > 1" class="flex items-center justify-between border-t border-gray-200 dark:border-gray-700 bg-white dark:bg-gray-800 px-4 py-3 sm:px-6 rounded-lg">
      <div class="flex flex-1 justify-between sm:hidden">
        <button 
          @click="changePage(currentPage - 1)"
          :disabled="currentPage <= 1"
          class="relative inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
        >
          이전
        </button>
        <button 
          @click="changePage(currentPage + 1)"
          :disabled="currentPage >= totalPages"
          class="relative ml-3 inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50"
        >
          다음
        </button>
      </div>
      <div class="hidden sm:flex sm:flex-1 sm:items-center sm:justify-between">
        <div>
          <p class="text-sm text-gray-700 dark:text-gray-300">
            총 <span class="font-medium">{{ totalCount }}</span>개 중 
            <span class="font-medium">{{ (currentPage - 1) * pageSize + 1 }}</span>-<span class="font-medium">{{ Math.min(currentPage * pageSize, totalCount) }}</span>개 표시
          </p>
        </div>
        <div>
          <nav class="relative z-0 inline-flex rounded-md shadow-sm -space-x-px">
            <button 
              @click="changePage(currentPage - 1)"
              :disabled="currentPage <= 1"
              class="relative inline-flex items-center px-2 py-2 rounded-l-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50"
            >
              이전
            </button>
            <button 
              v-for="page in visiblePages" 
              :key="page"
              @click="changePage(page)"
              :class="[
                page === currentPage 
                  ? 'z-10 bg-blue-50 border-blue-500 text-blue-600' 
                  : 'bg-white border-gray-300 text-gray-500 hover:bg-gray-50',
                'relative inline-flex items-center px-4 py-2 border text-sm font-medium'
              ]"
            >
              {{ page }}
            </button>
            <button 
              @click="changePage(currentPage + 1)"
              :disabled="currentPage >= totalPages"
              class="relative inline-flex items-center px-2 py-2 rounded-r-md border border-gray-300 bg-white text-sm font-medium text-gray-500 hover:bg-gray-50 disabled:opacity-50"
            >
              다음
            </button>
          </nav>
        </div>
      </div>
    </div>

    <!-- 상세보기 모달 -->
    <div v-if="selectedInform" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        <div class="p-6">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-medium text-gray-900 dark:text-white">인폼 상세정보</h3>
            <button @click="selectedInform = null" class="text-gray-400 hover:text-gray-600">
              <XMarkIcon class="w-6 h-6" />
            </button>
          </div>

          <div class="space-y-6">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">인폼 ID</label>
                <p class="mt-1 text-sm text-gray-900 dark:text-white">{{ selectedInform.informId || selectedInform.id }}</p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">제목</label>
                <p class="mt-1 text-sm text-gray-900 dark:text-white">{{ selectedInform.title }}</p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">작성자</label>
                <p class="mt-1 text-sm text-gray-900 dark:text-white">{{ selectedInform.author }}</p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">우선순위</label>
                <span :class="getPriorityColor(selectedInform.priority)" class="inline-block mt-1 px-2 py-1 text-xs font-medium rounded-full">
                  {{ getPriorityLabel(selectedInform.priority) }}
                </span>
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">상황 설명</label>
              <p class="mt-1 text-sm text-gray-900 dark:text-white whitespace-pre-wrap">{{ selectedInform.description }}</p>
            </div>

            <div v-if="selectedInform.actionTaken">
              <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">조치 사항</label>
              <p class="mt-1 text-sm text-gray-900 dark:text-white whitespace-pre-wrap">{{ selectedInform.actionTaken }}</p>
            </div>

            <div v-if="selectedInform.impactAssessment">
              <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">영향도</label>
              <p class="mt-1 text-sm text-gray-900 dark:text-white whitespace-pre-wrap">{{ selectedInform.impactAssessment }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  ExclamationTriangleIcon,
  DocumentTextIcon,
  PlusIcon,
  XMarkIcon
} from '@heroicons/vue/24/outline'
import { informsApi, apiUtils } from '../services/api.js'

export default {
  name: 'InformList',
  components: {
    ExclamationTriangleIcon,
    DocumentTextIcon,
    PlusIcon,
    XMarkIcon
  },
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const error = ref(null)
    const informs = ref([])
    const selectedInform = ref(null)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const totalCount = ref(0)
    const totalPages = ref(0)

    const filters = ref({
      status: '',
      priority: '',
      category: '',
      title: ''
    })

    let searchTimeout = null

    // API 호출 함수들
    const loadInforms = async () => {
      loading.value = true
      error.value = null
      try {
        const params = {
          page: currentPage.value - 1, // 백엔드는 0부터 시작
          size: pageSize.value,
          ...filters.value
        }

        // 빈 값 제거
        Object.keys(params).forEach(key => {
          if (params[key] === '' || params[key] === null || params[key] === undefined) {
            delete params[key]
          }
        })

        const response = await informsApi.getAll(params)
        const data = response.data
        
        informs.value = data.data || data || []
        totalCount.value = data.totalCount || 0
        totalPages.value = data.totalPages || Math.ceil(totalCount.value / pageSize.value)
        
        console.log('Informs loaded:', informs.value)
      } catch (err) {
        error.value = apiUtils.getErrorMessage(err)
        console.error('Failed to load informs:', err)
      } finally {
        loading.value = false
      }
    }

    const debounceSearch = () => {
      if (searchTimeout) {
        clearTimeout(searchTimeout)
      }
      searchTimeout = setTimeout(() => {
        currentPage.value = 1
        loadInforms()
      }, 500)
    }

    const changePage = (page) => {
      if (page >= 1 && page <= totalPages.value) {
        currentPage.value = page
        loadInforms()
      }
    }

    const viewInform = (inform) => {
      selectedInform.value = inform
    }

    const editInform = (inform) => {
      // 편집 페이지로 이동 (구현 필요)
      console.log('Edit inform:', inform.id)
    }

    const deleteInform = async (id) => {
      if (confirm('정말 삭제하시겠습니까?')) {
        try {
          await informsApi.delete(id)
          await loadInforms() // 목록 새로고침
        } catch (err) {
          error.value = apiUtils.getErrorMessage(err)
          console.error('Failed to delete inform:', err)
        }
      }
    }

    // UI 헬퍼 함수들
    const getPriorityColor = (priority) => {
      const colors = {
        LOW: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
        MEDIUM: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200',
        HIGH: 'bg-orange-100 text-orange-800 dark:bg-orange-900 dark:text-orange-200',
        CRITICAL: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200'
      }
      return colors[priority] || 'bg-gray-100 text-gray-800'
    }

    const getPriorityLabel = (priority) => {
      const labels = {
        LOW: '낮음',
        MEDIUM: '보통',
        HIGH: '높음',
        CRITICAL: '긴급'
      }
      return labels[priority] || priority
    }

    const getStatusColor = (status) => {
      const colors = {
        ACTIVE: 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200',
        RESOLVED: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
        CLOSED: 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
      }
      return colors[status] || 'bg-gray-100 text-gray-800'
    }

    const getStatusLabel = (status) => {
      const labels = {
        ACTIVE: '활성',
        RESOLVED: '해결됨',
        CLOSED: '종료'
      }
      return labels[status] || status
    }

    const getCategoryLabel = (category) => {
      const labels = {
        EQUIPMENT: '설비 관련',
        PROCESS: '공정 관련',
        QUALITY: '품질 관련',
        SAFETY: '안전 관련',
        MAINTENANCE: '유지보수',
        OTHER: '기타'
      }
      return labels[category] || category
    }

    const formatDate = (dateString) => {
      if (!dateString) return '-'
      return new Date(dateString).toLocaleDateString('ko-KR')
    }

    // 페이지네이션 표시할 페이지 번호들
    const visiblePages = computed(() => {
      const pages = []
      const start = Math.max(1, currentPage.value - 2)
      const end = Math.min(totalPages.value, currentPage.value + 2)
      
      for (let i = start; i <= end; i++) {
        pages.push(i)
      }
      return pages
    })

    // 컴포넌트 마운트 시 데이터 로드
    onMounted(() => {
      loadInforms()
    })

    return {
      loading,
      error,
      informs,
      selectedInform,
      currentPage,
      pageSize,
      totalCount,
      totalPages,
      filters,
      visiblePages,
      loadInforms,
      debounceSearch,
      changePage,
      viewInform,
      editInform,
      deleteInform,
      getPriorityColor,
      getPriorityLabel,
      getStatusColor,
      getStatusLabel,
      getCategoryLabel,
      formatDate
    }
  }
}
</script>

<style scoped>
.form-input {
  @apply block w-full border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm dark:bg-gray-700 dark:border-gray-600 dark:text-white;
}

.form-select {
  @apply block w-full border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm dark:bg-gray-700 dark:border-gray-600 dark:text-white;
}

.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
