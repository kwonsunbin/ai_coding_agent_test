<template>
  <div class="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700">
    <!-- 헤더 -->
    <div class="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
      <div class="flex items-center justify-between">
        <h2 class="text-lg font-semibold text-gray-900 dark:text-white">인폼 작성</h2>
        <div class="flex items-center space-x-2">
          <button @click="resetForm" class="btn-secondary">
            <ArrowPathIcon class="w-4 h-4 mr-2" />
            초기화
          </button>
          <button @click="saveInform" :disabled="saving" class="btn-primary">
            <DocumentPlusIcon class="w-4 h-4 mr-2" />
            {{ saving ? '저장 중...' : '저장' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 에러 메시지 -->
    <div v-if="error" class="mx-6 mt-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-md p-4">
      <div class="flex">
        <div class="flex-shrink-0">
          <ExclamationTriangleIcon class="h-5 w-5 text-red-400" />
        </div>
        <div class="ml-3">
          <h3 class="text-sm font-medium text-red-800 dark:text-red-200">오류가 발생했습니다</h3>
          <div class="mt-2 text-sm text-red-700 dark:text-red-300">
            {{ error }}
          </div>
        </div>
      </div>
    </div>

    <!-- 인폼 작성 폼 -->
    <div class="p-6">
      <form @submit.prevent="saveInform" class="space-y-6">
        <!-- 기본 정보 -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">인폼 제목 *</label>
            <input 
              v-model="informForm.title"
              type="text" 
              required
              placeholder="인폼 제목을 입력하세요"
              class="form-input"
            >
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">작성자 *</label>
            <input 
              v-model="informForm.author"
              type="text" 
              required
              placeholder="작성자명을 입력하세요"
              class="form-input"
            >
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">우선순위 *</label>
            <select v-model="informForm.priority" required class="form-select">
              <option value="">선택하세요</option>
              <option value="CRITICAL">긴급</option>
              <option value="HIGH">높음</option>
              <option value="MEDIUM">보통</option>
              <option value="LOW">낮음</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">카테고리 *</label>
            <select v-model="informForm.category" required class="form-select">
              <option value="">선택하세요</option>
              <option value="EQUIPMENT">설비 관련</option>
              <option value="PROCESS">공정 관련</option>
              <option value="QUALITY">품질 관련</option>
              <option value="SAFETY">안전 관련</option>
              <option value="MAINTENANCE">유지보수</option>
              <option value="OTHER">기타</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">대상 부서</label>
            <select v-model="informForm.targetDepartment" class="form-select">
              <option value="">전체</option>
              <option value="FAB">FAB</option>
              <option value="ENGINEERING">엔지니어링</option>
              <option value="MAINTENANCE">유지보수</option>
              <option value="QUALITY">품질관리</option>
              <option value="SAFETY">안전관리</option>
            </select>
          </div>
        </div>

        <!-- 설비 정보 -->
        <div class="border-t border-gray-200 dark:border-gray-700 pt-6">
          <h3 class="text-md font-medium text-gray-900 dark:text-white mb-4">설비 정보</h3>
          <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">GBM</label>
              <select v-model="informForm.gbm" class="form-select">
                <option value="">선택하세요</option>
                <option value="GBM1">GBM1</option>
                <option value="GBM2">GBM2</option>
                <option value="GBM3">GBM3</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Area</label>
              <select v-model="informForm.area" class="form-select">
                <option value="">선택하세요</option>
                <option value="FAB1">FAB1</option>
                <option value="FAB2">FAB2</option>
                <option value="FAB3">FAB3</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Line</label>
              <select v-model="informForm.line" class="form-select">
                <option value="">선택하세요</option>
                <option value="LINE1">LINE1</option>
                <option value="LINE2">LINE2</option>
                <option value="LINE3">LINE3</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">설비 ID</label>
              <input 
                v-model="informForm.equipmentId"
                type="text" 
                placeholder="설비 ID"
                class="form-input"
              >
            </div>
          </div>
        </div>

        <!-- 내용 -->
        <div class="border-t border-gray-200 dark:border-gray-700 pt-6">
          <h3 class="text-md font-medium text-gray-900 dark:text-white mb-4">인폼 내용</h3>
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">상황 설명 *</label>
              <textarea 
                v-model="informForm.description"
                rows="6"
                required
                placeholder="발생한 상황에 대해 자세히 설명해주세요..."
                class="form-textarea"
              ></textarea>
            </div>
            
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">조치 사항</label>
              <textarea 
                v-model="informForm.actionTaken"
                rows="4"
                placeholder="취한 조치사항이나 계획을 입력하세요..."
                class="form-textarea"
              ></textarea>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">영향도</label>
              <textarea 
                v-model="informForm.impactAssessment"
                rows="3"
                placeholder="생산, 품질, 안전 등에 미치는 영향을 설명하세요..."
                class="form-textarea"
              ></textarea>
            </div>
          </div>
        </div>

        <!-- 첨부파일 -->
        <div class="border-t border-gray-200 dark:border-gray-700 pt-6">
          <h3 class="text-md font-medium text-gray-900 dark:text-white mb-4">첨부파일</h3>
          <div class="space-y-4">
            <div>
              <input 
                type="file" 
                multiple
                @change="handleFileUpload"
                class="form-file"
              >
              <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">
                이미지, 문서, 로그 파일 등을 첨부할 수 있습니다. (최대 10MB)
              </p>
            </div>
            
            <div v-if="informForm.attachments.length > 0" class="space-y-2">
              <div v-for="(file, index) in informForm.attachments" :key="index" 
                   class="flex items-center justify-between bg-gray-50 dark:bg-gray-700 rounded-md px-3 py-2">
                <div class="flex items-center space-x-2">
                  <PaperClipIcon class="w-4 h-4 text-gray-500" />
                  <span class="text-sm text-gray-700 dark:text-gray-300">{{ file.name }}</span>
                  <span class="text-xs text-gray-500">({{ file.size }})</span>
                </div>
                <button type="button" @click="removeFile(index)" class="text-red-500 hover:text-red-700">
                  <XMarkIcon class="w-4 h-4" />
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 알림 설정 -->
        <div class="border-t border-gray-200 dark:border-gray-700 pt-6">
          <h3 class="text-md font-medium text-gray-900 dark:text-white mb-4">알림 설정</h3>
          <div class="space-y-3">
            <label class="flex items-center">
              <input 
                type="checkbox" 
                v-model="informForm.notifyEmail"
                class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              >
              <span class="ml-2 text-sm text-gray-700 dark:text-gray-300">이메일 알림 발송</span>
            </label>
            <label class="flex items-center">
              <input 
                type="checkbox" 
                v-model="informForm.notifySms"
                class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              >
              <span class="ml-2 text-sm text-gray-700 dark:text-gray-300">SMS 알림 발송</span>
            </label>
            <label class="flex items-center">
              <input 
                type="checkbox" 
                v-model="informForm.urgent"
                class="rounded border-gray-300 text-red-600 focus:ring-red-500"
              >
              <span class="ml-2 text-sm text-gray-700 dark:text-gray-300">긴급 알림 (즉시 발송)</span>
            </label>
          </div>
        </div>
      </form>
    </div>

    <!-- 성공 메시지 -->
    <div v-if="showSuccessMessage" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
      <div class="bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-md w-full p-6">
        <div class="flex items-center space-x-3 mb-4">
          <CheckCircleIcon class="w-8 h-8 text-green-500" />
          <h3 class="text-lg font-medium text-gray-900 dark:text-white">인폼 저장 완료</h3>
        </div>
        <p class="text-gray-600 dark:text-gray-400 mb-4">
          인폼이 성공적으로 저장되었습니다. 관련 부서에 알림이 발송됩니다.
        </p>
        <div class="flex justify-end space-x-3">
          <button @click="closeSuccessMessage" class="btn-secondary">확인</button>
          <button @click="writeNewInform" class="btn-primary">새 인폼 작성</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import {
  DocumentPlusIcon,
  ArrowPathIcon,
  PaperClipIcon,
  XMarkIcon,
  CheckCircleIcon,
  ExclamationTriangleIcon
} from '@heroicons/vue/24/outline'
import { informsApi, apiUtils } from '../services/api.js'

export default {
  name: 'InformWrite',
  components: {
    DocumentPlusIcon,
    ArrowPathIcon,
    PaperClipIcon,
    XMarkIcon,
    CheckCircleIcon,
    ExclamationTriangleIcon
  },
  setup() {
    const saving = ref(false)
    const error = ref(null)
    const showSuccessMessage = ref(false)

    const informForm = ref({
      title: '',
      author: '설비 엔지니어',
      priority: '',
      category: '',
      targetDepartment: '',
      gbm: '',
      area: '',
      line: '',
      equipmentId: '',
      description: '',
      actionTaken: '',
      impactAssessment: '',
      attachments: [],
      notifyEmail: true,
      notifySms: false,
      urgent: false
    })

    const handleFileUpload = (event) => {
      const files = Array.from(event.target.files)
      const newFiles = files.map(file => ({
        name: file.name,
        size: (file.size / 1024 / 1024).toFixed(1) + 'MB'
      }))
      informForm.value.attachments.push(...newFiles)
    }

    const removeFile = (index) => {
      informForm.value.attachments.splice(index, 1)
    }

    const resetForm = () => {
      if (confirm('작성 중인 내용이 모두 삭제됩니다. 계속하시겠습니까?')) {
        informForm.value = {
          title: '',
          author: '설비 엔지니어',
          priority: '',
          category: '',
          targetDepartment: '',
          gbm: '',
          area: '',
          line: '',
          equipmentId: '',
          description: '',
          actionTaken: '',
          impactAssessment: '',
          attachments: [],
          notifyEmail: true,
          notifySms: false,
          urgent: false
        }
        error.value = null
      }
    }

    const saveInform = async () => {
      // 폼 유효성 검사
      if (!informForm.value.title || !informForm.value.author || 
          !informForm.value.priority || !informForm.value.category || 
          !informForm.value.description) {
        error.value = '필수 항목을 모두 입력해주세요.'
        return
      }

      saving.value = true
      error.value = null

      try {
        // API 호출
        await informsApi.create(informForm.value)
        
        // 성공 메시지 표시
        showSuccessMessage.value = true
        console.log('인폼 저장 성공')
      } catch (err) {
        error.value = apiUtils.getErrorMessage(err)
        console.error('인폼 저장 실패:', err)
      } finally {
        saving.value = false
      }
    }

    const closeSuccessMessage = () => {
      showSuccessMessage.value = false
    }

    const writeNewInform = () => {
      showSuccessMessage.value = false
      resetForm()
    }

    return {
      saving,
      error,
      informForm,
      showSuccessMessage,
      handleFileUpload,
      removeFile,
      resetForm,
      saveInform,
      closeSuccessMessage,
      writeNewInform
    }
  }
}
</script>

<style scoped>
.btn-primary {
  @apply inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50;
}

.btn-secondary {
  @apply inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 dark:bg-gray-700 dark:text-gray-300 dark:border-gray-600 dark:hover:bg-gray-600;
}

.form-input {
  @apply block w-full border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm dark:bg-gray-700 dark:border-gray-600 dark:text-white;
}

.form-select {
  @apply block w-full border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm dark:bg-gray-700 dark:border-gray-600 dark:text-white;
}

.form-textarea {
  @apply block w-full border-gray-300 rounded-md shadow-sm focus:border-blue-500 focus:ring-blue-500 sm:text-sm dark:bg-gray-700 dark:border-gray-600 dark:text-white;
}

.form-file {
  @apply block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-medium file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100 dark:file:bg-gray-700 dark:file:text-gray-300;
}
</style>
