<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 네비게이션 바 -->
    <nav class="bg-white shadow-sm border-b border-gray-200">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between h-16">
          <div class="flex">
            <div class="flex-shrink-0 flex items-center">
              <h1 class="text-xl font-bold text-gray-900">반도체 설비 관리</h1>
            </div>
            <div class="hidden sm:ml-6 sm:flex sm:space-x-8">
              <router-link
                v-for="item in navigation"
                :key="item.name"
                :to="item.href"
                class="inline-flex items-center px-1 pt-1 border-b-2 text-sm font-medium transition-colors duration-200"
                :class="$route.name === item.name 
                  ? 'border-primary-500 text-gray-900' 
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'"
              >
                <component :is="item.icon" class="w-4 h-4 mr-2" />
                {{ item.label }}
              </router-link>
            </div>
          </div>
          
          <!-- 알림 및 사용자 메뉴 -->
          <div class="flex items-center space-x-4">
            <!-- 알림 아이콘 -->
            <button class="relative p-2 text-gray-400 hover:text-gray-500">
              <BellIcon class="w-6 h-6" />
              <span v-if="alertCount > 0" class="absolute top-0 right-0 block h-2 w-2 rounded-full bg-danger-400 ring-2 ring-white"></span>
            </button>
            
            <!-- 사용자 정보 -->
            <div class="flex items-center space-x-3">
              <div class="text-sm text-right">
                <p class="font-medium text-gray-900">관리자</p>
                <p class="text-gray-500">공장 관리팀</p>
              </div>
              <div class="w-8 h-8 bg-primary-500 rounded-full flex items-center justify-center">
                <span class="text-white text-sm font-medium">관</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 모바일 메뉴 -->
      <div class="sm:hidden">
        <div class="pt-2 pb-3 space-y-1">
          <router-link
            v-for="item in navigation"
            :key="item.name"
            :to="item.href"
            class="flex items-center px-3 py-2 text-base font-medium transition-colors duration-200"
            :class="$route.name === item.name 
              ? 'bg-primary-50 border-primary-500 text-primary-700 border-l-4' 
              : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'"
          >
            <component :is="item.icon" class="w-5 h-5 mr-3" />
            {{ item.label }}
          </router-link>
        </div>
      </div>
    </nav>

    <!-- 메인 컨텐츠 -->
    <main class="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  HomeIcon,
  CpuChipIcon,
  ClockIcon,
  BellIcon,
  ExclamationTriangleIcon
} from '@heroicons/vue/24/outline'

const route = useRoute()

const navigation = [
  { name: 'Dashboard', label: '대시보드', href: '/', icon: HomeIcon },
  { name: 'EquipmentList', label: '설비 목록', href: '/equipment', icon: CpuChipIcon },
  { name: 'ChangeTracking', label: '변경 추적', href: '/changes', icon: ClockIcon },
  { name: 'Alerts', label: '알림', href: '/alerts', icon: ExclamationTriangleIcon }
]

const alertCount = ref(3) // 예시 알림 개수
</script>
