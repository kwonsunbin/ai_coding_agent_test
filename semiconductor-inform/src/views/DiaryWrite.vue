<template>
  <div class="diary-write">
    <div class="page-header">
      <h2>{{ isEdit ? '일기 수정' : '새 일기 작성' }}</h2>
      <router-link to="/diary" class="btn btn-secondary">
        목록으로 돌아가기
      </router-link>
    </div>

    <form @submit.prevent="saveDiary" class="diary-form">
      <div class="card">
        <div class="form-group">
          <label for="title" class="form-label">제목 *</label>
          <input
            type="text"
            id="title"
            v-model="form.title"
            class="form-input"
            placeholder="일기 제목을 입력하세요"
            required
          />
        </div>

        <div class="form-group">
          <label for="category" class="form-label">카테고리</label>
          <select id="category" v-model="form.category" class="form-select">
            <option value="">카테고리 선택</option>
            <option value="공정">공정</option>
            <option value="장비">장비</option>
            <option value="품질관리">품질관리</option>
            <option value="연구개발">연구개발</option>
            <option value="교육">교육</option>
            <option value="기타">기타</option>
          </select>
        </div>

        <div class="form-group">
          <label for="content" class="form-label">내용 *</label>
          <textarea
            id="content"
            v-model="form.content"
            class="form-textarea"
            placeholder="오늘의 경험, 배운 점, 기술적 내용을 자세히 기록해보세요..."
            required
            rows="10"
          ></textarea>
        </div>

        <div class="form-group">
          <label class="form-label">태그</label>
          <div class="tag-input-container">
            <input
              type="text"
              v-model="newTag"
              @keyup.enter="addTag"
              class="form-input"
              placeholder="태그를 입력하고 Enter를 누르세요"
            />
            <button type="button" @click="addTag" class="btn btn-secondary">
              추가
            </button>
          </div>
          <div class="tags mt-2" v-if="form.tags.length > 0">
            <span
              v-for="(tag, index) in form.tags"
              :key="index"
              class="tag"
              @click="removeTag(index)"
              style="cursor: pointer;"
              title="클릭하여 삭제"
            >
              {{ tag }} ×
            </span>
          </div>
          <p class="text-muted mt-1">
            추천 태그: 웨이퍼, CVD, 포토리소그래피, 에칭, 증착, 이온주입, CMP, 검사, 측정
          </p>
        </div>

        <div class="form-group">
          <label class="form-label">이미지 첨부</label>
          <div
            class="image-upload"
            :class="{ dragover: isDragOver }"
            @drop="handleDrop"
            @dragover.prevent="isDragOver = true"
            @dragleave="isDragOver = false"
            @click="triggerFileInput"
          >
            <input
              type="file"
              ref="fileInput"
              @change="handleFileSelect"
              multiple
              accept="image/*"
              style="display: none;"
            />
            <div v-if="form.images.length === 0">
              <p>📷 이미지를 드래그하거나 클릭하여 업로드하세요</p>
              <p class="text-muted">JPG, PNG, GIF 파일을 지원합니다</p>
            </div>
            <div v-else>
              <p>{{ form.images.length }}개의 이미지가 선택되었습니다</p>
              <p class="text-muted">더 추가하려면 클릭하세요</p>
            </div>
          </div>

          <!-- 이미지 미리보기 -->
          <div v-if="form.images.length > 0" class="image-previews">
            <div
              v-for="(image, index) in form.images"
              :key="index"
              class="image-preview-item"
            >
              <img :src="image.url" :alt="image.name" class="image-preview" />
              <div class="image-info">
                <p class="image-name">{{ image.name }}</p>
                <button
                  type="button"
                  @click="removeImage(index)"
                  class="btn btn-danger btn-sm"
                >
                  삭제
                </button>
              </div>
            </div>
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn btn-primary" :disabled="!isFormValid">
            {{ isEdit ? '수정하기' : '저장하기' }}
          </button>
          <button type="button" @click="resetForm" class="btn btn-secondary">
            초기화
          </button>
          <router-link to="/diary" class="btn btn-secondary">
            취소
          </router-link>
        </div>
      </div>
    </form>
  </div>
