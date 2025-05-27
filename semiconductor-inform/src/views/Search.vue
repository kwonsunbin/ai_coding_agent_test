<template>
  <div class="search-page">
    <div class="page-header">
      <h2>통합 검색</h2>
      <p class="text-muted">일기 내용과 이미지를 검색할 수 있습니다</p>
    </div>

    <!-- 검색 폼 -->
    <div class="search-form">
      <div class="card">
        <div class="search-input-container">
          <input
            type="text"
            v-model="searchQuery"
            @input="performSearch"
            placeholder="제목, 내용, 태그로 검색하세요..."
            class="form-input search-main-input"
          />
          <button @click="performSearch" class="btn btn-primary">
            🔍 검색
          </button>
        </div>

        <!-- 고급 검색 옵션 -->
        <div class="advanced-search">
          <button
            @click="showAdvanced = !showAdvanced"
            class="btn btn-secondary"
            type="button"
          >
            {{ showAdvanced ? '간단 검색' : '고급 검색' }}
          </button>
        </div>

        <div v-if="showAdvanced" class="advanced-options">
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">카테고리</label>
              <select v-model="selectedCategory" @change="performSearch" class="form-select">
                <option value="">모든 카테고리</option>
                <option v-for="category in allCategories" :key="category" :value="category">
                  {{ category }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label class="form-label">날짜 범위</label>
              <div class="date-range">
                <input
                  type="date"
                  v-model="dateFrom"
                  @change="performSearch"
                  class="form-input"
                />
                <span>~</span>
                <input
                  type="date"
                  v-model="dateTo"
                  @change="performSearch"
                  class="form-input"
                />
              </div>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">태그 필터</label>
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

          <div class="form-group">
            <label class="form-label">검색 옵션</label>
            <div class="search-options">
              <label class="checkbox-label">
                <input type="checkbox" v-model="searchInImages" @change="performSearch" />
                이미지가 있는 일기만
              </label>
              <label class="checkbox-label">
                <input type="checkbox" v-model="exactMatch" @change="performSearch" />
                정확한 단어 일치
              </label>
            </div>
          </div>
        </div>

        <div class="search-actions">
          <button @click="clearSearch" class="btn btn-secondary">
            검색 초기화
          </button>
        </div>
      </div>
    </div>

    <!-- 검색 결과 -->
    <div class="search-results">
      <div v-if="searchQuery || hasFilters" class="results-header">
        <h3>
          검색 결과
          <span class="result-count">({{ searchResults.length }}개)</span>
        </h3>
        <div class="search-summary">
          <span v-if="searchQuery">검색어: "{{ searchQuery }}"</span>
          <span v-if="selectedCategory">카테고리: {{ selectedCategory }}</span>
          <span v-if="selectedTags.length > 0">태그: {{ selectedTags.join(', ') }}</span>
          <span v-if="dateFrom || dateTo">
            기간: {{ formatDateRange() }}
          </span>
        </div>
      </div>

      <div v-if="searchResults.length === 0 && (searchQuery || hasFilters)" class="no-results">
        <div class="no-results-content">
          <h3>검색 결과가 없습니다</h3>
          <p>다른 검색어나 필터를 시도해보세요.</p>
          <div class="search-suggestions">
            <h4>검색 팁:</h4>
            <ul>
              <li>더 간단한 검색어를 사용해보세요</li>
              <li>태그나 카테고리 필터를 조정해보세요</li>
              <li>날짜 범위를 넓혀보세요</li>
              <li>정확한 단어 일치 옵션을 해제해보세요</li>
            </ul>
          </div>
        </div>
      </div>

      <div v-else-if="searchResults.length > 0" class="results-grid">
        <div class="grid grid-2">
          <div v-for="diary in searchResults" :key="diary.id" class="card result-card">
            <div class="result-header">
              <h4>{{ highlightText(diary.title) }}</h4>
              <span class="result-date">{{ formatDate(diary.date) }}</span>
            </div>

            <div v-if="diary.category" class="result-category">
              <span class="category-badge">{{ diary.category }}</span>
            </div>

            <div class="result-content">
              <p v-html="highlightText(diary.content.substring(0, 200))"></p>
              <span v-if="diary.content.length > 200" class="text-muted">...</span>
            </div>

            <div v-if="diary.images && diary.images.length > 0" class="result-images">
              <div class="image-indicator">
                📷 {{ diary.images.length }}개의 이미지
              </div>
              <div class="image-thumbnails">
                <img
                  v-for="(image, index) in diary.images.slice(0, 3)"
                  :key="index"
                  :src="image.url"
                  :alt="image.name"
                  class="thumbnail"
                />
                <span v-if="diary.images.length > 3" class="more-images">
                  +{{ diary.images.length - 3 }}
                </span>
              </div>
            </div>

            <div v-if="diary.tags && diary.tags.length > 0" class="result-tags">
              <div class="tags">
                <span
                  v-for="tag in diary.tags"
                  :key="tag"
                  :class="['tag', { highlighted: selectedTags.includes(tag) }]"
                >
                  {{ tag }}
                </span>
              </div>
            </div>

            <div class="result-actions">
              <router-link :to="`/diary/${diary.id}`" class="btn btn-primary">
                자세히 보기
              </router-link>
            </div>
          </div>
        </div>
      </div>

      <!-- 기본 상태 (검색하지 않은 경우) -->
      <div v-else class="search-welcome">
        <div class="welcome-content">
          <h3>반도체 인폼 검색</h3>
          <p>저장된 일기와 이미지를 빠르게 찾아보세요.</p>
          
          <div class="search-features">
            <div class="feature-item">
              <div class="feature-icon">🔍</div>
              <h4>텍스트 검색</h4>
              <p>제목, 내용, 태그에서 원하는 정보를 찾을 수 있습니다.</p>
            </div>
            <div class="feature-item">
              <div class="feature-icon">🖼️</div>
              <h4>이미지 검색</h4>
              <p>이미지가 포함된 일기만 필터링할 수 있습니다.</p>
            </div>
            <div class="feature-item">
              <div class="feature-icon">🏷️</div>
              <h4>태그 필터</h4>
              <p>관련 태그로 정확한 내용을 찾을 수 있습니다.</p>
            </div>
          </div>

          <div class="popular-tags" v-if="allTags.length > 0">
            <h4>인기 태그</h4>
            <div class="tags">
              <span
                v-for="tag in allTags.slice(0, 10)"
                :key="tag"
                @click="quickSearch(tag)"
                class="tag clickable"
              >
                {{ tag }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useDiaryStore } from '../stores/diary'
import { computed, ref, watch } from 'vue'

export default {
  name: 'Search',
  setup() {
    const diaryStore = useDiaryStore()
    
    const searchQuery = ref('')
    const selectedCategory = ref('')
    const selectedTags = ref([])
    const dateFrom = ref('')
    const dateTo = ref('')
    const searchInImages = ref(false)
    const exactMatch = ref(false)
    const showAdvanced = ref(false)

    const allTags = computed(() => diaryStore.allTags)
    const allCategories = computed(() => diaryStore.allCategories)

    const hasFilters = computed(() => {
      return selectedCategory.value || 
             selectedTags.value.length > 0 || 
             dateFrom.value || 
             dateTo.value || 
             searchInImages.value
    })

    const searchResults = computed(() => {
      if (!searchQuery.value && !hasFilters.value) {
        return []
      }

      let results = diaryStore.diaries

      // 텍스트 검색
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        results = results.filter(diary => {
          if (exactMatch.value) {
            return diary.title.toLowerCase().includes(query) ||
                   diary.content.toLowerCase().includes(query) ||
                   diary.tags.some(tag => tag.toLowerCase() === query)
          } else {
            return diary.title.toLowerCase().includes(query) ||
                   diary.content.toLowerCase().includes(query) ||
                   diary.tags.some(tag => tag.toLowerCase().includes(query))
          }
        })
      }

      // 카테고리 필터
      if (selectedCategory.value) {
        results = results.filter(diary => diary.category === selectedCategory.value)
      }

      // 태그 필터
      if (selectedTags.value.length > 0) {
        results = results.filter(diary => 
          selectedTags.value.some(tag => diary.tags.includes(tag))
        )
      }

      // 날짜 필터
      if (dateFrom.value) {
        results = results.filter(diary => diary.date >= dateFrom.value)
      }
      if (dateTo.value) {
        results = results.filter(diary => diary.date <= dateTo.value)
      }

      // 이미지 필터
      if (searchInImages.value) {
        results = results.filter(diary => diary.images && diary.images.length > 0)
      }

      return results.sort((a, b) => new Date(b.date) - new Date(a.date))
    })

    const toggleTag = (tag) => {
      const index = selectedTags.value.indexOf(tag)
      if (index > -1) {
        selectedTags.value.splice(index, 1)
      } else {
        selectedTags.value.push(tag)
      }
      performSearch()
    }

    const performSearch = () => {
      // 검색 로직은 computed에서 자동으로 처리됨
    }

    const clearSearch = () => {
      searchQuery.value = ''
      selectedCategory.value = ''
      selectedTags.value = []
      dateFrom.value = ''
      dateTo.value = ''
      searchInImages.value = false
      exactMatch.value = false
    }

    const quickSearch = (tag) => {
      selectedTags.value = [tag]
      performSearch()
    }

    const highlightText = (text) => {
      if (!searchQuery.value) return text
      
      const regex = new RegExp(`(${searchQuery.value})`, 'gi')
      return text.replace(regex, '<mark>$1</mark>')
    }

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }

    const formatDateRange = () => {
      if (dateFrom.value && dateTo.value) {
        return `${formatDate(dateFrom.value)} ~ ${formatDate(dateTo.value)}`
      } else if (dateFrom.value) {
        return `${formatDate(dateFrom.value)} 이후`
      } else if (dateTo.value) {
        return `${formatDate(dateTo.value)} 이전`
      }
      return ''
    }

    return {
      searchQuery,
      selectedCategory,
      selectedTags,
      dateFrom,
      dateTo,
      searchInImages,
      exactMatch,
      showAdvanced,
      allTags,
      allCategories,
      hasFilters,
      searchResults,
      toggleTag,
      performSearch,
      clearSearch,
      quickSearch,
      highlightText,
      formatDate,
      formatDateRange
    }
  }
}
</script>

