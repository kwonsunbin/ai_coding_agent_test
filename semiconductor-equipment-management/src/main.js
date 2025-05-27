import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import './style.css'

// 컴포넌트 import
import Dashboard from './views/Dashboard.vue'
import EquipmentList from './views/EquipmentList.vue'
import EquipmentDetail from './views/EquipmentDetail.vue'
import ChangeTracking from './views/ChangeTracking.vue'
import ChangeDetail from './views/ChangeDetail.vue'
import Alerts from './views/Alerts.vue'

const routes = [
  { path: '/', name: 'Dashboard', component: Dashboard },
  { path: '/equipment', name: 'EquipmentList', component: EquipmentList },
  { path: '/equipment/:id', name: 'EquipmentDetail', component: EquipmentDetail },
  { path: '/changes', name: 'ChangeTracking', component: ChangeTracking },
  { path: '/changes/:id', name: 'ChangeDetail', component: ChangeDetail },
  { path: '/alerts', name: 'Alerts', component: Alerts }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const app = createApp(App)
app.use(router)
app.mount('#app')
