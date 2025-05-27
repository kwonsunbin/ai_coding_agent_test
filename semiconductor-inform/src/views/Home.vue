<template>
  <div class="home">
    <div class="hero">
      <h2>반도체 엔지니어를 위한 전문 일기장</h2>
      <p>공정 경험, 장비 점검, 기술 노하우를 체계적으로 기록하고 관리하세요.</p>
      <div class="hero-actions">
        <router-link to="/diary/write" class="btn btn-primary">
          새 일기 작성
        </router-link>
        <router-link to="/diary" class="btn btn-secondary">
          일기 목록 보기
        </router-link>
      </div>
    </div>

    <div class="features">
      <h3>주요 기능</h3>
      <div class="grid grid-3">
        <div class="card">
          <div class="feature-icon">📝</div>
          <h4>일기 작성</h4>
          <p>반도체 공정, 장비 점검, 기술 경험을 상세히 기록할 수 있습니다.</p>
        </div>
        <div class="card">
          <div class="feature-icon">🔍</div>
          <h4>강력한 검색</h4>
          <p>제목, 내용, 태그를 통한 빠른 검색으로 필요한 정보를 즉시 찾을 수 있습니다.</p>
        </div>
        <div class="card">
          <div class="feature-icon">🖼️</div>
          <h4>이미지 관리</h4>
          <p>공정 사진, 장비 이미지, 데이터 차트를 첨부하여 시각적으로 기록할 수 있습니다.</p>
        </div>
        <div class="card">
          <div class="feature-icon">🏷️</div>
          <h4>태그 시스템</h4>
          <p>카테고리와 태그를 활용하여 체계적으로 분류하고 관리할 수 있습니다.</p>
        </div>
        <div class="card">
          <div class="feature-icon">📊</div>
          <h4>통계 분석</h4>
          <p>작성한 일기의 패턴과 트렌드를 분석하여 인사이트를 얻을 수 있습니다.</p>
        </div>
        <div class="card">
          <div class="feature-icon">💾</div>
          <h4>안전한 저장</h4>
          <p>중요한 기술 정보와 경험을 안전하게 보관하고 언제든 접근할 수 있습니다.</p>
        </div>
      </div>
    </div>

    <div class="recent-diaries" v-if="recentDiaries.length > 0">
      <h3>최근 작성된 일기</h3>
      <div class="grid grid-2">
        <div v-for="diary in recentDiaries" :key="diary.id" class="card">
          <h4>{{ diary.title }}</h4>
          <p class="text-muted">{{ formatDate(diary.date) }}</p>
          <p>{{ diary.content.substring(0, 100) }}...</p>
          <div class="tags">
            <span v-for="tag in diary.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
          <router-link :to="`/diary/${diary.id}`" class="btn btn-primary mt-2">
            자세히 보기
          </router-link>
        </div>
      </div>
    </div>

    <div class="stats">
      <h3>통계</h3>
      <div class="grid grid-3">
        <div class="card text-center">
          <div class="stat-number">{{ totalDiaries }}</div>
          <div class="stat-label">총 일기 수</div>
        </div>
        <div class="card text-center">
          <div class="stat-number">{{ totalTags }}</div>
          <div class="stat-label">사용된 태그 수</div>
        </div>
        <div class="card text-center">
          <div class="stat-number">{{ totalCategories }}</div>
          <div class="stat-label">카테고리 수</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useDiaryStore } from '../stores/diary'
import { computed } from 'vue'

export default {
  name: 'Home',
  setup() {
    const diaryStore = useDiaryStore()

    const recentDiaries = computed(() => {
      return diaryStore.diaries.slice(0, 4)
    })

    const totalDiaries = computed(() => diaryStore.diaries.length)
    const totalTags = computed(() => diaryStore.allTags.length)
    const totalCategories = computed(() => diaryStore.allCategories.length)

    const formatDate = (dateString) => {
      const date = new Date(dateString)
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    }

    return {
      recentDiaries,
      totalDiaries,
      totalTags,
      totalCategories,
      formatDate
    }
  }
}
</script>

<style scoped>
.hero {
  text-align: center;
  padding: 3rem 0;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: 15px;
  margin-bottom: 3rem;
}

.hero h2 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  color: #333;
}

.hero p {
  font-size: 1.2rem;
  color: #666;
  margin-bottom: 2rem;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.features {
  margin-bottom: 3rem;
}

.features h3 {
  text-align: center;
  margin-bottom: 2rem;
  font-size: 2rem;
  color: #333;
}

.feature-icon {
  font-size: 3rem;
  text-align: center;
  margin-bottom: 1rem;
}

.recent-diaries {
  margin-bottom: 3rem;
}

.recent-diaries h3 {
  margin-bottom: 2rem;
  font-size: 2rem;
  color: #333;
}

.stats h3 {
  text-align: center;
  margin-bottom: 2rem;
  font-size: 2rem;
  color: #333;
}

.stat-number {
  font-size: 3rem;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 0.5rem;
}

.stat-label {
  font-size: 1.1rem;
  color: #666;
}

@media (max-width: 768px) {
  .hero h2 {
    font-size: 2rem;
  }
  
  .hero p {
    font-size: 1rem;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
}
</style>