</template>

<script>
import { useDiaryStore } from '../stores/diary'
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

export default {
  name: 'DiaryWrite',
  setup() {
    const diaryStore = useDiaryStore()
    const router = useRouter()
    const route = useRoute()
    
    const isEdit = computed(() => !!route.params.id)
    const fileInput = ref(null)
    const isDragOver = ref(false)
    const newTag = ref('')

    const form = ref({
      title: '',
      category: '',
      content: '',
      tags: [],
      images: []
    })

    const isFormValid = computed(() => {
      return form.value.title.trim() && form.value.content.trim()
    })

    onMounted(() => {
      if (isEdit.value) {
        const diary = diaryStore.getDiaryById(route.params.id)
        if (diary) {
          form.value = {
            title: diary.title,
            category: diary.category || '',
            content: diary.content,
            tags: [...diary.tags],
            images: [...(diary.images || [])]
          }
        }
      }
    })

    const addTag = () => {
      const tag = newTag.value.trim()
      if (tag && !form.value.tags.includes(tag)) {
        form.value.tags.push(tag)
        newTag.value = ''
      }
    }

    const removeTag = (index) => {
      form.value.tags.splice(index, 1)
    }

    const triggerFileInput = () => {
      fileInput.value.click()
    }

    const handleFileSelect = (event) => {
      const files = Array.from(event.target.files)
      processFiles(files)
    }

    const handleDrop = (event) => {
      event.preventDefault()
      isDragOver.value = false
      const files = Array.from(event.dataTransfer.files)
      processFiles(files)
    }

    const processFiles = (files) => {
      files.forEach(file => {
        if (file.type.startsWith('image/')) {
          const reader = new FileReader()
          reader.onload = (e) => {
            form.value.images.push({
              name: file.name,
              url: e.target.result,
              size: file.size
            })
          }
          reader.readAsDataURL(file)
        }
      })
    }

    const removeImage = (index) => {
      form.value.images.splice(index, 1)
    }

    const saveDiary = () => {
      if (!isFormValid.value) return

      const diaryData = {
        title: form.value.title.trim(),
        category: form.value.category,
        content: form.value.content.trim(),
        tags: form.value.tags,
        images: form.value.images
      }

      if (isEdit.value) {
        diaryStore.updateDiary(parseInt(route.params.id), diaryData)
      } else {
        diaryStore.addDiary(diaryData)
      }

      router.push('/diary')
    }

    const resetForm = () => {
      form.value = {
        title: '',
        category: '',
        content: '',
        tags: [],
        images: []
      }
      newTag.value = ''
    }

    return {
      form,
      isEdit,
      isFormValid,
      newTag,
      fileInput,
      isDragOver,
      addTag,
      removeTag,
      triggerFileInput,
      handleFileSelect,
      handleDrop,
      removeImage,
      saveDiary,
      resetForm
    }
  }
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.page-header h2 {
  font-size: 2rem;
  color: #333;
}

.diary-form {
  max-width: 800px;
}

.tag-input-container {
  display: flex;
  gap: 1rem;
}

.tag-input-container input {
  flex: 1;
}

.image-previews {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.image-preview-item {
  border: 1px solid #e1e5e9;
  border-radius: 8px;
  overflow: hidden;
}

.image-preview {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.image-info {
  padding: 0.75rem;
}

.image-name {
  font-size: 0.9rem;
  color: #666;
  margin-bottom: 0.5rem;
  word-break: break-all;
}

.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-start;
  flex-wrap: wrap;
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid #e1e5e9;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .tag-input-container {
    flex-direction: column;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .image-previews {
    grid-template-columns: 1fr;
  }
}
</style>
