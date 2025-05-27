<template>
  <div class="space-y-6">
    <!-- Down 이력 헤더 -->
    <div class="flex justify-between items-center">
      <h2 class="text-lg font-semibold text-gray-900 dark:text-white">설비 Down 이력</h2>
      <button 
        @click="showAddModal = true"
        class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
      >
        + 새 Down 이력 추가
      </button>
    </div>

    <!-- 통계 카드 -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-4 border border-gray-200 dark:border-gray-700">
        <div class="flex items-center">
          <div class="p-2 bg-red-100 dark:bg-red-900 rounded-lg">
            <ExclamationTriangleIcon class="w-6 h-6 text-red-600 dark:text-red-400" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-500 dark:text-gray-400">총 Down 건수</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ totalDownCount }}</p>
          </div>
        </div>
      </div>
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-4 border border-gray-200 dark:border-gray-700">
        <div class="flex items-center">
          <div class="p-2 bg-yellow-100 dark:bg-yellow-900 rounded-lg">
            <ClockIcon class="w-6 h-6 text-yellow-600 dark:text-yellow-400" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-500 dark:text-gray-400">총 Down 시간</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ totalDownTime }}</p>
          </div>
        </div>
      </div>
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-4 border border-gray-200 dark:border-gray-700">
        <div class="flex items-center">
          <div class="p-2 bg-blue-100 dark:bg-blue-900 rounded-lg">
            <ChartBarIcon class="w-6 h-6 text-blue-600 dark:text-blue-400" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-500 dark:text-gray-400">평균 Down 시간</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ averageDownTime }}</p>
          </div>
        </div>
      </div>
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm p-4 border border-gray-200 dark:border-gray-700">
        <div class="flex items-center">
          <div class="p-2 bg-green-100 dark:bg-green-900 rounded-lg">
            <CheckCircleIcon class="w-6 h-6 text-green-600 dark:text-green-400" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-gray-500 dark:text-gray-400">복구율</p>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ recoveryRate }}%</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 필터 -->
    <div class="bg-white dark:bg-gray-800 shadow-sm rounded-lg p-4">
      <div class="grid grid-cols-1 md:grid-cols-5 gap-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">설비 ID</label>
          <select v-model="filterEquipmentId" class="form-select">
            <option value="">전체</option>
            <option v-for="eqp in equipmentList" :key="eqp" :value="eqp">{{ eqp }}</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Down 유형</label>
          <select v-model="filterDownType" class="form-select">
            <option value="">전체</option>
            <option value="equipment">설비 고장</option>
            <option value="process">공정 이상</option>
            <option value="utility">유틸리티</option>
            <option value="material">재료 부족</option>
            <option value="planned">계획 정지</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">상태</label>
          <select v-model="filterStatus" class="form-select">
            <option value="">전체</option>
            <option value="ongoing">진행중</option>
            <option value="resolved">복구완료</option>
            <option value="investigating">조사중</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">심각도</label>
          <select v-model="filterSeverity" class="form-select">
            <option value="">전체</option>
            <option value="critical">Critical</option>
            <option value="high">High</option>
            <option value="medium">Medium</option>
            <option value="low">Low</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">기간</label>
          <select v-model="filterPeriod" @change="applyPeriodFilter" class="form-select">
            <option value="all">전체</option>
            <option value="today">오늘</option>
            <option value="week">최근 1주</option>
            <option value="month">최근 1개월</option>
          </select>
        </div>
      </div>
    </div>

    <!-- Down 이력 테이블 -->
    <div class="bg-white dark:bg-gray-800 shadow-sm rounded-lg overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
          <thead class="bg-gray-50 dark:bg-gray-700">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                Down ID
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                설비 ID
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                Down 유형
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                심각도
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                설명
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                상태
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                시작시간
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                복구시간
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                Down 시간
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                담당자
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
                <span :class="getDownTypeColor(record.downType)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ getDownTypeLabel(record.downType) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getSeverityColor(record.severity)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ record.severity }}
                </span>
              </td>
              <td class="px-6 py-4 text-sm text-gray-900 dark:text-white">
                <div class="max-w-xs truncate">{{ record.description }}</div>
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
                {{ calculateDownTime(record.startTime, record.endTime) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-white">
                {{ record.assignee }}
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
              {{ editingRecord ? 'Down 이력 편집' : '새 Down 이력 추가' }}
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
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Down 유형</label>
                <select 
                  v-model="formData.downType" 
                  required
                  class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                >
                  <option value="equipment">설비 고장</option>
                  <option value="process">공정 이상</option>
                  <option value="utility">유틸리티</option>
                  <option value="material">재료 부족</option>
                  <option value="planned">계획 정지</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">심각도</label>
                <select 
                  v-model="formData.severity" 
                  required
                  class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                >
                  <option value="critical">Critical</option>
                  <option value="high">High</option>
                  <option value="medium">Medium</option>
                  <option value="low">Low</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">상태</label>
                <select 
                  v-model="formData.status" 
                  required
                  class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                >
                  <option value="ongoing">진행중</option>
                  <option value="resolved">복구완료</option>
                  <option value="investigating">조사중</option>
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
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">시작시간</label>
                <input 
                  v-model="formData.startTime" 
                  type="datetime-local" 
                  required
                  class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                >
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">복구시간</label>
                <input 
                  v-model="formData.endTime" 
                  type="datetime-local"
                  class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                >
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">설명</label>
              <textarea 
                v-model="formData.description" 
                rows="4"
                required
                placeholder="Down 발생 원인 및 상황을 상세히 기록하세요..."
                class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
              ></textarea>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">조치 내용</label>
              <textarea 
                v-model="formData.actionTaken" 
                rows="4"
                placeholder="수행한 조치 사항을 기록하세요..."
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
            <h3 class="text-lg font-medium text-gray-900 dark:text-white">Down 이력 상세</h3>
            <button @click="viewingRecord = null" class="text-gray-400 hover:text-gray-600">
              <XMarkIcon class="w-6 h-6" />
            </button>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">Down ID</label>
                <p class="text-lg font-medium text-gray-900 dark:text-white">{{ viewingRecord.id }}</p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">설비 ID</label>
                <p class="text-lg font-medium text-gray-900 dark:text-white">{{ viewingRecord.equipmentId }}</p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">Down 유형</label>
                <span :class="getDownTypeColor(viewingRecord.downType)" class="px-3 py-1 text-sm font-medium rounded-full">
                  {{ getDownTypeLabel(viewingRecord.downType) }}
                </span>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">심각도</label>
                <span :class="getSeverityColor(viewingRecord.severity)" class="px-3 py-1 text-sm font-medium rounded-full">
                  {{ viewingRecord.severity }}
                </span>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">담당자</label>
                <p class="text-lg font-medium text-gray-900 dark:text-white">{{ viewingRecord.assignee }}</p>
              </div>
            </div>
            <div class="space-y-4">
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">상태</label>
                <span :class="getStatusColor(viewingRecord.status)" class="px-3 py-1 text-sm font-medium rounded-full">
                  {{ getStatusLabel(viewingRecord.status) }}
                </span>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">시작시간</label>
                <p class="text-lg font-medium text-gray-900 dark:text-white">{{ formatDateTime(viewingRecord.startTime) }}</p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">복구시간</label>
                <p class="text-lg font-medium text-gray-900 dark:text-white">
                  {{ viewingRecord.endTime ? formatDateTime(viewingRecord.endTime) : '미복구' }}
                </p>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-500 dark:text-gray-400">Down 시간</label>
                <p class="text-lg font-medium text-gray-900 dark:text-white">
                  {{ calculateDownTime(viewingRecord.startTime, viewingRecord.endTime) }}
                </p>
              </div>
            </div>
          </div>

          <div class="mt-6 space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-500 dark:text-gray-400 mb-2">설명</label>
              <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
                <p class="text-gray-900 dark:text-white">{{ viewingRecord.description }}</p>
              </div>
            </div>
            <div v-if="viewingRecord.actionTaken">
              <label class="block text-sm font-medium text-gray-500 dark:text-gray-400 mb-2">조치 내용</label>
              <div class="bg-gray-50 dark:bg-gray-700 rounded-lg p-4">
                <div class="prose dark:prose-invert max-w-none">
                  <div v-html="formatContent(viewingRecord.actionTaken)"></div>
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
import { 
  XMarkIcon, 
  ExclamationTriangleIcon, 
  ClockIcon, 
  ChartBarIcon, 
  CheckCircleIcon 
} from '@heroicons/vue/24/outline'

export default {
  name: 'DownHistory',
  components: {
    XMarkIcon,
    ExclamationTriangleIcon,
    ClockIcon,
    ChartBarIcon,
    CheckCircleIcon
  },
  setup() {
    const showAddModal = ref(false)
    const editingRecord = ref(null)
    const viewingRecord = ref(null)
    const records = ref([])
    
    // 필터
    const filterEquipmentId = ref('')
    const filterDownType = ref('')
    const filterStatus = ref('')
    const filterSeverity = ref('')
    const filterPeriod = ref('all')

    const formData = ref({
      equipmentId: '',
      downType: 'equipment',
      severity: 'medium',
      status: 'ongoing',
      assignee: '',
      startTime: '',
      endTime: '',
      description: '',
      actionTaken: ''
    })

    // 설비 목록
    const equipmentList = ['EQP-001', 'EQP-002', 'EQP-003', 'EQP-004', 'EQP-005']

    // 샘플 데이터
    const sampleData = [
      {
        id: 'DOWN-001',
        equipmentId: 'EQP-001',
        downType: 'equipment',
        severity: 'critical',
        description: 'CVD 챔버 히터 모듈 고장으로 인한 온도 제어 불가',
        status: 'resolved',
        assignee: '김수리',
        startTime: '2024-01-15T14:30:00',
        endTime: '2024-01-15T18:45:00',
        actionTaken: '1. 히터 모듈 교체\n2. 온도 센서 재캘리브레이션\n3. 테스트 런 수행\n\n교체 부품: 히터 모듈 (P/N: HTR-001)'
      },
      {
        id: 'DOWN-002',
        equipmentId: 'EQP-002',
        downType: 'process',
        severity: 'high',
        description: 'Etch 공정 중 플라즈마 불안정으로 인한 웨이퍼 손상',
        status: 'resolved',
        assignee: '이공정',
        startTime: '2024-01-16T10:15:00',
        endTime: '2024-01-16T16:30:00',
        actionTaken: 'RF 매칭 네트워크 조정 및 가스 플로우 최적화'
      },
      {
        id: 'DOWN-003',
        equipmentId: 'EQP-003',
        downType: 'utility',
        severity: 'medium',
        description: '냉각수 공급 중단으로 인한 설비 온도 상승',
        status: 'ongoing',
        assignee: '박유틸',
        startTime: '2024-01-18T09:00:00',
        endTime: '',
        actionTaken: '냉각수 펌프 점검 중'
      },
      {
        id: 'DOWN-004',
        equipmentId: 'EQP-001',
        downType: 'planned',
        severity: 'low',
        description: '정기 예방 정비를 위한 계획 정지',
        status: 'resolved',
        assignee: '김유지',
        startTime: '2024-01-17T20:00:00',
        endTime: '2024-01-18T06:00:00',
        actionTaken: '정기 점검 및 소모품 교체 완료'
      }
    ]

    const filteredRecords = computed(() => {
      let filtered = records.value

      if (filterEquipmentId.value) {
        filtered = filtered.filter(record => record.equipmentId === filterEquipmentId.value)
      }

      if (filterDownType.value) {
        filtered = filtered.filter(record => record.downType === filterDownType.value)
      }

      if (filterStatus.value) {
        filtered = filtered.filter(record => record.status === filterStatus.value)
      }

      if (filterSeverity.value) {
        filtered = filtered.filter(record => record.severity === filterSeverity.value)
      }

      return filtered.sort((a, b) => new Date(b.startTime) - new Date(a.startTime))
    })

    // 통계 계산
    const totalDownCount = computed(() => records.value.length)
    
    const totalDownTime = computed(() => {
      const totalMinutes = records.value.reduce((total, record) => {
        if (record.endTime) {
          const start = new Date(record.startTime)
          const end = new Date(record.endTime)
          return total + (end - start) / (1000 * 60)
        }
        return total
      }, 0)
      
      const hours = Math.floor(totalMinutes / 60)
      return `${hours}시간`
    })

    const averageDownTime = computed(() => {
      const resolvedRecords = records.value.filter(record => record.endTime)
      if (resolvedRecords.length === 0) return '0시간'
      
      const totalMinutes = resolvedRecords.reduce((total, record) => {
        const start = new Date(record.startTime)
        const end = new Date(record.endTime)
        return total + (end - start) / (1000 * 60)
      }, 0)
      
      const avgMinutes = totalMinutes / resolvedRecords.length
      const hours = Math.floor(avgMinutes / 60)
      const minutes = Math.floor(avgMinutes % 60)
      return `${hours}시간 ${minutes}분`
    })

    const recoveryRate = computed(() => {
      const resolvedCount = records.value.filter(record => record.status === 'resolved').length
      return records.value.length > 0 ? Math.round((resolvedCount / records.value.length) * 100) : 0
    })

    const getDownTypeColor = (type) => {
      const colors = {
        equipment: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200',
        process: 'bg-orange-100 text-orange-800 dark:bg-orange-900 dark:text-orange-200',
        utility: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200',
        material: 'bg-purple-100 text-purple-800 dark:bg-purple-900 dark:text-purple-200',
        planned: 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200'
      }
      return colors[type] || 'bg-gray-100 text-gray-800'
    }

    const getDownTypeLabel = (type) => {
      const labels = {
        equipment: '설비 고장',
        process: '공정 이상',
        utility: '유틸리티',
        material: '재료 부족',
        planned: '계획 정지'
      }
      return labels[type] || type
    }

    const getSeverityColor = (severity) => {
      const colors = {
        critical: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200',
        high: 'bg-orange-100 text-orange-800 dark:bg-orange-900 dark:text-orange-200',
        medium: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200',
        low: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200'
      }
      return colors[severity] || 'bg-gray-100 text-gray-800'
    }

    const getStatusColor = (status) => {
      const colors = {
        ongoing: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200',
        resolved: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
        investigating: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200'
      }
      return colors[status] || 'bg-gray-100 text-gray-800'
    }

    const getStatusLabel = (status) => {
      const labels = {
        ongoing: '진행중',
        resolved: '복구완료',
        investigating: '조사중'
      }
      return labels[status] || status
    }

    const formatDateTime = (dateString) => {
      return new Date(dateString).toLocaleString('ko-KR')
    }

    const formatContent = (content) => {
      return content.replace(/\n/g, '<br>')
    }

    const calculateDownTime = (startTime, endTime) => {
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
        downType: 'equipment',
        severity: 'medium',
        status: 'ongoing',
        assignee: '',
        startTime: '',
        endTime: '',
        description: '',
        actionTaken: ''
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
          id: `DOWN-${String(records.value.length + 1).padStart(3, '0')}`
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
      filterDownType,
      filterStatus,
      filterSeverity,
      filterPeriod,
      formData,
      equipmentList,
      filteredRecords,
      totalDownCount,
      totalDownTime,
      averageDownTime,
      recoveryRate,
      getDownTypeColor,
      getDownTypeLabel,
      getSeverityColor,
      getStatusColor,
      getStatusLabel,
      formatDateTime,
      formatContent,
      calculateDownTime,
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
