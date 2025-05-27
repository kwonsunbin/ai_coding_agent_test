import { defineStore } from 'pinia'

export const useDiaryStore = defineStore('diary', {
  state: () => ({
    diaries: [
      {
        id: 1,
        title: '첫 번째 반도체 공정 경험',
        content: '오늘은 웨이퍼 제조 공정에 대해 배웠다. 실리콘 웨이퍼의 품질이 최종 제품에 미치는 영향이 생각보다 크다는 것을 알게 되었다.',
        date: '2024-01-15',
        tags: ['웨이퍼', '제조공정', '품질관리'],
        images: [],
        category: '공정'
      },
      {
        id: 2,
        title: 'CVD 장비 점검 일지',
        content: 'Chemical Vapor Deposition 장비의 정기 점검을 실시했다. 가스 플로우 레이트와 온도 프로파일을 확인했으며, 모든 파라미터가 정상 범위 내에 있음을 확인했다.',
        date: '2024-01-16',
        tags: ['CVD', '장비점검', '가스플로우'],
        images: [],
        category: '장비'
      },
      {
        id: 3,
        title: '포토리소그래피 공정 최적화',
        content: '포토레지스트 코팅 두께를 조정하여 패턴 해상도를 개선했다. 스핀 속도를 3000rpm에서 3500rpm으로 증가시킨 결과 균일도가 향상되었다.',
        date: '2024-01-17',
        tags: ['포토리소그래피', '최적화', '패턴'],
        images: [],
        category: '공정'
      }
    ],
    searchQuery: '',
    selectedCategory: '',
    selectedTags: []
  }),

  getters: {
    filteredDiaries: (state) => {
      let filtered = state.diaries

      // 텍스트 검색
      if (state.searchQuery) {
        const query = state.searchQuery.toLowerCase()
        filtered = filtered.filter(diary => 
          diary.title.toLowerCase().includes(query) ||
          diary.content.toLowerCase().includes(query) ||
          diary.tags.some(tag => tag.toLowerCase().includes(query))
        )
      }

      // 카테고리 필터
      if (state.selectedCategory) {
        filtered = filtered.filter(diary => diary.category === state.selectedCategory)
      }

      // 태그 필터
      if (state.selectedTags.length > 0) {
        filtered = filtered.filter(diary => 
          state.selectedTags.some(tag => diary.tags.includes(tag))
        )
      }

      return filtered.sort((a, b) => new Date(b.date) - new Date(a.date))
    },

    allTags: (state) => {
      const tags = new Set()
      state.diaries.forEach(diary => {
        diary.tags.forEach(tag => tags.add(tag))
      })
      return Array.from(tags).sort()
    },

    allCategories: (state) => {
      const categories = new Set()
      state.diaries.forEach(diary => {
        if (diary.category) categories.add(diary.category)
      })
      return Array.from(categories).sort()
    },

    getDiaryById: (state) => (id) => {
      return state.diaries.find(diary => diary.id === parseInt(id))
    }
  },

  actions: {
    addDiary(diary) {
      const newDiary = {
        id: Date.now(),
        ...diary,
        date: new Date().toISOString().split('T')[0]
      }
      this.diaries.unshift(newDiary)
    },

    updateDiary(id, updatedDiary) {
      const index = this.diaries.findIndex(diary => diary.id === id)
      if (index !== -1) {
        this.diaries[index] = { ...this.diaries[index], ...updatedDiary }
      }
    },

    deleteDiary(id) {
      const index = this.diaries.findIndex(diary => diary.id === id)
      if (index !== -1) {
        this.diaries.splice(index, 1)
      }
    },

    setSearchQuery(query) {
      this.searchQuery = query
    },

    setSelectedCategory(category) {
      this.selectedCategory = category
    },

    setSelectedTags(tags) {
      this.selectedTags = tags
    },

    clearFilters() {
      this.searchQuery = ''
      this.selectedCategory = ''
      this.selectedTags = []
    }
  }
})
