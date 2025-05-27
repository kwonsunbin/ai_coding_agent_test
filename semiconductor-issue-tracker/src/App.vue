<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-secondary-50 dark:from-gray-900 dark:to-gray-800 transition-colors duration-300">
    <!-- 헤더 -->
    <header class="bg-white/80 dark:bg-gray-800/80 backdrop-blur-md border-b border-blue-100/50 dark:border-gray-700/50 shadow-soft sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <!-- 로고 -->
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <h1 class="text-xl font-bold gradient-text">반도체 설비 이슈 트래커</h1>
            </div>
          </div>

          <!-- 사용자 정보 -->
          <div class="flex items-center space-x-4">
            <!-- 다크모드 토글 -->
            <button 
              @click="toggleDarkMode"
              class="p-2 rounded-lg hover:bg-blue-50 transition-colors"
              :title="isDarkMode ? '라이트 모드로 전환' : '다크 모드로 전환'"
            >
              <SunIcon v-if="isDarkMode" class="w-5 h-5 text-yellow-500" />
              <MoonIcon v-else class="w-5 h-5 text-blue-600" />
            </button>
            
            <div class="flex items-center space-x-2">
              <div class="w-8 h-8 bg-gradient-to-r from-blue-500 to-blue-600 rounded-full flex items-center justify-center">
                <UserIcon class="w-5 h-5 text-white" />
              </div>
              <div class="text-sm">
                <div class="font-medium text-secondary-900 dark:text-white">설비 엔지니어</div>
                <div class="text-secondary-500 dark:text-gray-400">이슈 관리팀</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- 조회 조건 바 (첫 번째 줄) -->
    <div class="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 shadow-sm">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
        <div class="grid grid-cols-2 md:grid-cols-5 lg:grid-cols-10 gap-4">
          <div>
            <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">GBM</label>
            <select v-model="filters.gbm" class="form-select-sm">
              <option value="">전체</option>
              <option value="GBM1">GBM1</option>
              <option value="GBM2">GBM2</option>
              <option value="GBM3">GBM3</option>
            </select>
          </div>
          <div>
            <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Area</label>
            <select v-model="filters.area" class="form-select-sm">
              <option value="">전체</option>
              <option value="FAB1">FAB1</option>
              <option value="FAB2">FAB2</option>
              <option value="FAB3">FAB3</option>
            </select>
          </div>
          <div>
            <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">Line</label>
            <select v-model="filters.line" class="form-select-sm">
              <option value="">전체</option>
              <option value="LINE1">LINE1</option>
              <option value="LINE2">LINE2</option>
              <option value="LINE3">LINE3</option>
            </select>
          </div>
          <div>
            <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">SDWT</label>
            <select v-model="filters.sdwt" class="form-select-sm">
              <option value="">전체</option>
              <option value="SDWT1">SDWT1</option>
              <option value="SDWT2">SDWT2</option>
              <option value="SDWT3">SDWT3</option>
            </select>
          </div>
          <div>
            <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">EQP Model</label>
            <select v-model="filters.eqpModel" class="form-select-sm">
              <option value="">전체</option>
              <option value="CVD">CVD</option>
              <option value="ETCH">ETCH</option>
              <option value="LITHO">LITHO</option>
            </select>
          </div>
          <div>
            <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">PRC Group</label>
            <select v-model="filters.prcGroup" class="form-select-sm">
              <option value="">전체</option>
              <option value="PRC1">PRC1</option>
              <option value="PRC2">PRC2</option>
              <option value="PRC3">PRC3</option>
            </select>
          </div>
          <div>
            <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">EQP ID</label>
            <select v-model="filters.eqpId" class="form-select-sm">
              <option value="">전체</option>
              <option value="EQP001">EQP001</option>
              <option value="EQP002">EQP002</option>
              <option value="EQP003">EQP003</option>
            </select>
          </div>
          <div>
            <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">FDC Model</label>
            <select v-model="filters.fdcModel" class="form-select-sm">
              <option value="">전체</option>
              <option value="FDC1">FDC1</option>
              <option value="FDC2">FDC2</option>
              <option value="FDC3">FDC3</option>
            </select>
          </div>
          <div>
            <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">From</label>
            <input v-model="filters.from" type="date" class="form-input-sm">
          </div>
          <div>
            <label class="block text-xs font-medium text-gray-700 dark:text-gray-300 mb-1">To</label>
            <input v-model="filters.to" type="date" class="form-input-sm">
          </div>
        </div>
      </div>
    </div>

    <!-- 이슈 타입 및 상태 체크박스 (두 번째 줄) -->
    <div class="bg-gray-50 dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-3">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <!-- Issue Type -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Issue Type</label>
            <div class="flex flex-wrap gap-3">
              <label v-for="type in issueTypes" :key="type.value" class="flex items-center">
                <input 
                  type="checkbox" 
                  :value="type.value" 
                  v-model="selectedIssueTypes"
                  class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                >
                <span class="ml-2 text-sm text-gray-700 dark:text-gray-300">{{ type.label }}</span>
              </label>
            </div>
          </div>

          <!-- Status -->
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Status</label>
            <div class="flex flex-wrap gap-3">
              <label v-for="status in statusOptions" :key="status.value" class="flex items-center">
                <input 
                  type="checkbox" 
                  :value="status.value" 
                  v-model="selectedStatuses"
                  class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                >
                <span class="ml-2 text-sm text-gray-700 dark:text-gray-300">{{ status.label }}</span>
              </label>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 메인 콘텐츠 -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <!-- 탭 네비게이션 -->
      <div class="mb-6">
        <nav class="flex space-x-8" aria-label="Tabs">
          <button
            v-for="tab in tabs"
            :key="tab.id"
            @click="activeTab = tab.id"
            :class="[
              activeTab === tab.id
                ? 'border-blue-500 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300',
              'whitespace-nowrap py-2 px-1 border-b-2 font-medium text-sm'
            ]"
          >
            {{ tab.name }}
          </button>
        </nav>
      </div>

      <!-- 탭 콘텐츠 -->
      <div class="tab-content">
        <component :is="currentTabComponent" />
      </div>
    </main>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { 
  SunIcon,
  MoonIcon,
  UserIcon
} from '@heroicons/vue/24/outline'

