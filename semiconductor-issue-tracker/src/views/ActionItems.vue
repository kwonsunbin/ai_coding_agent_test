<template>
  <div class="space-y-6">
    <!-- 액션 아이템 헤더 -->
    <div class="flex justify-between items-center">
      <h2 class="text-lg font-semibold text-gray-900 dark:text-white">Action Items</h2>
      <button 
        @click="showAddModal = true"
        class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
      >
        + 새 액션 아이템
      </button>
    </div>

    <!-- 에러 메시지 -->
    <div v-if="error" class="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-md p-4">
      <div class="flex">
        <div class="flex-shrink-0">
          <ExclamationTriangleIcon class="h-5 w-5 text-red-400" />
        </div>
        <div class="ml-3">
          <h3 class="text-sm font-medium text-red-800 dark:text-red-200">오류 발생</h3>
          <div class="mt-2 text-sm text-red-700 dark:text-red-300">
            {{ error }}
          </div>
        </div>
      </div>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="loading" class="flex justify-center items-center py-8">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      <span class="ml-2 text-gray-600 dark:text-gray-400">데이터를 불러오는 중...</span>
    </div>

    <!-- 액션 아이템 테이블 -->
    <div v-if="!loading" class="bg-white dark:bg-gray-800 shadow-sm rounded-lg overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
          <thead class="bg-gray-50 dark:bg-gray-700">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                ID
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                제목
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                이슈 타입
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                상태
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                담당자
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                우선순위
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                생성일
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                마감일
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                액션
              </th>
            </tr>
          </thead>
          <tbody class="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="item in actionItems" :key="item.id" class="hover:bg-gray-50 dark:hover:bg-gray-700">
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-white">
                {{ item.id }}
              </td>
              <td class="px-6 py-4 text-sm text-gray-900 dark:text-white">
                <div class="max-w-xs truncate">{{ item.title }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getIssueTypeColor(item.issueType)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ getIssueTypeLabel(item.issueType) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getStatusColor(item.status)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ getStatusLabel(item.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900 dark:text-white">
                {{ item.assignee }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span :class="getPriorityColor(item.priority)" class="px-2 py-1 text-xs font-medium rounded-full">
                  {{ getPriorityLabel(item.priority) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                {{ formatDate(item.createdAt) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                {{ formatDate(item.dueDate) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button 
                  @click="editItem(item)"
                  class="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300 mr-3"
                >
                  편집
                </button>
                <button 
                  @click="deleteItem(item.id)"
                  class="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300"
                >
                  삭제
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 데이터가 없을 때 -->
      <div v-if="actionItems.length === 0" class="text-center py-8">
        <p class="text-gray-500 dark:text-gray-400">액션 아이템이 없습니다.</p>
      </div>
    </div>

    <!-- 추가/편집 모달 -->
    <div v-if="showAddModal || editingItem" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        <div class="p-6">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-medium text-gray-900 dark:text-white">
              {{ editingItem ? '액션 아이템 편집' : '새 액션 아이템 추가' }}
            </h3>
            <button @click="closeModal" class="text-gray-400 hover:text-gray-600">
              <XMarkIcon class="w-6 h-6" />
            </button>
          </div>

          <form @submit.prevent="saveItem" class="space-y-4">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">제목</label>
                <input 
                  v-model="formData.title" 
                  type="text" 
                  required
                  class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                >
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">이슈 타입</label>
                <select 
                  v-model="formData.issueType" 
                  required
                  class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                >
                  <option value="EQUIPMENT">설비 이슈</option>
                  <option value="PROCESS">공정 이슈</option>
                  <option value="QUALITY">품질 이슈</option>
                  <option value="MAINTENANCE">유지보수</option>
                  <option value="FDC">FDC 알람</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">상태</label>
                <select 
                  v-model="formData.status" 
                  required
                  class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                >
                  <option value="OPEN">Open</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="RESOLVED">Resolved</option>
                  <option value="CLOSED">Closed</option>
                  <option value="PENDING">Pending</option>
                </select>
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">우선순위</label>
                <select 
                  v-model="formData.priority" 
                  required
                  class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
                >
                  <option value="LOW">낮음</option>
                  <option value="MEDIUM">보통</option>
                  <option value="HIGH">높음</option>
                  <option value="CRITICAL">긴급</option>
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
                <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">마감일</label>
                <input 
                  v-model="formData.dueDate" 
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
                :disabled="saving"
                class="px-4 py-2 bg-blue-600 border border-transparent rounded-md text-sm font-medium text-white hover:bg-blue-700 disabled:opacity-50"
              >
                {{ saving ? '저장 중...' : (editingItem ? '수정' : '추가') }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { XMarkIcon, ExclamationTriangleIcon } from '@heroicons/vue/24/outline'
import { actionItemsApi, apiUtils } from '../services/api'

export default {
  name: 'ActionItems',
  components: {
    XMarkIcon,
    ExclamationTriangleIcon
  },
  setup() {
    const loading = ref(false)
    const saving = ref(false)
    const error = ref('')
    const showAddModal = ref(false)
    const editingItem = ref(null)
    const actionItems = ref([])

    const formData = ref({
      title: '',
      issueType: 'EQUIPMENT',
      status: 'OPEN',
      priority: 'MEDIUM',
      assignee: '',
      reporter: '설비 엔지니어',
      dueDate: '',
      description: ''
    })

    // 데이터 로드 함수
    const loadActionItems = async () => {
      loading.value = true
      error.value = ''
      try {
        const response = await actionItemsApi.getAll()
        actionItems.value = response.data.data || response.data || []
      } catch (err) {
        error.value = apiUtils.getErrorMessage(err)
        console.error('Failed to load action items:', err)
      } finally {
        loading.value = false
      }
    }

    const saveItem = async () => {
      saving.value = true
      error.value = ''
      try {
        const itemData = {
          ...formData.value,
          dueDate: formData.value.dueDate ? new Date(formData.value.dueDate).toISOString() : null
        }

        if (editingItem.value) {
          // 수정
          await actionItemsApi.update(editingItem.value.id, itemData)
        } else {
          // 새로 추가
          await actionItemsApi.create(itemData)
        }
        
        await loadActionItems() // 목록 새로고침
        closeModal()
      } catch (err) {
        error.value = apiUtils.getErrorMessage(err)
        console.error('Failed to save action item:', err)
      } finally {
        saving.value = false
      }
    }

    const deleteItem = async (id) => {
      if (confirm('정말 삭제하시겠습니까?')) {
        try {
          await actionItemsApi.delete(id)
          await loadActionItems() // 목록 새로고침
        } catch (err) {
          error.value = apiUtils.getErrorMessage(err)
          console.error('Failed to delete action item:', err)
        }
      }
    }

    // UI 헬퍼 함수들
    const getIssueTypeColor = (type) => {
      const colors = {
        EQUIPMENT: 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-200',
        PROCESS: 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200',
        QUALITY: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200',
        MAINTENANCE: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
        FDC: 'bg-purple-100 text-purple-800 dark:bg-purple-900 dark:text-purple-200'
      }
      return colors[type] || 'bg-gray-100 text-gray-800'
    }

    const getIssueTypeLabel = (type) => {
      const labels = {
        EQUIPMENT: '설비 이슈',
        PROCESS: '공정 이슈',
        QUALITY: '품질 이슈',
        MAINTENANCE: '유지보수',
        FDC: 'FDC 알람'
      }
      return labels[type] || type
    }

    const getStatusColor = (status) => {
      const colors = {
        OPEN: 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200',
        IN_PROGRESS: 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200',
        RESOLVED: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
        CLOSED: 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200',
        PENDING: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200'
      }
      return colors[status] || 'bg-gray-100 text-gray-800'
    }

    const getStatusLabel = (status) => {
      const labels = {
        OPEN: 'Open',
        IN_PROGRESS: 'In Progress',
        RESOLVED: 'Resolved',
        CLOSED: 'Closed',
        PENDING: 'Pending'
      }
      return labels[status] || status
    }

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

    const formatDate = (dateString) => {
      if (!dateString) return '-'
      return new Date(dateString).toLocaleDateString('ko-KR')
    }

    const editItem = (item) => {
      editingItem.value = item
      formData.value = { 
        ...item,
        dueDate: item.dueDate ? new Date(item.dueDate).toISOString().slice(0, 16) : ''
      }
    }

    const closeModal = () => {
      showAddModal.value = false
      editingItem.value = null
      formData.value = {
        title: '',
        issueType: 'EQUIPMENT',
        status: 'OPEN',
        priority: 'MEDIUM',
        assignee: '',
        reporter: '설비 엔지니어',
        dueDate: '',
        description: ''
      }
    }

    // 컴포넌트 마운트 시 데이터 로드
    onMounted(() => {
      loadActionItems()
    })

    return {
      loading,
      saving,
      error,
      showAddModal,
      editingItem,
      actionItems,
      formData,
      loadActionItems,
      saveItem,
      deleteItem,
      getIssueTypeColor,
      getIssueTypeLabel,
      getStatusColor,
      getStatusLabel,
      getPriorityColor,
      getPriorityLabel,
      formatDate,
      editItem,
      closeModal
    }
  }
}
</script>