<style scoped>
.page-header {
  text-align: center;
  margin-bottom: 2rem;
}

.page-header h2 {
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 0.5rem;
}

.search-input-container {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.search-main-input {
  flex: 1;
  font-size: 1.1rem;
  padding: 1rem;
}

.advanced-search {
  margin-bottom: 1rem;
}

.advanced-options {
  border-top: 1px solid #e1e5e9;
  padding-top: 1.5rem;
  margin-top: 1rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-bottom: 1rem;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.date-range input {
  flex: 1;
}

.search-options {
  display: flex;
  gap: 2rem;
  flex-wrap: wrap;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.search-actions {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #e1e5e9;
}

.results-header {
  margin-bottom: 2rem;
}

.results-header h3 {
  font-size: 1.8rem;
  color: #333;
  margin-bottom: 0.5rem;
}

.result-count {
  color: #667eea;
  font-weight: normal;
}

.search-summary {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
  color: #666;
}

.search-summary span {
  background-color: #f8f9fa;
  padding: 0.25rem 0.75rem;
  border-radius: 15px;
  font-size: 0.9rem;
}

.no-results {
  text-align: center;
  padding: 3rem;
}

.no-results-content h3 {
  color: #666;
  margin-bottom: 1rem;
}

.search-suggestions {
  text-align: left;
  max-width: 400px;
  margin: 2rem auto;
}

.search-suggestions h4 {
  margin-bottom: 1rem;
  color: #333;
}

.search-suggestions ul {
  list-style-position: inside;
  color: #666;
}

.search-suggestions li {
  margin-bottom: 0.5rem;
}

.result-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

.result-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
  gap: 1rem;
}

.result-header h4 {
  font-size: 1.3rem;
  color: #333;
  margin: 0;
  flex: 1;
}

.result-date {
  color: #666;
  font-size: 0.9rem;
  white-space: nowrap;
}

.result-category {
  margin-bottom: 1rem;
}

.result-content {
  margin-bottom: 1rem;
}

.result-content p {
  color: #666;
  line-height: 1.6;
}

.result-images {
  margin-bottom: 1rem;
}

.image-indicator {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.image-thumbnails {
  display: flex;
  gap: 0.5rem;
  align-items: center;
}

.thumbnail {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
}

.more-images {
  color: #666;
  font-size: 0.9rem;
}

.result-tags {
  margin-bottom: 1rem;
}

.tag.highlighted {
  background-color: #667eea;
  color: white;
}

.result-actions {
  display: flex;
  justify-content: flex-start;
}

.search-welcome {
  text-align: center;
  padding: 3rem;
}

.welcome-content h3 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 1rem;
}

.welcome-content p {
  font-size: 1.1rem;
  color: #666;
  margin-bottom: 3rem;
}

.search-features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.feature-item {
  text-align: center;
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.feature-item h4 {
  color: #333;
  margin-bottom: 0.5rem;
}

.feature-item p {
  color: #666;
  font-size: 0.9rem;
}

.popular-tags h4 {
  margin-bottom: 1rem;
  color: #333;
}

.tag.clickable {
  cursor: pointer;
  transition: all 0.2s;
}

.tag.clickable:hover {
  background-color: #667eea;
  color: white;
}

mark {
  background-color: #fff3cd;
  padding: 0.1rem 0.2rem;
  border-radius: 2px;
}

@media (max-width: 768px) {
  .search-input-container {
    flex-direction: column;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .date-range {
    flex-direction: column;
  }
  
  .search-options {
    flex-direction: column;
    gap: 1rem;
  }
  
  .search-summary {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .result-header {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .search-features {
    grid-template-columns: 1fr;
  }
}
</style>
