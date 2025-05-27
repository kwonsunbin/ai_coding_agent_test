<template>
  <div id="app">
    <header class="header">
      <div class="container">
        <h1>반도체 인폼</h1>
        <p>반도체 엔지니어를 위한 전문 일기장</p>
        <nav class="nav">
          <ul class="nav-list">
            <li>
              <router-link to="/" class="nav-link" :class="{ active: $route.name === 'home' }">
                홈
              </router-link>
            </li>
            <li>
              <router-link to="/diary" class="nav-link" :class="{ active: $route.name === 'diary-list' }">
                일기 목록
              </router-link>
            </li>
            <li>
              <router-link to="/diary/write" class="nav-link" :class="{ active: $route.name === 'diary-write' }">
                일기 작성
              </router-link>
            </li>
            <li>
              <router-link to="/search" class="nav-link" :class="{ active: $route.name === 'search' }">
                검색
              </router-link>
            </li>
          </ul>
        </nav>
      </div>
    </header>

    <main class="main">
      <div class="container">
        <router-view />
      </div>
    </main>

    <footer class="footer">
      <div class="container text-center">
        <p class="text-muted">&copy; 2024 반도체 인폼. 반도체 엔지니어를 위한 전문 일기장.</p>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
.footer {
  background-color: #f8f9fa;
  padding: 2rem 0;
  margin-top: 3rem;
  border-top: 1px solid #e9ecef;
}
</style>
