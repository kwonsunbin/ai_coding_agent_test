<template>
  <div class="space-y-6">
    <!-- 페이지 헤더 -->
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-soft p-6 border border-blue-100/50 dark:border-gray-700/50">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-secondary-900 dark:text-white">설비 기준정보</h1>
          <p class="text-secondary-600 dark:text-gray-300 mt-1">반도체 설비별 기준 사양 및 운영 파라미터 정보</p>
        </div>
        <div class="flex items-center space-x-3">
          <button 
            @click="refreshData"
            class="btn-secondary"
            :disabled="isLoading"
          >
            <ArrowPathIcon class="w-4 h-4 mr-2" :class="{ 'animate-spin': isLoading }" />
            새로고침
          </button>
          <button 
            @click="exportSpecs"
            class="btn-primary"
          >
            <DocumentArrowDownIcon class="w-4 h-4 mr-2" />
            내보내기
          </button>
        </div>
      </div>
    </div>

    <!-- 필터 및 검색 -->
    <div class="bg-white dark:bg-gray-800 rounded-xl shadow-soft p-6 border border-blue-100/50 dark:border-gray-700/50">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
        <div>
          <label class="block text-sm font-medium text-secondary-700 dark:text-gray-300 mb-2">설비 유형</label>
          <select v-model="selectedType" class="form-select">
            <option value="">전체</option>
            <option value="CVD">CVD</option>
            <option value="Etch">Etch</option>
            <option value="Litho">Litho</option>
            <option value="Implant">Implant</option>
            <option value="CMP">CMP</option>
            <option value="Metrology">Metrology</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-secondary-700 dark:text-gray-300 mb-2">제조사</label>
          <select v-model="selectedVendor" class="form-select">
            <option value="">전체</option>
            <option value="AMAT">AMAT</option>
            <option value="LAM">LAM Research</option>
            <option value="TEL">Tokyo Electron</option>
            <option value="ASML">ASML</option>
            <option value="KLA">KLA</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-secondary-700 dark:text-gray-300 mb-2">상태</label>
          <select v-model="selectedStatus" class="form-select">
            <option value="">전체</option>
            <option value="Active">운영중</option>
            <option value="Maintenance">점검중</option>
            <option value="Standby">대기중</option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-secondary-700 dark:text-gray-300 mb-2">검색</label>
          <input 
            v-model="searchQuery" 
            type="text" 
            placeholder="설비명 또는 모델명 검색"
            class="form-input"
          />
        </div>
      </div>
    </div>

    <!-- 설비 기준정보 목록 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
      <div 
        v-for="equipment in filteredEquipments" 
        :key="equipment.id"
        class="bg-white dark:bg-gray-800 rounded-xl shadow-soft border border-blue-100/50 dark:border-gray-700/50 hover:shadow-lg transition-all duration-300 cursor-pointer"
        @click="selectEquipment(equipment)"
      >
        <div class="p-6">
          <!-- 설비 헤더 -->
          <div class="flex items-start justify-between mb-4">
            <div class="flex items-center space-x-3">
              <div class="w-12 h-12 rounded-lg flex items-center justify-center"
                   :class="getEquipmentTypeColor(equipment.type)">
                <component :is="getEquipmentIcon(equipment.type)" class="w-6 h-6 text-white" />
              </div>
              <div>
                <h3 class="font-semibold text-secondary-900 dark:text-white">{{ equipment.name }}</h3>
                <p class="text-sm text-secondary-600 dark:text-gray-300">{{ equipment.model }}</p>
              </div>
            </div>
            <span 
              class="px-2 py-1 text-xs font-medium rounded-full"
              :class="getStatusColor(equipment.status)"
            >
              {{ getStatusText(equipment.status) }}
            </span>
          </div>

          <!-- 기본 정보 -->
          <div class="space-y-3">
            <div class="flex justify-between items-center">
              <span class="text-sm text-secondary-600 dark:text-gray-400">설비 유형</span>
              <span class="text-sm font-medium text-secondary-900 dark:text-white">{{ equipment.type }}</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-sm text-secondary-600 dark:text-gray-400">제조사</span>
              <span class="text-sm font-medium text-secondary-900 dark:text-white">{{ equipment.vendor }}</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-sm text-secondary-600 dark:text-gray-400">설치일</span>
              <span class="text-sm font-medium text-secondary-900 dark:text-white">{{ equipment.installDate }}</span>
            </div>
            <div class="flex justify-between items-center">
              <span class="text-sm text-secondary-600 dark:text-gray-400">웨이퍼 크기</span>
              <span class="text-sm font-medium text-secondary-900 dark:text-white">{{ equipment.waferSize }}</span>
            </div>
          </div>

          <!-- 주요 사양 미리보기 -->
          <div class="mt-4 pt-4 border-t border-secondary-200 dark:border-gray-600">
            <div class="flex items-center justify-between">
              <span class="text-sm text-secondary-600 dark:text-gray-400">주요 사양</span>
              <span class="text-xs text-blue-600 dark:text-blue-400">{{ equipment.specs.length }}개 항목</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 설비 상세 정보 모달 -->
    <div v-if="selectedEquipment" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white dark:bg-gray-800 rounded-xl shadow-xl max-w-4xl w-full max-h-[90vh] overflow-hidden">
        <!-- 모달 헤더 -->
        <div class="flex items-center justify-between p-6 border-b border-secondary-200 dark:border-gray-600">
          <div class="flex items-center space-x-3">
            <div class="w-12 h-12 rounded-lg flex items-center justify-center"
                 :class="getEquipmentTypeColor(selectedEquipment.type)">
              <component :is="getEquipmentIcon(selectedEquipment.type)" class="w-6 h-6 text-white" />
            </div>
            <div>
              <h2 class="text-xl font-bold text-secondary-900 dark:text-white">{{ selectedEquipment.name }}</h2>
              <p class="text-secondary-600 dark:text-gray-300">{{ selectedEquipment.model }}</p>
            </div>
          </div>
          <button @click="selectedEquipment = null" class="text-secondary-400 hover:text-secondary-600">
            <XMarkIcon class="w-6 h-6" />
          </button>
        </div>

        <!-- 모달 내용 -->
        <div class="p-6 overflow-y-auto max-h-[calc(90vh-120px)]">
          <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <!-- 기본 정보 -->
            <div class="space-y-4">
              <h3 class="text-lg font-semibold text-secondary-900 dark:text-white mb-4">기본 정보</h3>
              <div class="bg-secondary-50 dark:bg-gray-700 rounded-lg p-4 space-y-3">
                <div class="flex justify-between">
                  <span class="text-secondary-600 dark:text-gray-400">설비 ID</span>
                  <span class="font-medium text-secondary-900 dark:text-white">{{ selectedEquipment.id }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-secondary-600 dark:text-gray-400">설비 유형</span>
                  <span class="font-medium text-secondary-900 dark:text-white">{{ selectedEquipment.type }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-secondary-600 dark:text-gray-400">제조사</span>
                  <span class="font-medium text-secondary-900 dark:text-white">{{ selectedEquipment.vendor }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-secondary-600 dark:text-gray-400">모델명</span>
                  <span class="font-medium text-secondary-900 dark:text-white">{{ selectedEquipment.model }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-secondary-600 dark:text-gray-400">설치일</span>
                  <span class="font-medium text-secondary-900 dark:text-white">{{ selectedEquipment.installDate }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-secondary-600 dark:text-gray-400">웨이퍼 크기</span>
                  <span class="font-medium text-secondary-900 dark:text-white">{{ selectedEquipment.waferSize }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-secondary-600 dark:text-gray-400">상태</span>
                  <span 
                    class="px-2 py-1 text-xs font-medium rounded-full"
                    :class="getStatusColor(selectedEquipment.status)"
                  >
                    {{ getStatusText(selectedEquipment.status) }}
                  </span>
                </div>
              </div>
            </div>

            <!-- 운영 파라미터 -->
            <div class="space-y-4">
              <h3 class="text-lg font-semibold text-secondary-900 dark:text-white mb-4">운영 파라미터</h3>
              <div class="bg-secondary-50 dark:bg-gray-700 rounded-lg p-4 space-y-3">
                <div v-for="param in selectedEquipment.operatingParams" :key="param.name" class="flex justify-between">
                  <span class="text-secondary-600 dark:text-gray-400">{{ param.name }}</span>
                  <span class="font-medium text-secondary-900 dark:text-white">{{ param.value }} {{ param.unit }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 상세 사양 -->
          <div class="mt-6">
            <h3 class="text-lg font-semibold text-secondary-900 dark:text-white mb-4">상세 사양</h3>
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-secondary-200 dark:divide-gray-600">
                <thead class="bg-secondary-50 dark:bg-gray-700">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-secondary-500 dark:text-gray-300 uppercase tracking-wider">
                      항목
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-secondary-500 dark:text-gray-300 uppercase tracking-wider">
                      기준값
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-secondary-500 dark:text-gray-300 uppercase tracking-wider">
                      허용범위
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-secondary-500 dark:text-gray-300 uppercase tracking-wider">
                      단위
                    </th>
                  </tr>
                </thead>
                <tbody class="bg-white dark:bg-gray-800 divide-y divide-secondary-200 dark:divide-gray-600">
                  <tr v-for="spec in selectedEquipment.specs" :key="spec.parameter">
                    <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-secondary-900 dark:text-white">
                      {{ spec.parameter }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-secondary-600 dark:text-gray-300">
                      {{ spec.standardValue }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-secondary-600 dark:text-gray-300">
                      {{ spec.tolerance }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-secondary-600 dark:text-gray-300">
                      {{ spec.unit }}
                    </td>
                  </tr>
                </tbody>
              </table>
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
  ArrowPathIcon, 
  DocumentArrowDownIcon,
  XMarkIcon,
  CogIcon,
  BeakerIcon,
  CpuChipIcon,
  ComputerDesktopIcon,
  WrenchScrewdriverIcon,
  MagnifyingGlassIcon
} from '@heroicons/vue/24/outline'

export default {
  name: 'EquipmentSpecs',
  components: {
    ArrowPathIcon,
    DocumentArrowDownIcon,
    XMarkIcon,
    CogIcon,
    BeakerIcon,
    CpuChipIcon,
    ComputerDesktopIcon,
    WrenchScrewdriverIcon,
    MagnifyingGlassIcon
  },
  setup() {
    const isLoading = ref(false)
    const selectedType = ref('')
    const selectedVendor = ref('')
    const selectedStatus = ref('')
    const searchQuery = ref('')
    const selectedEquipment = ref(null)

    // 샘플 데이터
    const equipments = ref([
      {
        id: 'CVD-001',
        name: 'CVD Chamber A1',
        model: 'Centura 5200',
        type: 'CVD',
        vendor: 'AMAT',
        status: 'Active',
        installDate: '2023-01-15',
        waferSize: '300mm',
        operatingParams: [
          { name: '온도', value: '400', unit: '°C' },
          { name: '압력', value: '5', unit: 'Torr' },
          { name: '가스 유량', value: '200', unit: 'sccm' },
          { name: '전력', value: '2000', unit: 'W' }
        ],
        specs: [
          { parameter: '챔버 온도', standardValue: '400', tolerance: '±5', unit: '°C' },
          { parameter: '공정 압력', standardValue: '5.0', tolerance: '±0.2', unit: 'Torr' },
          { parameter: 'SiH4 유량', standardValue: '200', tolerance: '±10', unit: 'sccm' },
          { parameter: 'N2 유량', standardValue: '1000', tolerance: '±50', unit: 'sccm' },
          { parameter: 'RF 전력', standardValue: '2000', tolerance: '±100', unit: 'W' },
          { parameter: '증착 속도', standardValue: '50', tolerance: '±5', unit: 'Å/min' }
        ]
      },
      {
        id: 'ETCH-001',
        name: 'Etch Chamber B1',
        model: 'Flex 45',
        type: 'Etch',
        vendor: 'LAM',
        status: 'Active',
        installDate: '2023-02-20',
        waferSize: '300mm',
        operatingParams: [
          { name: '온도', value: '60', unit: '°C' },
          { name: '압력', value: '10', unit: 'mTorr' },
          { name: 'RF 전력', value: '1500', unit: 'W' },
          { name: '에칭 속도', value: '100', unit: 'Å/min' }
        ],
        specs: [
          { parameter: '챔버 온도', standardValue: '60', tolerance: '±3', unit: '°C' },
          { parameter: '공정 압력', standardValue: '10', tolerance: '±1', unit: 'mTorr' },
          { parameter: 'CF4 유량', standardValue: '50', tolerance: '±5', unit: 'sccm' },
          { parameter: 'O2 유량', standardValue: '10', tolerance: '±2', unit: 'sccm' },
          { parameter: 'RF 전력', standardValue: '1500', tolerance: '±75', unit: 'W' },
          { parameter: '에칭 속도', standardValue: '100', tolerance: '±10', unit: 'Å/min' }
        ]
      },
      {
        id: 'LITHO-001',
        name: 'Scanner C1',
        model: 'NXT:1980Di',
        type: 'Litho',
        vendor: 'ASML',
        status: 'Maintenance',
        installDate: '2022-11-10',
        waferSize: '300mm',
        operatingParams: [
          { name: '노광 에너지', value: '25', unit: 'mJ/cm²' },
          { name: '초점 정확도', value: '±50', unit: 'nm' },
          { name: '오버레이', value: '±2', unit: 'nm' },
          { name: '처리량', value: '275', unit: 'wph' }
        ],
        specs: [
          { parameter: '파장', standardValue: '193', tolerance: '±0.1', unit: 'nm' },
          { parameter: '해상도', standardValue: '38', tolerance: '±2', unit: 'nm' },
          { parameter: '노광 에너지', standardValue: '25', tolerance: '±2', unit: 'mJ/cm²' },
          { parameter: '초점 정확도', standardValue: '50', tolerance: '±10', unit: 'nm' },
          { parameter: '오버레이 정확도', standardValue: '2.0', tolerance: '±0.5', unit: 'nm' },
          { parameter: '처리량', standardValue: '275', tolerance: '±25', unit: 'wph' }
        ]
      },
      {
        id: 'IMP-001',
        name: 'Implanter D1',
        model: 'VIISta 3000XR',
        type: 'Implant',
        vendor: 'AMAT',
        status: 'Active',
        installDate: '2023-03-05',
        waferSize: '300mm',
        operatingParams: [
          { name: '이온 에너지', value: '80', unit: 'keV' },
          { name: '도즈량', value: '1E15', unit: 'atoms/cm²' },
          { name: '빔 전류', value: '10', unit: 'mA' },
          { name: '웨이퍼 온도', value: '500', unit: '°C' }
        ],
        specs: [
          { parameter: '이온 에너지', standardValue: '80', tolerance: '±2', unit: 'keV' },
          { parameter: '도즈량', standardValue: '1E15', tolerance: '±5%', unit: 'atoms/cm²' },
          { parameter: '빔 전류', standardValue: '10', tolerance: '±0.5', unit: 'mA' },
          { parameter: '웨이퍼 온도', standardValue: '500', tolerance: '±10', unit: '°C' },
          { parameter: '균일도', standardValue: '±2', tolerance: '±0.5', unit: '%' },
          { parameter: '처리량', standardValue: '200', tolerance: '±20', unit: 'wph' }
        ]
      },
      {
        id: 'CMP-001',
        name: 'CMP Tool E1',
        model: 'Reflexion LK',
        type: 'CMP',
        vendor: 'AMAT',
        status: 'Standby',
        installDate: '2023-04-12',
        waferSize: '300mm',
        operatingParams: [
          { name: '연마 압력', value: '3', unit: 'psi' },
          { name: '회전 속도', value: '100', unit: 'rpm' },
          { name: '슬러리 유량', value: '200', unit: 'ml/min' },
          { name: '연마 시간', value: '60', unit: 'sec' }
        ],
        specs: [
          { parameter: '연마 압력', standardValue: '3.0', tolerance: '±0.2', unit: 'psi' },
          { parameter: '헤드 회전 속도', standardValue: '100', tolerance: '±5', unit: 'rpm' },
          { parameter: '플래튼 회전 속도', standardValue: '80', tolerance: '±5', unit: 'rpm' },
          { parameter: '슬러리 유량', standardValue: '200', tolerance: '±20', unit: 'ml/min' },
          { parameter: '연마 시간', standardValue: '60', tolerance: '±5', unit: 'sec' },
          { parameter: '제거율', standardValue: '3000', tolerance: '±300', unit: 'Å/min' }
        ]
      },
      {
        id: 'MET-001',
        name: 'Metrology F1',
        model: 'SpectraShape 11k',
        type: 'Metrology',
        vendor: 'KLA',
        status: 'Active',
        installDate: '2023-05-18',
        waferSize: '300mm',
        operatingParams: [
          { name: '측정 정확도', value: '±0.1', unit: 'nm' },
          { name: '반복성', value: '0.05', unit: 'nm' },
          { name: '측정 시간', value: '30', unit: 'sec' },
          { name: '처리량', value: '120', unit: 'wph' }
        ],
        specs: [
          { parameter: '측정 정확도', standardValue: '0.1', tolerance: '±0.05', unit: 'nm' },
          { parameter: '반복성', standardValue: '0.05', tolerance: '±0.02', unit: 'nm' },
          { parameter: '측정 범위', standardValue: '1-1000', tolerance: 'N/A', unit: 'nm' },
          { parameter: '측정 시간', standardValue: '30', tolerance: '±5', unit: 'sec' },
          { parameter: '처리량', standardValue: '120', tolerance: '±10', unit: 'wph' },
          { parameter: '재현성', standardValue: '0.02', tolerance: '±0.01', unit: 'nm' }
        ]
      }
    ])

    const filteredEquipments = computed(() => {
      return equipments.value.filter(equipment => {
        const matchesType = !selectedType.value || equipment.type === selectedType.value
        const matchesVendor = !selectedVendor.value || equipment.vendor === selectedVendor.value
        const matchesStatus = !selectedStatus.value || equipment.status === selectedStatus.value
        const matchesSearch = !searchQuery.value || 
          equipment.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          equipment.model.toLowerCase().includes(searchQuery.value.toLowerCase())
        
        return matchesType && matchesVendor && matchesStatus && matchesSearch
      })
    })

    const getEquipmentTypeColor = (type) => {
      const colors = {
        'CVD': 'bg-blue-500',
        'Etch': 'bg-red-500',
        'Litho': 'bg-purple-500',
        'Implant': 'bg-green-500',
        'CMP': 'bg-yellow-500',
        'Metrology': 'bg-indigo-500'
      }
      return colors[type] || 'bg-gray-500'
    }

    const getEquipmentIcon = (type) => {
      const icons = {
        'CVD': BeakerIcon,
        'Etch': CpuChipIcon,
        'Litho': ComputerDesktopIcon,
        'Implant': WrenchScrewdriverIcon,
        'CMP': CogIcon,
        'Metrology': MagnifyingGlassIcon
      }
      return icons[type] || CogIcon
    }

    const getStatusColor = (status) => {
      const colors = {
        'Active': 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
        'Maintenance': 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200',
        'Standby': 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
      }
      return colors[status] || 'bg-gray-100 text-gray-800'
    }

    const getStatusText = (status) => {
      const texts = {
        'Active': '운영중',
        'Maintenance': '점검중',
        'Standby': '대기중'
      }
      return texts[status] || status
    }

    const selectEquipment = (equipment) => {
      selectedEquipment.value = equipment
    }

    const refreshData = async () => {
      isLoading.value = true
      // 실제 구현에서는 API 호출
      await new Promise(resolve => setTimeout(resolve, 1000))
      isLoading.value = false
    }

    const exportSpecs = () => {
      // 실제 구현에서는 Excel 또는 PDF 내보내기
      alert('기준정보를 내보내는 기능입니다.')
    }

    onMounted(() => {
      // 컴포넌트 마운트 시 초기 데이터 로드
    })

    return {
      isLoading,
      selectedType,
      selectedVendor,
      selectedStatus,
      searchQuery,
      selectedEquipment,
      equipments,
      filteredEquipments,
      getEquipmentTypeColor,
      getEquipmentIcon,
      getStatusColor,
      getStatusText,
      selectEquipment,
      refreshData,
      exportSpecs
    }
  }
}
</script>