// 탭 컴포넌트들
import ActionItems from './views/ActionItems.vue'
import Comments from './views/Comments.vue'
import PMBMHistory from './views/PMBMHistory.vue'
import DownHistory from './views/DownHistory.vue'
import RACBHistory from './views/RACBHistory.vue'
import InformWrite from './views/InformWrite.vue'
import InformList from './views/InformList.vue'

export default {
  name: 'App',
  components: {
    SunIcon,
    MoonIcon,
    UserIcon,
    ActionItems,
    Comments,
    PMBMHistory,
    DownHistory,
    RACBHistory,
    InformWrite,
    InformList
  },
  setup() {
    const isDarkMode = ref(false)
    const activeTab = ref('actionItems')

    // 필터 상태
    const filters = ref({
      gbm: '',
      area: '',
      line: '',
      sdwt: '',
      eqpModel: '',
      prcGroup: '',
      eqpId: '',
      fdcModel: '',
      from: '',
      to: ''
    })

    // 이슈 타입 옵션
    const issueTypes = ref([
      { value: 'equipment', label: '설비 이슈' },
      { value: 'process', label: '공정 이슈' },
      { value: 'quality', label: '품질 이슈' },
      { value: 'maintenance', label: '유지보수' },
      { value: 'fdc', label: 'FDC 알람' }
    ])

    // 상태 옵션
    const statusOptions = ref([
      { value: 'open', label: 'Open' },
      { value: 'inProgress', label: 'In Progress' },
      { value: 'resolved', label: 'Resolved' },
      { value: 'closed', label: 'Closed' },
      { value: 'pending', label: 'Pending' }
    ])

    // 선택된 필터
    const selectedIssueTypes = ref(['equipment', 'process', 'quality', 'maintenance', 'fdc'])
    const selectedStatuses = ref(['open', 'inProgress', 'resolved', 'closed', 'pending'])

    // 탭 정의
    const tabs = ref([
      { id: 'actionItems', name: 'Action Items', component: 'ActionItems' },
      { id: 'comments', name: 'Comments', component: 'Comments' },
      { id: 'pmBmHistory', name: 'PM/BM 이력', component: 'PMBMHistory' },
      { id: 'downHistory', name: 'Down 이력', component: 'DownHistory' },
      { id: 'racbHistory', name: 'RACB 이력', component: 'RACBHistory' },
      { id: 'informWrite', name: '인폼 작성', component: 'InformWrite' },
      { id: 'informList', name: '인폼 조회', component: 'InformList' }
    ])

    // 현재 활성 탭 컴포넌트
    const currentTabComponent = computed(() => {
      const tab = tabs.value.find(t => t.id === activeTab.value)
      return tab ? tab.component : 'ActionItems'
    })

    const toggleDarkMode = () => {
      isDarkMode.value = !isDarkMode.value
      if (isDarkMode.value) {
        document.documentElement.classList.add('dark')
        localStorage.setItem('darkMode', 'true')
      } else {
        document.documentElement.classList.remove('dark')
        localStorage.setItem('darkMode', 'false')
      }
    }

    onMounted(() => {
      // 다크모드 설정 복원
      const savedDarkMode = localStorage.getItem('darkMode')
      if (savedDarkMode === 'true') {
        isDarkMode.value = true
        document.documentElement.classList.add('dark')
      }

      // 기본 날짜 설정 (최근 30일)
      const today = new Date()
      const thirtyDaysAgo = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000)
      
      filters.value.to = today.toISOString().split('T')[0]
      filters.value.from = thirtyDaysAgo.toISOString().split('T')[0]
    })

    return {
      isDarkMode,
      activeTab,
      filters,
      issueTypes,
      statusOptions,
      selectedIssueTypes,
      selectedStatuses,
      tabs,
      currentTabComponent,
      toggleDarkMode
    }
  }
}
</script>

<style>
/* 기본 스타일 */
.gradient-text {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.shadow-soft {
  box-shadow: 0 2px 15px -3px rgba(0, 0, 0, 0.07), 0 10px 20px -2px rgba(0, 0, 0, 0.04);
}

/* 폼 요소 스타일 */
.form-select-sm {
  @apply block w-full text-xs border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white;
  padding: 0.25rem 0.5rem;
  height: 2rem;
}

.form-input-sm {
  @apply block w-full text-xs border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white;
  padding: 0.25rem 0.5rem;
  height: 2rem;
}

/* 탭 콘텐츠 */
.tab-content {
  min-height: 500px;
}
</style>
