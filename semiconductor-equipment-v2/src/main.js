import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import './style.css'

// 컴포넌트 import
import Dashboard from './views/Dashboard.vue'
import EquipmentList from './views/EquipmentList.vue'
import EquipmentDetail from './views/EquipmentDetail.vue'
import EquipmentSpecs from './views/EquipmentSpecs.vue'
import LotHistory from './views/LotHistory.vue'
import LotDetail from './views/LotDetail.vue'
import ChangeTracking from './views/ChangeTracking.vue'
import ChangeDetail from './views/ChangeDetail.vue'
import Alerts from './views/Alerts.vue'

const routes = [
  { path: '/', name: 'Dashboard', component: Dashboard },
  { path: '/equipment', name: 'EquipmentList', component: EquipmentList },
  { path: '/equipment/:id', name: 'EquipmentDetail', component: EquipmentDetail },
  { path: '/equipment/:id/lots', name: 'EquipmentLotHistory', component: LotHistory },
  { path: '/equipment-specs', name: 'EquipmentSpecs', component: EquipmentSpecs },
  { path: '/lot-history', name: 'LotHistory', component: LotHistory },
  { path: '/lots/:id', name: 'LotDetail', component: LotDetail },
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
