<template>
  <div class="diary-list">
    <div class="page-header">
      <h2>일기 목록</h2>
      <router-link to="/diary/write" class="btn btn-primary">
        새 일기 작성
      </router-link>
    </div>

    <!-- 검색 및 필터 -->
    <div class="search-container">
      <div class="search-input">
        <input
          type="text"
          v-model="searchQuery"
          @input="updateSearch"
          placeholder="제목, 내용, 태그로 검색..."
          class="form-input"
        />
      </div>
      <div class="search-filters">
        <select v-model="selectedCategory" @change="updateCategory" class="form-select">
          <option value="">모든 카테고리</option>
          <option v-for="category in allCategories" :key="category" :value="category">
            {{ category }}
          </option>
        </select>
        <button @click="clearFilters" class="btn btn-secondary">
          필터 초기화
        </button>
      </div>
    </div>

    <!-- 태그 필터 -->
    <div class="tags-filter" v-if="allTags.length > 0">
      <h4>태그로 필터링:</h4>
      <div class="tags">
        <span
          v-for="tag in allTags"
          :key="tag"
          @click="toggleTag(tag)"
          :class="['tag', { selected: selectedTags.includes(tag) }]"
        >
          {{ tag }}
        </span>
      </div>
    </div>

    <!-- 일기 목록 -->
    <div class="diaries-container">
      <div v-if="filteredDiaries.length === 0" class="no-results">
        <p>검색 결과가 없습니다.</p>
        <router-link to="/diary/write" class="btn btn-primary">
          첫 번째 일기 작성하기
        </router-link>
      </div>
      
      <div v-else class="grid grid-2">
        <div v-for="diary in filteredDiaries" :key="diary.id" class="card diary-card">
          <div class="diary-header">
            <h3>{{ diary.title }}</h3>
            <span class="diary-date">{{ formatDate(diary.date) }}</span>
          </div>
          
          <div class="diary-category" v-if="diary.category">
            <span class="category-badge">{{ diary.category }}</span>
          </div>
          
          <div class="diary-content">
            <p>{{ diary.content.substring(0, 150) }}{{ diary.content.length > 150 ? '...' : '' }}</p>
          </div>
          
          <div class="diary-images" v-if="diary.images && diary.images.length > 0">
            <div class="image-count">
              📷 {{ diary.images.length }}개의 이미지
            </div>
          </div>
          
          <div class="diary-tags" v-if="diary.tags && diary.tags.length > 0">
            <div class="tags">
              <span v-for="tag in diary.tags" :key="tag" class="tag">{{ tag }}</span>
            </div>
          </div>
          
          <div class="diary-actions">
            <router-link :to="`/diary/${diary.id}`" class="btn btn-primary">
              자세히 보기
            </router-link>
            <button @click="deleteDiary(diary.id)" class="btn btn-danger">
              삭제
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 통계 정보 -->
    <div class="stats-summary">
      <div class="grid grid-3">
        <div class="card text-center">
          <div class="stat-number">{{ filteredDiaries.length }}</div>
          <div class="stat-label">검색 결과</div>
        </div>
        <div class="card text-center">
          <div class="stat-number">{{ totalDiaries }}</div>
          <div class="stat-label">전체 일기</div>
        </div>
        <div class="card text-center">
          <div class="stat-number">{{ selectedTags.length }}</div>
          <div class="stat-label">선택된 태그</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useDiaryStore } from '../stores/diary'
import { computed, ref } from 'vue'

export default {
  name: 'DiaryList',
  setup() {
    const diaryStore = useDiaryStore()
    
    const searchQuery = ref(diaryStore.searchQuery)
    const selectedCategory = ref(diaryStore.selectedCategory)
    const selectedTags = ref([...diaryStore.selectedTags])

    const filteredDiaries = computed(() => diaryStore.filteredDiaries)
    const allTags = computed(() => diaryStore.allTags)
    const allCategories = computed(() => diaryStore.allCategories)
    const totalDiaries = computed(() => diaryStore.diaries.length)

    const updateSearch = () => {
      diaryStore.setSearchQuery(searchQuery.value)
    }

    const updateCategory = () => {
      diaryStore.setSelectedCategory(selectedCategory.value)
    }

    const toggleTag = (tag) => {
      const index = selectedTags.value.indexOf(tag)
      if (index > -1) {
        selectedTags.value.splice(index, 1)
      } else {
        selectedTags.value.push(tag)
      }
      diaryStore.setSelectedTags([...selectedTags.value])
    }

    const clearFilters = () => {
      searchQuery.value = ''
      selectedCategory.value = ''
      selectedTags.value = []
      diaryStore.clearFilters()
    }

    const deleteDiary = (id) => {
      if (confirm('정말로 이 일기를 삭제하시겠습니까?')) {
        diaryStore.deleteDiary(id)
      }
    }

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }

    return {
      searchQuery,
      selectedCategory,
      selectedTags,
      filteredDiaries,
      allTags,
      allCategories,
      totalDiaries,
      updateSearch,
      updateCategory,
      toggleTag,
      clearFilters,
      deleteDiary,
      formatDate
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

.tags-filter {
  margin-bottom: 2rem;
}

.tags-filter h4 {
  margin-bottom: 1rem;
  color: #555;
}

.diary-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

.diary-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}

.diary-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
  gap: 1rem;
}

.diary-header h3 {
  font-size: 1.3rem;
  color: #333;
  margin: 0;
  flex: 1;
}

.diary-date {
  color: #666;
  font-size: 0.9rem;
  white-space: nowrap;
}

.diary-category {
  margin-bottom: 1rem;
}

.category-badge {
  background-color: #667eea;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 15px;
  font-size: 0.8rem;
  font-weight: 500;
}

.diary-content {
  margin-bottom: 1rem;
}

.diary-content p {
  color: #666;
  line-height: 1.6;
}

.diary-images {
  margin-bottom: 1rem;
}

.image-count {
  color: #666;
  font-size: 0.9rem;
}

.diary-tags {
  margin-bottom: 1rem;
}

.diary-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-start;
}

.no-results {
  text-align: center;
  padding: 3rem;
  color: #666;
}

.no-results p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
}

.stats-summary {
  margin-top: 3rem;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 0.5rem;
}

.stat-label {
  color: #666;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .diary-header {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .diary-actions {
    flex-direction: column;
  }
}
</style>
