<template>
  <div class="space-y-6">
    <!-- 코멘트 헤더 -->
    <div class="flex justify-between items-center">
      <h2 class="text-lg font-semibold text-gray-900 dark:text-white">Action Item Comments</h2>
      <div class="flex space-x-3">
        <select 
          v-model="selectedActionItem" 
          class="border border-gray-300 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
        >
          <option value="">액션 아이템 선택</option>
          <option v-for="item in actionItems" :key="item.id" :value="item.id">
            {{ item.id }} - {{ item.title }}
          </option>
        </select>
        <button 
          @click="openAddModal"
          :disabled="!selectedActionItem"
          :title="!selectedActionItem ? '액션 아이템을 먼저 선택해주세요' : ''"
          class="bg-blue-600 hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed text-white px-4 py-2 rounded-md text-sm font-medium transition-colors"
        >
          + 코멘트 추가
        </button>
      </div>
    </div>

    <!-- 선택된 액션 아이템 정보 -->
    <div v-if="selectedActionItemData" class="bg-blue-50 dark:bg-blue-900/20 border border-blue-200 dark:border-blue-800 rounded-lg p-4">
      <div class="flex items-center justify-between">
        <div>
          <h3 class="font-medium text-blue-900 dark:text-blue-100">{{ selectedActionItemData.title }}</h3>
          <p class="text-sm text-blue-700 dark:text-blue-300 mt-1">{{ selectedActionItemData.description }}</p>
        </div>
        <div class="text-right">
          <span :class="getStatusColor(selectedActionItemData.status)" class="px-2 py-1 text-xs font-medium rounded-full">
            {{ getStatusLabel(selectedActionItemData.status) }}
          </span>
          <p class="text-sm text-blue-600 dark:text-blue-400 mt-1">담당자: {{ selectedActionItemData.assignee }}</p>
        </div>
      </div>
    </div>

    <!-- 코멘트 목록 -->
    <div class="space-y-4">
      <div v-if="filteredComments.length === 0" class="text-center py-8 text-gray-500 dark:text-gray-400">
        {{ selectedActionItem ? '코멘트가 없습니다.' : '액션 아이템을 선택해주세요.' }}
      </div>
      
      <div v-for="comment in filteredComments" :key="comment.id" class="bg-white dark:bg-gray-800 shadow-sm rounded-lg border border-gray-200 dark:border-gray-700">
        <div class="p-6">
          <div class="flex items-start justify-between">
            <div class="flex items-center space-x-3">
              <div class="w-10 h-10 bg-gradient-to-r from-blue-500 to-blue-600 rounded-full flex items-center justify-center">
                <UserIcon class="w-5 h-5 text-white" />
              </div>
              <div>
                <h4 class="font-medium text-gray-900 dark:text-white">{{ comment.author }}</h4>
                <p class="text-sm text-gray-500 dark:text-gray-400">{{ formatDateTime(comment.createdAt) }}</p>
              </div>
            </div>
            <div class="flex space-x-2">
              <button 
                @click="editComment(comment)"
                class="text-blue-600 hover:text-blue-900 dark:text-blue-400 dark:hover:text-blue-300 text-sm"
              >
                편집
              </button>
              <button 
                @click="deleteComment(comment.id)"
                class="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300 text-sm"
              >
                삭제
              </button>
            </div>
          </div>
          
          <div class="mt-4">
            <div class="prose dark:prose-invert max-w-none">
              <div v-html="formatContent(comment.content)"></div>
            </div>
          </div>

          <!-- 첨부파일 -->
          <div v-if="comment.attachments && comment.attachments.length > 0" class="mt-4">
            <h5 class="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">첨부파일</h5>
            <div class="flex flex-wrap gap-2">
              <div v-for="file in comment.attachments" :key="file.name" 
                   class="flex items-center space-x-2 bg-gray-100 dark:bg-gray-700 rounded-md px-3 py-2">
                <PaperClipIcon class="w-4 h-4 text-gray-500" />
                <span class="text-sm text-gray-700 dark:text-gray-300">{{ file.name }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 코멘트 추가/편집 모달 -->
    <div v-if="showAddModal || editingComment" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto">
        <div class="p-6">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-medium text-gray-900 dark:text-white">
              {{ editingComment ? '코멘트 편집' : '새 코멘트 추가' }}
            </h3>
            <button @click="closeModal" class="text-gray-400 hover:text-gray-600">
              <XMarkIcon class="w-6 h-6" />
            </button>
          </div>

          <form @submit.prevent="saveComment" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">작성자</label>
              <input 
                v-model="commentForm.author" 
                type="text" 
                required
                class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
              >
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">내용</label>
              <textarea 
                v-model="commentForm.content" 
                rows="8"
                required
                placeholder="코멘트 내용을 입력하세요..."
                class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
              ></textarea>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">첨부파일</label>
              <input 
                type="file" 
                multiple
                @change="handleFileUpload"
                class="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white"
              >
              <div v-if="commentForm.attachments.length > 0" class="mt-2">
                <div v-for="(file, index) in commentForm.attachments" :key="index" 
                     class="flex items-center justify-between bg-gray-100 dark:bg-gray-700 rounded-md px-3 py-2 mt-1">
                  <span class="text-sm text-gray-700 dark:text-gray-300">{{ file.name }}</span>
                  <button type="button" @click="removeFile(index)" class="text-red-500 hover:text-red-700">
                    <XMarkIcon class="w-4 h-4" />
                  </button>
                </div>
              </div>
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
                {{ editingComment ? '수정' : '추가' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { XMarkIcon, UserIcon, PaperClipIcon } from '@heroicons/vue/24/outline'

export default {
  name: 'Comments',
  components: {
    XMarkIcon,
    UserIcon,
    PaperClipIcon
  },
  setup() {
    const showAddModal = ref(false)
    const editingComment = ref(null)
    const selectedActionItem = ref('')
    const comments = ref([])
    const actionItems = ref([])

    const commentForm = ref({
      author: '',
      content: '',
      attachments: []
    })

    // 샘플 액션 아이템 데이터
    const sampleActionItems = [
      {
        id: 'AI-001',
        title: 'CVD 챔버 온도 이상 현상 조치',
        description: 'CVD 챔버의 온도가 설정값보다 높게 측정되는 현상 발생',
        status: 'inProgress',
        assignee: '김설비'
      },
      {
        id: 'AI-002',
        title: 'Etch 공정 균일도 개선',
        description: 'Etch 공정에서 웨이퍼 전체 균일도가 기준치를 벗어남',
        status: 'open',
        assignee: '이공정'
      },
      {
        id: 'AI-003',
        title: 'FDC 알람 임계값 조정',
        description: 'FDC 시스템의 알람 임계값이 너무 민감하게 설정되어 있음',
        status: 'resolved',
        assignee: '박모니터'
      }
    ]

    // 샘플 코멘트 데이터
    const sampleComments = [
      {
        id: 'C-001',
        actionItemId: 'AI-001',
        author: '김설비',
        content: '온도 센서를 점검한 결과 센서 자체에는 이상이 없었습니다.\n\n히터 컨트롤러 설정을 확인해보겠습니다.',
        createdAt: '2024-01-15T10:30:00',
        attachments: [
          { name: 'temperature_log.xlsx', size: '2.3MB' },
          { name: 'sensor_check.pdf', size: '1.1MB' }
        ]
      },
      {
        id: 'C-002',
        actionItemId: 'AI-001',
        author: '이엔지니어',
        content: '히터 컨트롤러 펌웨어 버전을 확인해보시기 바랍니다.\n\n최근 업데이트 후 유사한 이슈가 다른 챔버에서도 발생했었습니다.',
        createdAt: '2024-01-15T14:20:00',
        attachments: []
      },
      {
        id: 'C-003',
        actionItemId: 'AI-002',
        author: '이공정',
        content: '웨이퍼 매핑 데이터를 분석한 결과 가장자리 영역에서 에칭 속도가 느린 것을 확인했습니다.\n\n가스 플로우 패턴을 조정해보겠습니다.',
        createdAt: '2024-01-16T09:15:00',
        attachments: [
          { name: 'wafer_map.png', size: '856KB' }
        ]
      }
    ]

    const selectedActionItemData = computed(() => {
      return actionItems.value.find(item => item.id === selectedActionItem.value)
    })

    const filteredComments = computed(() => {
      if (!selectedActionItem.value) return []
      return comments.value.filter(comment => comment.actionItemId === selectedActionItem.value)
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    })

    const getStatusColor = (status) => {
      const colors = {
        open: 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200',
        inProgress: 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200',
        resolved: 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200',
        closed: 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200',
        pending: 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200'
      }
      return colors[status] || 'bg-gray-100 text-gray-800'
    }

    const getStatusLabel = (status) => {
      const labels = {
        open: 'Open',
        inProgress: 'In Progress',
        resolved: 'Resolved',
        closed: 'Closed',
        pending: 'Pending'
      }
      return labels[status] || status
    }

    const formatDateTime = (dateString) => {
      return new Date(dateString).toLocaleString('ko-KR')
    }

    const formatContent = (content) => {
      return content.replace(/\n/g, '<br>')
    }

    const handleFileUpload = (event) => {
      const files = Array.from(event.target.files)
      commentForm.value.attachments = files.map(file => ({
        name: file.name,
        size: (file.size / 1024 / 1024).toFixed(1) + 'MB'
      }))
    }

    const removeFile = (index) => {
      commentForm.value.attachments.splice(index, 1)
    }

    const editComment = (comment) => {
      editingComment.value = comment
      commentForm.value = {
        author: comment.author,
        content: comment.content,
        attachments: [...comment.attachments]
      }
    }

    const deleteComment = (id) => {
      if (confirm('정말 삭제하시겠습니까?')) {
        comments.value = comments.value.filter(comment => comment.id !== id)
      }
    }

    const openAddModal = () => {
      showAddModal.value = true
      commentForm.value = {
        author: '설비 엔지니어',
        content: '',
        attachments: []
      }
    }

    const closeModal = () => {
      showAddModal.value = false
      editingComment.value = null
      commentForm.value = {
        author: '',
        content: '',
        attachments: []
      }
    }

    const saveComment = () => {
      if (editingComment.value) {
        // 편집
        const index = comments.value.findIndex(comment => comment.id === editingComment.value.id)
        if (index !== -1) {
          comments.value[index] = {
            ...editingComment.value,
            ...commentForm.value,
            updatedAt: new Date().toISOString()
          }
        }
      } else {
        // 새로 추가
        const newComment = {
          id: `C-${String(comments.value.length + 1).padStart(3, '0')}`,
          actionItemId: selectedActionItem.value,
          ...commentForm.value,
          createdAt: new Date().toISOString()
        }
        comments.value.push(newComment)
      }
      closeModal()
    }

    onMounted(() => {
      actionItems.value = [...sampleActionItems]
      comments.value = [...sampleComments]
    })

    return {
      showAddModal,
      editingComment,
      selectedActionItem,
      selectedActionItemData,
      comments,
      actionItems,
      commentForm,
      filteredComments,
      getStatusColor,
      getStatusLabel,
      formatDateTime,
      formatContent,
      handleFileUpload,
      removeFile,
      editComment,
      deleteComment,
      openAddModal,
      closeModal,
      saveComment
    }
  }
}
</script>
