import axios from 'axios'

// API 기본 설정
const API_BASE_URL = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
})

// 요청 인터셉터
api.interceptors.request.use(
  (config) => {
    console.log('API Request:', config.method?.toUpperCase(), config.url)
    return config
  },
  (error) => {
    console.error('API Request Error:', error)
    return Promise.reject(error)
  }
)

// 응답 인터셉터
api.interceptors.response.use(
  (response) => {
    console.log('API Response:', response.status, response.config.url)
    return response
  },
  (error) => {
    console.error('API Response Error:', error.response?.status, error.response?.data)
    return Promise.reject(error)
  }
)

// Action Items API
export const actionItemsApi = {
  // 목록 조회
  getAll: (params = {}) => {
    return api.get('/action-items', { params })
  },
  
  // 상세 조회
  getById: (id) => {
    return api.get(`/action-items/${id}`)
  },
  
  // 생성
  create: (data) => {
    return api.post('/action-items', data)
  },
  
  // 수정
  update: (id, data) => {
    return api.put(`/action-items/${id}`, data)
  },
  
  // 삭제
  delete: (id) => {
    return api.delete(`/action-items/${id}`)
  },
  
  // 담당자별 조회
  getByAssignee: (assignee) => {
    return api.get(`/action-items/assignee/${assignee}`)
  },
  
  // 상태별 조회
  getByStatus: (status) => {
    return api.get(`/action-items/status/${status}`)
  },
  
  // 우선순위별 조회
  getByPriority: (priority) => {
    return api.get(`/action-items/priority/${priority}`)
  },
  
  // 연체된 항목 조회
  getOverdue: () => {
    return api.get('/action-items/overdue')
  },
  
  // 통계 조회
  getStatistics: () => {
    return api.get('/action-items/statistics')
  }
}

// Informs API
export const informsApi = {
  // 목록 조회
  getAll: (params = {}) => {
    return api.get('/informs', { params })
  },
  
  // 상세 조회
  getById: (id) => {
    return api.get(`/informs/${id}`)
  },
  
  // 생성
  create: (data) => {
    return api.post('/informs', data)
  },
  
  // 수정
  update: (id, data) => {
    return api.put(`/informs/${id}`, data)
  },
  
  // 삭제
  delete: (id) => {
    return api.delete(`/informs/${id}`)
  },
  
  // 상태별 조회
  getByStatus: (status) => {
    return api.get(`/informs/status/${status}`)
  },
  
  // 카테고리별 조회
  getByCategory: (category) => {
    return api.get(`/informs/category/${category}`)
  },
  
  // 우선순위별 조회
  getByPriority: (priority) => {
    return api.get(`/informs/priority/${priority}`)
  },
  
  // 통계 조회
  getStatistics: () => {
    return api.get('/informs/statistics')
  }
}

// 공통 API 유틸리티
export const apiUtils = {
  // 에러 메시지 추출
  getErrorMessage: (error) => {
    if (error.response?.data?.error) {
      return error.response.data.error
    }
    if (error.response?.data?.message) {
      return error.response.data.message
    }
    if (error.message) {
      return error.message
    }
    return '알 수 없는 오류가 발생했습니다.'
  },
  
  // 성공 여부 확인
  isSuccess: (response) => {
    return response.status >= 200 && response.status < 300
  }
}

export default api
