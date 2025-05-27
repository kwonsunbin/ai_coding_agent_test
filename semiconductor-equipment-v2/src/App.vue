<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-secondary-50 dark:from-gray-900 dark:to-gray-800 transition-colors duration-300">
    <!-- 데스크탑 네비게이션 -->
    <nav class="hidden md:block bg-white/80 dark:bg-gray-800/80 backdrop-blur-md border-b border-blue-100/50 dark:border-gray-700/50 shadow-soft sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center h-16">
          <!-- 로고 -->
          <div class="flex items-center">
            <div class="flex-shrink-0">
              <h1 class="text-xl font-bold gradient-text">반도체 설비 관리</h1>
            </div>
          </div>

          <!-- 네비게이션 메뉴 -->
          <div class="flex space-x-1">
            <router-link 
              to="/" 
              :class="[$route.name === 'Dashboard' ? 'nav-link-active' : 'nav-link-inactive', 'nav-link']"
            >
              <ChartBarIcon class="w-4 h-4 mr-2" />
              대시보드
            </router-link>
            <router-link 
              to="/equipment" 
              :class="[$route.name?.includes('Equipment') && $route.name !== 'EquipmentSpecs' ? 'nav-link-active' : 'nav-link-inactive', 'nav-link']"
            >
              <CogIcon class="w-4 h-4 mr-2" />
              설비 목록
            </router-link>
            <router-link 
              to="/equipment-specs" 
              :class="[$route.name === 'EquipmentSpecs' ? 'nav-link-active' : 'nav-link-inactive', 'nav-link']"
            >
              <DocumentTextIcon class="w-4 h-4 mr-2" />
              기준정보
            </router-link>
            <router-link 
              to="/lot-history" 
              :class="[$route.name?.includes('Lot') ? 'nav-link-active' : 'nav-link-inactive', 'nav-link']"
            >
              <DocumentTextIcon class="w-4 h-4 mr-2" />
              랏 이력
            </router-link>
            <router-link 
              to="/changes" 
              :class="[$route.name?.includes('Change') ? 'nav-link-active' : 'nav-link-inactive', 'nav-link']"
            >
              <ClockIcon class="w-4 h-4 mr-2" />
              변경 추적
            </router-link>
            <router-link 
              to="/alerts" 
              :class="[$route.name === 'Alerts' ? 'nav-link-active' : 'nav-link-inactive', 'nav-link']"
            >
              <BellIcon class="w-4 h-4 mr-2" />
              알림
              <span v-if="alertCount > 0" class="ml-2 bg-danger-500 text-white text-xs rounded-full px-2 py-0.5">
                {{ alertCount }}
              </span>
            </router-link>
          </div>

          <!-- 사용자 정보 -->
          <div class="flex items-center space-x-4">
            <!-- 다크모드 토글 -->
            <button 
              @click="toggleDarkMode"
              class="p-2 rounded-lg hover:bg-blue-50 transition-colors"
              :title="isDarkMode ? '라이트 모드로 전환' : '다크 모드로 전환'"
            >
              <SunIcon v-if="isDarkMode" class="w-5 h-5 text-yellow-500" />
              <MoonIcon v-else class="w-5 h-5 text-blue-600" />
            </button>
            
            <div class="relative">
              <BellIcon class="w-6 h-6 text-secondary-600 hover:text-blue-600 cursor-pointer transition-colors" />
              <span v-if="alertCount > 0" class="absolute -top-1 -right-1 bg-danger-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                {{ alertCount }}
              </span>
            </div>
            <div class="flex items-center space-x-2">
              <div class="w-8 h-8 bg-gradient-to-r from-blue-500 to-blue-600 rounded-full flex items-center justify-center">
                <UserIcon class="w-5 h-5 text-white" />
              </div>
              <div class="text-sm">
                <div class="font-medium text-secondary-900">관리자</div>
                <div class="text-secondary-500">공장 관리팀</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>

    <!-- 모바일 네비게이션 -->
    <div class="md:hidden bg-white/80 backdrop-blur-md border-b border-blue-100/50 shadow-soft sticky top-0 z-50">
      <div class="px-4 py-3">
        <div class="flex justify-between items-center">
          <h1 class="text-lg font-bold gradient-text">반도체 설비 관리</h1>
          <div class="flex items-center space-x-3">
            <div class="relative">
              <BellIcon class="w-6 h-6 text-secondary-600" />
              <span v-if="alertCount > 0" class="absolute -top-1 -right-1 bg-danger-500 text-white text-xs rounded-full w-5 h-5 flex items-center justify-center">
                {{ alertCount }}
              </span>
            </div>
            <div class="w-8 h-8 bg-gradient-to-r from-blue-500 to-blue-600 rounded-full flex items-center justify-center">
              <UserIcon class="w-5 h-5 text-white" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 메인 콘텐츠 -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
      <router-view />
    </main>

    <!-- 모바일 하단 네비게이션 -->
    <nav class="md:hidden fixed bottom-0 left-0 right-0 bg-white/90 backdrop-blur-md border-t border-blue-100/50 shadow-soft-lg">
      <div class="grid grid-cols-5 py-2">
        <router-link 
          to="/" 
          :class="[$route.name === 'Dashboard' ? 'text-blue-600' : 'text-secondary-500', 'flex flex-col items-center py-2 px-1 transition-colors']"
        >
          <ChartBarIcon class="w-5 h-5" />
          <span class="text-xs mt-1">대시보드</span>
        </router-link>
        <router-link 
          to="/equipment" 
          :class="[$route.name?.includes('Equipment') && $route.name !== 'EquipmentSpecs' ? 'text-blue-600' : 'text-secondary-500', 'flex flex-col items-center py-2 px-1 transition-colors']"
        >
          <CogIcon class="w-5 h-5" />
          <span class="text-xs mt-1">설비</span>
        </router-link>
        <router-link 
          to="/equipment-specs" 
          :class="[$route.name === 'EquipmentSpecs' ? 'text-blue-600' : 'text-secondary-500', 'flex flex-col items-center py-2 px-1 transition-colors']"
        >
          <DocumentTextIcon class="w-5 h-5" />
          <span class="text-xs mt-1">기준정보</span>
        </router-link>
        <router-link 
          to="/changes" 
          :class="[$route.name?.includes('Change') ? 'text-blue-600' : 'text-secondary-500', 'flex flex-col items-center py-2 px-1 transition-colors']"
        >
          <ClockIcon class="w-5 h-5" />
          <span class="text-xs mt-1">변경</span>
        </router-link>
        <router-link 
          to="/alerts" 
          :class="[$route.name === 'Alerts' ? 'text-blue-600' : 'text-secondary-500', 'flex flex-col items-center py-2 px-1 transition-colors relative']"
        >
          <BellIcon class="w-5 h-5" />
          <span class="text-xs mt-1">알림</span>
          <span v-if="alertCount > 0" class="absolute -top-1 right-1 bg-danger-500 text-white text-xs rounded-full w-4 h-4 flex items-center justify-center">
            {{ alertCount }}
          </span>
        </router-link>
      </div>
    </nav>

    <!-- 모바일 하단 여백 -->
    <div class="md:hidden h-20"></div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { 
  ChartBarIcon, 
  CogIcon, 
  ClockIcon, 
  BellIcon, 
  UserIcon,
  DocumentTextIcon,
  SunIcon,
  MoonIcon
} from '@heroicons/vue/24/outline'

export default {
  name: 'App',
  components: {
    ChartBarIcon,
    CogIcon,
    ClockIcon,
    BellIcon,
    UserIcon,
    DocumentTextIcon,
    SunIcon,
    MoonIcon
  },
  setup() {
    const alertCount = ref(3)
    const isDarkMode = ref(false)

    const toggleDarkMode = () => {
      isDarkMode.value = !isDarkMode.value
      if (isDarkMode.value) {
        document.documentElement.classList.add('dark')
        localStorage.setItem('darkMode', 'true')
      } else {
        document.documentElement.classList.remove('dark')
        localStorage.setItem('darkMode', 'false')
      }
    }

    onMounted(() => {
      // 다크모드 설정 복원
      const savedDarkMode = localStorage.getItem('darkMode')
      if (savedDarkMode === 'true') {
        isDarkMode.value = true
        document.documentElement.classList.add('dark')
      }

      // 실시간 알림 카운트 업데이트 로직
      // 실제 구현에서는 WebSocket이나 API 폴링을 사용
    })

    return {
      alertCount,
      isDarkMode,
      toggleDarkMode
    }
  }
}
</script>
