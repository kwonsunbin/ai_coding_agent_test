<template>
  <div class="diary-detail">
    <div v-if="!diary" class="not-found">
      <h2>일기를 찾을 수 없습니다</h2>
      <router-link to="/diary" class="btn btn-primary">
        목록으로 돌아가기
      </router-link>
    </div>

    <div v-else>
      <div class="page-header">
        <div class="header-content">
          <h1>{{ diary.title }}</h1>
          <div class="diary-meta">
            <span class="diary-date">{{ formatDate(diary.date) }}</span>
            <span v-if="diary.category" class="category-badge">{{ diary.category }}</span>
          </div>
        </div>
        <div class="header-actions">
          <router-link :to="`/diary/write?edit=${diary.id}`" class="btn btn-secondary">
            수정
          </router-link>
          <button @click="deleteDiary" class="btn btn-danger">
            삭제
          </button>
          <router-link to="/diary" class="btn btn-secondary">
            목록
          </router-link>
        </div>
      </div>

      <div class="diary-content">
        <div class="card">
          <div class="content-section">
            <h3>내용</h3>
            <div class="content-text">
              {{ diary.content }}
            </div>
          </div>

          <div v-if="diary.tags && diary.tags.length > 0" class="tags-section">
            <h3>태그</h3>
            <div class="tags">
              <span v-for="tag in diary.tags" :key="tag" class="tag">{{ tag }}</span>
            </div>
          </div>

          <div v-if="diary.images && diary.images.length > 0" class="images-section">
            <h3>첨부 이미지 ({{ diary.images.length }}개)</h3>
            <div class="image-gallery">
              <div
                v-for="(image, index) in diary.images"
                :key="index"
                class="image-item"
                @click="openImageModal(index)"
              >
                <img :src="image.url" :alt="image.name" class="gallery-image" />
                <div class="image-overlay">
                  <span class="image-name">{{ image.name }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 관련 일기 추천 -->
      <div v-if="relatedDiaries.length > 0" class="related-diaries">
        <h3>관련 일기</h3>
        <div class="grid grid-2">
          <div v-for="relatedDiary in relatedDiaries" :key="relatedDiary.id" class="card">
            <h4>{{ relatedDiary.title }}</h4>
            <p class="text-muted">{{ formatDate(relatedDiary.date) }}</p>
            <p>{{ relatedDiary.content.substring(0, 100) }}...</p>
            <div class="tags">
              <span v-for="tag in relatedDiary.tags.slice(0, 3)" :key="tag" class="tag">{{ tag }}</span>
            </div>
            <router-link :to="`/diary/${relatedDiary.id}`" class="btn btn-primary mt-2">
              읽어보기
            </router-link>
          </div>
        </div>
      </div>

      <!-- 이미지 모달 -->
      <div v-if="showImageModal" class="image-modal" @click="closeImageModal">
        <div class="modal-content" @click.stop>
          <button class="modal-close" @click="closeImageModal">&times;</button>
          <img :src="currentImage.url" :alt="currentImage.name" class="modal-image" />
          <div class="modal-info">
            <h4>{{ currentImage.name }}</h4>
            <div class="modal-navigation">
              <button
                @click="previousImage"
                :disabled="currentImageIndex === 0"
                class="btn btn-secondary"
              >
                이전
              </button>
              <span>{{ currentImageIndex + 1 }} / {{ diary.images.length }}</span>
              <button
                @click="nextImage"
                :disabled="currentImageIndex === diary.images.length - 1"
                class="btn btn-secondary"
              >
                다음
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useDiaryStore } from '../stores/diary'
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

export default {
  name: 'DiaryDetail',
  props: {
    id: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const diaryStore = useDiaryStore()
    const route = useRoute()
    const router = useRouter()
    
    const showImageModal = ref(false)
    const currentImageIndex = ref(0)

    const diary = computed(() => diaryStore.getDiaryById(props.id))
    
    const currentImage = computed(() => {
      if (diary.value && diary.value.images && diary.value.images.length > 0) {
        return diary.value.images[currentImageIndex.value]
      }
      return null
    })

    const relatedDiaries = computed(() => {
      if (!diary.value) return []
      
      return diaryStore.diaries
        .filter(d => d.id !== diary.value.id)
        .filter(d => {
          // 같은 카테고리이거나 공통 태그가 있는 일기
          const hasCommonCategory = d.category === diary.value.category
          const hasCommonTags = d.tags.some(tag => diary.value.tags.includes(tag))
          return hasCommonCategory || hasCommonTags
        })
        .slice(0, 4)
    })

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        weekday: 'long'
      })
    }

    const deleteDiary = () => {
      if (confirm('정말로 이 일기를 삭제하시겠습니까?')) {
        diaryStore.deleteDiary(parseInt(props.id))
        router.push('/diary')
      }
    }

    const openImageModal = (index) => {
      currentImageIndex.value = index
      showImageModal.value = true
      document.body.style.overflow = 'hidden'
    }

    const closeImageModal = () => {
      showImageModal.value = false
      document.body.style.overflow = 'auto'
    }

    const previousImage = () => {
      if (currentImageIndex.value > 0) {
        currentImageIndex.value--
      }
    }

    const nextImage = () => {
      if (currentImageIndex.value < diary.value.images.length - 1) {
        currentImageIndex.value++
      }
    }

    return {
      diary,
      relatedDiaries,
      showImageModal,
      currentImageIndex,
      currentImage,
      formatDate,
      deleteDiary,
      openImageModal,
      closeImageModal,
      previousImage,
      nextImage
    }
  }
}
</script>

<style scoped>
.not-found {
  text-align: center;
  padding: 3rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 2rem;
  gap: 2rem;
}

.header-content h1 {
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 1rem;
  line-height: 1.2;
}

.diary-meta {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.diary-date {
  color: #666;
  font-size: 1.1rem;
}

.category-badge {
  background-color: #667eea;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.content-section {
  margin-bottom: 2rem;
}

.content-section h3 {
  color: #333;
  margin-bottom: 1rem;
  font-size: 1.3rem;
}

.content-text {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #444;
  white-space: pre-wrap;
}

.tags-section {
  margin-bottom: 2rem;
}

.tags-section h3 {
  color: #333;
  margin-bottom: 1rem;
  font-size: 1.3rem;
}

.images-section h3 {
  color: #333;
  margin-bottom: 1rem;
  font-size: 1.3rem;
}

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1rem;
}

.image-item {
  position: relative;
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.2s;
}

.image-item:hover {
  transform: scale(1.02);
}

.gallery-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0,0,0,0.7));
  color: white;
  padding: 1rem;
}

.image-name {
  font-size: 0.9rem;
  font-weight: 500;
}

.related-diaries {
  margin-top: 3rem;
}

.related-diaries h3 {
  margin-bottom: 2rem;
  font-size: 2rem;
  color: #333;
}

/* 이미지 모달 */
.image-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  position: relative;
  max-width: 90%;
  max-height: 90%;
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.modal-close {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  font-size: 1.5rem;
  cursor: pointer;
  z-index: 1001;
}

.modal-image {
  width: 100%;
  height: auto;
  max-height: 70vh;
  object-fit: contain;
}

.modal-info {
  padding: 1rem;
  background: white;
}

.modal-info h4 {
  margin-bottom: 1rem;
  color: #333;
}

.modal-navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .header-content h1 {
    font-size: 2rem;
  }
  
  .header-actions {
    justify-content: stretch;
  }
  
  .header-actions .btn {
    flex: 1;
    text-align: center;
  }
  
  .image-gallery {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    max-width: 95%;
    max-height: 95%;
  }
}
</style>
