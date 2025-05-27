<template>
  <div class="space-y-6 animate-fade-in-up">
    <!-- 페이지 헤더 -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <div>
        <h1 class="text-2xl font-bold gradient-text">설비 목록</h1>
        <p class="mt-1 text-secondary-600">전체 반도체 설비 현황 및 관리</p>
      </div>
      <div class="mt-4 sm:mt-0">
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
          <label class="block text-sm font-medium text-secondary-700 mb-2">검색</label>
          <div class="relative">
            <MagnifyingGlassIcon class="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-secondary-400" />
            <input
              v-model="searchQuery"
              type="text"
              placeholder="설비명 검색..."
              class="input-field pl-10"
            />
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium text-secondary-700 mb-2">상태</label>
          <select v-model="statusFilter" class="select-field">
            <option value="">전체</option>
            <option value="normal">정상</option>
            <option value="warning">경고</option>
            <option value="critical">위험</option>
            <option value="maintenance">정비</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-secondary-700 mb-2">타입</label>
          <select v-model="typeFilter" class="select-field">
            <option value="">전체</option>
            <option value="CVD">CVD</option>
            <option value="ETCH">ETCH</option>
            <option value="CMP">CMP</option>
            <option value="LITHO">LITHO</option>
            <option value="ION">ION</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-secondary-700 mb-2">정렬</label>
          <select v-model="sortBy" class="select-field">
            <option value="name">설비명</option>
            <option value="efficiency">가동률</option>
            <option value="lastMaintenance">마지막 점검</option>
            <option value="status">상태</option>
          </select>
        </div>
      </div>
    </div>

    <!-- 설비 통계 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-blue-500 to-blue-600 rounded-lg">
            <CogIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">총 설비</p>
            <p class="text-2xl font-semibold text-secondary-900">{{ filteredEquipment.length }}</p>
          </div>
        </div>
      </div>
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-success-500 to-success-600 rounded-lg">
            <CheckCircleIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">정상 가동</p>
            <p class="text-2xl font-semibold text-secondary-900">{{ normalCount }}</p>
          </div>
        </div>
      </div>
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-warning-500 to-warning-600 rounded-lg">
            <ExclamationTriangleIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">경고</p>
            <p class="text-2xl font-semibold text-secondary-900">{{ warningCount }}</p>
          </div>
        </div>
      </div>
      <div class="metric-card">
        <div class="flex items-center">
          <div class="p-3 bg-gradient-to-r from-danger-500 to-danger-600 rounded-lg">
            <XCircleIcon class="w-6 h-6 text-white" />
          </div>
          <div class="ml-4">
            <p class="text-sm font-medium text-secondary-600">위험</p>
            <p class="text-2xl font-semibold text-secondary-900">{{ criticalCount }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 설비 목록 테이블 -->
    <div class="card">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-secondary-200">
          <thead class="table-header">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">설비명</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">타입</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">상태</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">가동률</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">예측</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">마지막 점검</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">랏 이력</th>
              <th class="px-6 py-3 text-left text-xs font-medium uppercase tracking-wider">액션</th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-secondary-200">
            <tr v-for="equipment in paginatedEquipment" :key="equipment.id" class="table-row">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="text-sm font-medium text-secondary-900">{{ equipment.name }}</div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-800">
                  {{ equipment.type }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="status-badge" :class="getStatusClass(equipment.status)">
                  {{ getStatusText(equipment.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="text-sm text-secondary-900 mr-2">{{ equipment.efficiency }}%</div>
                  <div class="progress-bar w-20">
                    <div class="progress-fill" :class="getProgressClass(equipment.efficiency)" :style="`width: ${equipment.efficiency}%`"></div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span class="status-badge" :class="getPredictionClass(equipment.prediction)">
                  {{ getPredictionText(equipment.prediction) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-secondary-500">
                {{ equipment.lastMaintenance }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <router-link :to="`/equipment/${equipment.id}/lots`" class="btn-ghost text-sm">
                  <ClipboardDocumentListIcon class="w-4 h-4 mr-1" />
                  랏 이력
                </router-link>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <div class="flex space-x-2">
                  <router-link :to="`/equipment/${equipment.id}`" class="btn-ghost text-sm">
                    상세보기
                  </router-link>
                  <button class="btn-ghost text-sm text-warning-600 hover:text-warning-700">
                    정비
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 페이지네이션 -->
      <div class="flex items-center justify-between px-6 py-3 border-t border-secondary-200">
        <div class="flex items-center text-sm text-secondary-500">
          <span>총 {{ filteredEquipment.length }}개 중 {{ startIndex + 1 }}-{{ endIndex }}개 표시</span>
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
import { ref, computed, onMounted } from 'vue'
import {
  CogIcon,
  CheckCircleIcon,
  ExclamationTriangleIcon,
  XCircleIcon,
  PlusIcon,
  MagnifyingGlassIcon,
  ClipboardDocumentListIcon
} from '@heroicons/vue/24/outline'

export default {
  name: 'EquipmentList',
  components: {
    CogIcon,
    CheckCircleIcon,
    ExclamationTriangleIcon,
    XCircleIcon,
    PlusIcon,
    MagnifyingGlassIcon,
    ClipboardDocumentListIcon
  },
  setup() {
    const searchQuery = ref('')
    const statusFilter = ref('')
    const typeFilter = ref('')
    const sortBy = ref('name')
    const currentPage = ref(1)
    const itemsPerPage = ref(10)

    const equipment = ref([
      {
        id: 'cvd-001',
        name: 'CVD-001',
        type: 'CVD',
        status: 'critical',
        efficiency: 85,
        prediction: 'critical',
        lastMaintenance: '2025.05.16'
      },
      {
        id: 'etch-003',
        name: 'ETCH-003',
        type: 'ETCH',
        status: 'warning',
        efficiency: 92,
        prediction: 'warning',
        lastMaintenance: '2025.05.09'
      },
      {
        id: 'cmp-002',
        name: 'CMP-002',
        type: 'CMP',
        status: 'normal',
        efficiency: 98,
        prediction: 'normal',
        lastMaintenance: '2025.05.20'
      },
      {
        id: 'litho-001',
        name: 'LITHO-001',
        type: 'LITHO',
        status: 'normal',
        efficiency: 96,
        prediction: 'normal',
        lastMaintenance: '2025.05.18'
      },
      {
        id: 'ion-004',
        name: 'ION-004',
        type: 'ION',
        status: 'normal',
        efficiency: 94,
        prediction: 'normal',
        lastMaintenance: '2025.05.15'
      },
      {
        id: 'cvd-002',
        name: 'CVD-002',
        type: 'CVD',
        status: 'maintenance',
        efficiency: 0,
        prediction: 'normal',
        lastMaintenance: '2025.05.22'
      }
    ])

    const filteredEquipment = computed(() => {
      let filtered = equipment.value

      // 검색 필터
      if (searchQuery.value) {
        filtered = filtered.filter(eq =>
          eq.name.toLowerCase().includes(searchQuery.value.toLowerCase())
        )
      }

      // 상태 필터
      if (statusFilter.value) {
        filtered = filtered.filter(eq => eq.status === statusFilter.value)
      }

      // 타입 필터
      if (typeFilter.value) {
        filtered = filtered.filter(eq => eq.type === typeFilter.value)
      }

      // 정렬
      filtered.sort((a, b) => {
        switch (sortBy.value) {
          case 'name':
            return a.name.localeCompare(b.name)
          case 'efficiency':
            return b.efficiency - a.efficiency
          case 'lastMaintenance':
            return new Date(b.lastMaintenance) - new Date(a.lastMaintenance)
          case 'status':
            return a.status.localeCompare(b.status)
          default:
            return 0
        }
      })

      return filtered
    })

    const totalPages = computed(() => {
      return Math.ceil(filteredEquipment.value.length / itemsPerPage.value)
    })

    const startIndex = computed(() => {
      return (currentPage.value - 1) * itemsPerPage.value
    })

    const endIndex = computed(() => {
      return Math.min(startIndex.value + itemsPerPage.value, filteredEquipment.value.length)
    })

    const paginatedEquipment = computed(() => {
      return filteredEquipment.value.slice(startIndex.value, endIndex.value)
    })

    const normalCount = computed(() => {
      return filteredEquipment.value.filter(eq => eq.status === 'normal').length
    })

    const warningCount = computed(() => {
      return filteredEquipment.value.filter(eq => eq.status === 'warning').length
    })

    const criticalCount = computed(() => {
      return filteredEquipment.value.filter(eq => eq.status === 'critical').length
    })

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
        maintenance: '정비'
      }
      return texts[status] || '정상'
    }

    const getPredictionClass = (prediction) => {
      const classes = {
        normal: 'status-normal',
        warning: 'status-warning',
        critical: 'status-critical'
      }
      return classes[prediction] || 'status-normal'
    }

    const getPredictionText = (prediction) => {
      const texts = {
        normal: '안전',
        warning: '주의',
        critical: '위험'
      }
      return texts[prediction] || '안전'
    }

    const getProgressClass = (efficiency) => {
      if (efficiency >= 95) return 'progress-success'
      if (efficiency >= 85) return 'progress-warning'
      return 'progress-danger'
    }

    return {
      searchQuery,
      statusFilter,
      typeFilter,
      sortBy,
      currentPage,
      itemsPerPage,
      filteredEquipment,
      paginatedEquipment,
      totalPages,
      startIndex,
      endIndex,
      normalCount,
      warningCount,
      criticalCount,
      getStatusClass,
      getStatusText,
      getPredictionClass,
      getPredictionText,
      getProgressClass
    }
  }
}
</script>
