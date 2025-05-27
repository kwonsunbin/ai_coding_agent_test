import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import DiaryList from '../views/DiaryList.vue'
import DiaryWrite from '../views/DiaryWrite.vue'
import DiaryDetail from '../views/DiaryDetail.vue'
import Search from '../views/Search.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home
    },
    {
      path: '/diary',
      name: 'diary-list',
      component: DiaryList
    },
    {
      path: '/diary/write',
      name: 'diary-write',
      component: DiaryWrite
    },
    {
      path: '/diary/:id',
      name: 'diary-detail',
      component: DiaryDetail,
      props: true
    },
    {
      path: '/search',
      name: 'search',
      component: Search
    }
  ]
})

export default router
