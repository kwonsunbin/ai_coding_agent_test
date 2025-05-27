<template>
  <div class="space-y-6">
    <!-- PM/BM 이력 헤더 -->
    <div class="flex justify-between items-center">
      <h2 class="text-lg font-semibold text-gray-900 dark:text-white">설비 PM/BM 이력</h2>
      <button 
        @click="showAddModal = true"
        class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
      >
        + 새 이력 추가
      </button>
    </div>

    <!-- 필터 -->
    <div class="bg-white dark:bg-gray-800 shadow-sm rounded-lg p-4">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">설비 ID</label>
          <select v-model="filterEquipmentId" class="form-select">
            <option value="">전체</option>
            <option v-for="eqp in equipmentList" :key="eqp" :value="eqp">{{ eqp }}</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">유지보수 타입</label>
          <select v-model="filterType" class="form-select">
            <option value="">전체</option>
            <option value="PM">PM (Preventive Maintenance)</option>
            <option value="BM">BM (Breakdown Maintenance)</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">상태</label>
          <select v-model="filterStatus" class="form-select">
            <option value="">전체</option>
            <option value="scheduled">예정</option>
            <option value="inProgress">진행중</option>
            <option value="completed">완료</option>
            <option value="cancelled">취소</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">기간</label>
          <select v-model="filterPeriod" @change="applyPeriodFilter" class="form-select">
            <option value="all">전체</option>
            <option value="week">최근 1주</option>
            <option value="month">최근 1개월</option>
            <option value="quarter">최근 3개월</option>
          </select>
        </div>
      </div>
    </div>

    <!-- PM/BM 이력 테이블 -->
    <div class="bg-white dark:bg-gray-800 shadow-sm rounded-lg overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
          <thead class="bg-gray-50 dark:bg-gray-700">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                이력 ID
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                설비 ID
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                타입
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                작업 내용
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                담당자
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                상태
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                시작일시
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                완료일시
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                소요시간
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                액션
              </th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="record in filteredRecords" :key="record.id" class="hover:bg-gray-50 dark:hover:bg-gray-700">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-white">
                {{ record.id }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-white">
                {{ record.equipmentId }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getTypeColor(record.type)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ record.type }}
                </span>
              </td>
              <td class="px-6 py-4 text-sm text-gray-900 dark:text-white">
                <div class="max-w-xs truncate">{{ record.workDescription }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-white">
                {{ record.assignee }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusColor(record.status)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ getStatusLabel(record.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                {{ formatDateTime(record.startTime) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                {{ record.endTime ? formatDateTime(record.endTime) : '-' }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                {{ calculateDuration(record.startTime, record.endTime) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button 
                  @click="viewDetails(record)"
                  class="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300 mr-3"
                >
                  상세
                </button>
                <button 
                  @click="editRecord(record)"
                  class="text-green-600 hover:text-green-900 dark:text-green-400 dark:hover:text-green-300 mr-3"
                >
                  편집
                </button>
                <button 
                  @click="deleteRecord(record.id)"
                  class="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300"
                >
                  삭제
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 추가/편집 모달 -->
    <div v-if="showAddModal || editingRecord" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto">
        <div class="p-6">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-medium text-gray-900 dark:text-white">
              {{ editingRecord ? 'PM/BM 이력 편집' : '새 PM/BM 이력 추가' }}
            </h3>
            <button @click="closeModal" class="text-gray-400 hover:text-gray-600">
              <XMarkIcon class="w-6 h-6" />
            </button>
          </div>

          <form @submit.prevent="saveRecord" class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">설비 ID</label>
                <input 
                  v-model="formData.equipmentId" 
                  type="text" 
                  required
                  class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                >
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">유지보수 타입</label>
                <select 
                  v-model="formData.type" 
                  required
                  class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                >
                  <option value="PM">PM (Preventive Maintenance)</option>
                  <option value="BM">BM (Breakdown Maintenance)</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">담당자</label>
                <input 
                  v-model="formData.assignee" 
                  type="text" 
                  required
                  class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                >
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">상태</label>
                <select 
                  v-model="formData.status" 
                  required
                  class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                >
                  <option value="scheduled">예정</option>
                  <option value="inProgress">진행중</option>
                  <option value="completed">완료</option>
                  <option value="cancelled">취소</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">시작일시</label>
                <input 
                  v-model="formData.startTime" 
                  type="datetime-local" 
                  required
                  class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                >
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">완료일시</label>
                <input 
                  v-model="formData.endTime" 
                  type="datetime-local"
                  class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                >
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">작업 내용</label>
              <textarea 
                v-model="formData.workDescription" 
                rows="4"
                required
                class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
              ></textarea>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">상세 내용</label>
              <textarea 
                v-model="formData.details" 
                rows="6"
                placeholder="작업 상세 내용, 사용된 부품, 특이사항 등을 기록하세요..."
                class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
              ></textarea>
            </div>
            <div class="flex justify-end space-x-3 pt-4">
              <button 
                type="button" 
                @click="closeModal"
                class="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 hover:bg-gray-50 dark:border-gray-600 dark:text-gray-300 dark:hover:bg-gray-700"
              >
                취소
              </button>
              <button 
                type="submit"
                class="px-4 py-2 bg-blue-600 border border-transparent rounded-md text-sm font-medium text-white hover:bg-blue-700"
              >
                {{ editingRecord ? '수정' : '추가' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 상세보기 모달 -->
    <div v-if="viewingRecord" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-4xl w-full max-h-[90vh] overflow-y-auto">
        <div class="p-6">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-medium text-gray-900 dark:text-white">PM/BM 이력 상세</h3>
            <button @click="viewingRecord = null" class="text-gray-400 hover:text-gray-600">
              <XMarkIcon class="w-6 h-6" />
            </button>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">이력 ID</label>
                <p class="text-lg font-medium text-gray-900 dark:text-white">{{ viewingRecord.id }}</p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">설비 ID</label>
                <p class="text-lg font-medium text-gray-900 dark:text-white">{{ viewingRecord.equipmentId }}</p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">유지보수 타입</label>
                <span :class="getTypeColor(viewingRecord.type)" class="px-3 py-1 text-sm font-medium rounded-full">
                  {{ viewingRecord.type }}
                </span>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">담당자</label>
                <p class="text-lg font-medium text-gray-900 dark:text-white">{{ viewingRecord.assignee }}</p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">상태</label>
                <span :class="getStatusColor(viewingRecord.status)" class="px-3 py-1 text-sm font-medium rounded-full">
                  {{ getStatusLabel(viewingRecord.status) }}
                </span>
              </div>
            </div>
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">시작일시</label>
                <p class="text-lg font-medium text-gray-900 dark:text-white">{{ formatDateTime(viewingRecord.startTime) }}</p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">완료일시</label>
                <p class="text-lg font-medium text-gray-900 dark:text-white">
                  {{ viewingRecord.endTime ? formatDateTime(viewingRecord.endTime) : '미완료' }}
                </p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">소요시간</label>
                <p class="text-lg font-medium text-gray-900 dark:text-white">
                  {{ calculateDuration(viewingRecord.startTime, viewingRecord.endTime) }}
                </p>
              </div>
            </div>
          </div>

          <div class="mt-6 space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-500 dark:text-gray-400 mb-2">작업 내용</label>
              <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
                <p class="text-gray-900 dark:text-white">{{ viewingRecord.workDescription }}</p>
              </div>
            </div>
            <div v-if="viewingRecord.details">
              <label class="block text-sm font-medium text-gray-500 dark:text-gray-400 mb-2">상세 내용</label>
              <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
                <div class="prose dark:prose-invert max-w-none">
                  <div v-html="formatContent(viewingRecord.details)"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { XMarkIcon } from '@heroicons/vue/24/outline'

export default {
  name: 'PMBMHistory',
  components: {
    XMarkIcon
  },
  setup() {
    const showAddModal = ref(false)
    const editingRecord = ref(null)
    const viewingRecord = ref(null)
    const records = ref([])
    
    // 필터
    const filterEquipmentId = ref('')
    const filterType = ref('')
    const filterStatus = ref('')
    const filterPeriod = ref('all')

    const formData = ref({
      equipmentId: '',
      type: 'PM',
      assignee: '',
      status: 'scheduled',
      startTime: '',
      endTime: '',
      workDescription: '',
      details: ''
    })

    // 설비 목록
    const equipmentList = ['EQP-001', 'EQP-002', 'EQP-003', 'EQP-004', 'EQP-005']

    // 샘플 데이터
    const sampleData = [
      {
        id: 'PM-001',
        equipmentId: 'EQP-001',
        type: 'PM',
        workDescription: 'CVD 챔버 정기 점검 및 청소',
        assignee: '김유지',
        status: 'completed',
        startTime: '2024-01-15T09:00:00',
        endTime: '2024-01-15T15:30:00',
        details: '1. 챔버 내부 청소\n2. 가스 라인 점검\n3. 히터 상태 확인\n4. 센서 캘리브레이션\n\n사용 부품:\n- O-ring 교체 (2개)\n- 필터 교체 (1개)'
      },
      {
        id: 'BM-001',
        equipmentId: 'EQP-002',
        type: 'BM',
        workDescription: 'Etch 챔버 RF 제너레이터 고장 수리',
        assignee: '이수리',
        status: 'completed',
        startTime: '2024-01-16T14:00:00',
        endTime: '2024-01-17T11:00:00',
        details: 'RF 제너레이터 매칭 네트워크 불량으로 인한 수리\n\n교체 부품:\n- 매칭 네트워크 모듈\n- 커패시터 (3개)\n\n테스트 결과: 정상 동작 확인'
      },
      {
        id: 'PM-002',
        equipmentId: 'EQP-003',
        type: 'PM',
        workDescription: 'Litho 스캐너 정기 점검',
        assignee: '박정밀',
        status: 'inProgress',
        startTime: '2024-01-18T08:00:00',
        endTime: '',
        details: '진행 중인 작업:\n1. 레이저 출력 점검 ✓\n2. 렌즈 청소 ✓\n3. 스테이지 정밀도 측정 (진행중)\n4. 오버레이 정확도 확인 (예정)'
      }
    ]

    const filteredRecords = computed(() => {
      let filtered = records.value

      if (filterEquipmentId.value) {
        filtered = filtered.filter(record => record.equipmentId === filterEquipmentId.value)
      }

      if (filterType.value) {
        filtered = filtered.filter(record => record.type === filterType.value)
      }

      if (filterStatus.value) {
        filtered = filtered.filter(record => record.status === filterStatus.value)
      }

      return filtered.sort((a, b) => new Date(b.startTime) - new Date(a.startTime))
    })

    const getTypeColor = (type) => {
      return type === 'PM' 
        ? 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200'
        : 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200'
    }

    const getStatusColor = (status) => {
      const colors = {
        scheduled: 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200',
        inProgress: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200',
        completed: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
        cancelled: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200'
      }
      return colors[status] || 'bg-gray-100 text-gray-800'
    }

    const getStatusLabel = (status) => {
      const labels = {
        scheduled: '예정',
        inProgress: '진행중',
        completed: '완료',
        cancelled: '취소'
      }
      return labels[status] || status
    }

    const formatDateTime = (dateString) => {
      return new Date(dateString).toLocaleString('ko-KR')
    }

    const formatContent = (content) => {
      return content.replace(/\n/g, '<br>')
    }

    const calculateDuration = (startTime, endTime) => {
      if (!endTime) return '진행중'
      
      const start = new Date(startTime)
      const end = new Date(endTime)
      const diffMs = end - start
      const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
      const diffMinutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60))
      
      return `${diffHours}시간 ${diffMinutes}분`
    }

    const applyPeriodFilter = () => {
      // 기간 필터 로직 (실제 구현에서는 API 호출)
      console.log('Period filter applied:', filterPeriod.value)
    }

    const viewDetails = (record) => {
      viewingRecord.value = record
    }

    const editRecord = (record) => {
      editingRecord.value = record
      formData.value = { ...record }
    }

    const deleteRecord = (id) => {
      if (confirm('정말 삭제하시겠습니까?')) {
        records.value = records.value.filter(record => record.id !== id)
      }
    }

    const closeModal = () => {
      showAddModal.value = false
      editingRecord.value = null
      formData.value = {
        equipmentId: '',
        type: 'PM',
        assignee: '',
        status: 'scheduled',
        startTime: '',
        endTime: '',
        workDescription: '',
        details: ''
      }
    }

    const saveRecord = () => {
      if (editingRecord.value) {
        // 편집
        const index = records.value.findIndex(record => record.id === editingRecord.value.id)
        if (index !== -1) {
          records.value[index] = { ...formData.value }
        }
      } else {
        // 새로 추가
        const newRecord = {
          ...formData.value,
          id: `${formData.value.type}-${String(records.value.length + 1).padStart(3, '0')}`
        }
        records.value.push(newRecord)
      }
      closeModal()
    }

    onMounted(() => {
      records.value = [...sampleData]
    })

    return {
      showAddModal,
      editingRecord,
      viewingRecord,
      records,
      filterEquipmentId,
      filterType,
      filterStatus,
      filterPeriod,
      formData,
      equipmentList,
      filteredRecords,
      getTypeColor,
      getStatusColor,
      getStatusLabel,
      formatDateTime,
      formatContent,
      calculateDuration,
      applyPeriodFilter,
      viewDetails,
      editRecord,
      deleteRecord,
      closeModal,
      saveRecord
    }
  }
}
</script>

<style scoped>
.form-select {
  @apply block w-full text-sm border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white;
}
</style>
