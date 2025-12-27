<script setup>
import { ref, onMounted, computed } from 'vue'
import { RouterView, RouterLink, useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { cartState } from '@/utils/cart'

const router = useRouter()
const user = ref(null)
const searchKeyword = ref('')

const cartCount = computed(() => cartState.value.length)

onMounted(() => {
  const userStr = localStorage.getItem('user')
  if (userStr) {
    try {
      user.value = JSON.parse(userStr)
    } catch (e) {
      localStorage.removeItem('user')
    }
  }
})

const handleLogout = () => {
  localStorage.removeItem('user')
  user.value = null
  router.push('/login')
  setTimeout(() => location.reload(), 100)
}

const doSearch = () => {
  const query = searchKeyword.value.trim() ? { q: searchKeyword.value.trim() } : {}
  router.push({ path: '/search', query: query })
}
</script>

<template>
  <div class="steam-container">
    <div class="global-header">
      <div class="content-width global-nav">
        <div class="global-actions">
          <template v-if="!user">
            <el-button type="info" link class="global-btn login-btn" @click="router.push('/login')">登录</el-button>
          </template>
          <template v-else>
            <span class="user-welcome">欢迎回来, <strong>{{ user.nickname || user.username }}</strong></span>
            <el-button type="info" link class="global-btn logout-btn" @click="handleLogout">注销</el-button>
          </template>
        </div>
      </div>
    </div>

    <header class="steam-header">
      <div class="content-width header-inner">
        <div class="logo-wrapper" @click="router.push('/')">
          <img src="https://store.cloudflare.steamstatic.com/public/shared/images/header/logo_steam.svg?t=962016" class="steam-logo-img" alt="Steam Logo" />
        </div>

        <nav class="nav-links">
          <RouterLink to="/" class="nav-item">商店</RouterLink>
          <a href="#" class="nav-item">社区</a>
          <a href="#" class="nav-item">关于</a>
          <a href="#" class="nav-item">客服</a>
          <RouterLink v-if="user && user.role === 1" to="/admin" class="nav-item admin-link">后台管理</RouterLink>
        </nav>

        <div class="header-right-area">
          <div class="steam-search-bar">
            <input type="text" placeholder="搜索" v-model="searchKeyword" @keyup.enter="doSearch" />
            <button class="steam-search-btn" @click="doSearch">
              <el-icon><Search /></el-icon>
            </button>
          </div>

          <div class="steam-cart-box" v-if="user" @click="router.push('/cart')">
            <svg class="cart-icon" viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
              <path d="M7 18c-1.1 0-1.99.9-1.99 2S5.9 22 7 22s2-.9 2-2-.9-2-2-2zM1 2v2h2l3.6 7.59-1.35 2.45c-.16.28-.25.61-.25.96 0 1.1.9 2 2 2h12v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12.9-1.63h7.45c.75 0 1.41-.41 1.75-1.03l3.58-6.49c.08-.14.12-.31.12-.48 0-.55-.45-1-1-1H5.21l-.94-2H1zm16 16c-1.1 0-1.99.9-1.99 2s.89 2 1.99 2 2-.9 2-2-.9-2-2-2z"/>
            </svg>
            <span class="cart-text">购物车</span>
            <span class="cart-badge">{{ cartCount }}</span>
          </div>
        </div>
      </div>
    </header>

    <main class="main-content">
      <RouterView />
    </main>

    <footer class="steam-footer">
      <div class="content-width">
        <div class="footer-text">
          <p>© 2025 Valve Corporation. 保留所有权利。</p>
          <div class="footer-links">
            <a href="#">隐私政策</a> | <a href="#">法律信息</a> | <a href="#">Steam 订户协议</a> | <a href="#">退款</a>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Motiva+Sans:wght@300;400;500;700&display=swap');

:root { --steam-bg-dark: #171a21; --steam-bg-main: #1b2838; --steam-text-color: #c6d4df; --steam-accent: #66c0f4; }
body { margin: 0; padding: 0; background-color: var(--steam-bg-main); color: var(--steam-text-color); font-family: "Motiva Sans", Arial, sans-serif; }
.steam-container { min-height: 100vh; display: flex; flex-direction: column; }
.global-header { background: #171a21; height: 34px; display: flex; align-items: center; }
.global-nav { display: flex; justify-content: flex-end; width: 1200px; margin: 0 auto; } /* 这里的 width 虽然由 content-width 控制，但为了保险 */

.global-btn span { color: #b8b6b4 !important; font-size: 12px; font-weight: normal; transition: color 0.2s; }
.global-btn:hover span { color: #fff !important; text-decoration: underline; }
.user-welcome { color: #b8b6b4; font-size: 12px; margin-right: 10px; }
.user-welcome strong { color: #fff; }
.login-btn span { color: #c6d4df !important; }
.logout-btn { margin-left: 5px; }

.steam-header { background: #171a21; height: 104px; box-shadow: 0 0 10px rgba(0,0,0,0.4); position: relative; z-index: 100; }

/* ★★★ 核心修改：将页面宽度改为 1200px，提升宽屏体验 ★★★ */
.content-width { width: 1200px; margin: 0 auto; }

.header-inner { height: 100%; display: flex; align-items: center; }
.logo-wrapper { margin-right: 40px; cursor: pointer; }
.steam-logo-img { width: 176px; height: 44px; transition: filter 0.2s; }
.steam-logo-img:hover { filter: brightness(1.2); }
.nav-links { display: flex; height: 100%; align-items: center; }
.nav-item { text-decoration: none; color: #b8b6b4; font-size: 14px; font-weight: bold; text-transform: uppercase; padding: 10px 15px; transition: color 0.3s; letter-spacing: 0.05em; display: block; }
.nav-item:hover { color: var(--steam-accent); }
.router-link-active { color: #1a9fff; border-bottom: 3px solid #1a9fff; padding-bottom: 7px; }
.admin-link { color: #ff4c4c !important; text-shadow: 0 0 5px rgba(255, 76, 76, 0.3); }

.header-right-area { margin-left: auto; display: flex; align-items: center; gap: 10px; }
.steam-search-bar { display: flex; align-items: center; background-color: #316282; border-radius: 3px; padding: 1px; border: 1px solid rgba(0,0,0,0); transition: all 0.3s; height: 30px; }
.steam-search-bar:hover, .steam-search-bar:focus-within { background-color: #3d769c; border: 1px solid #66c0f4; }
.steam-search-bar input { background: transparent; border: none; color: #fff; padding: 0 10px; font-size: 13px; outline: none; width: 140px; font-family: "Motiva Sans", Arial, sans-serif; text-shadow: 1px 1px 0 rgba(0,0,0,0.2); font-style: italic; }
.steam-search-bar input::placeholder { color: #12212d; }
.steam-search-btn { background: transparent; border: none; cursor: pointer; padding: 0 8px; color: #66c0f4; display: flex; align-items: center; height: 100%; }
.steam-search-btn:hover { color: #fff; }

.steam-cart-box { background-color: #212c42; height: 32px; display: flex; align-items: center; padding: 0 12px; border-radius: 2px; cursor: pointer; transition: background-color 0.2s, transform 0.1s; box-shadow: 0 1px 2px rgba(0,0,0,0.3); }
.steam-cart-box:hover { background-color: #2a475e; filter: brightness(1.1); }
.steam-cart-box:active { transform: translateY(1px); }
.cart-icon { color: #66c0f4; margin-right: 6px; }
.cart-text { color: #c6d4df; font-size: 11px; font-weight: bold; text-transform: uppercase; margin-right: 8px; }
.cart-badge { background-color: #66c0f4; color: #000; font-size: 11px; font-weight: bold; padding: 1px 5px; border-radius: 2px; min-width: 16px; text-align: center; }

.main-content { flex: 1; }
.steam-footer { background-color: #171a21; padding: 40px 0; text-align: center; font-size: 12px; color: #8f98a0; margin-top: 0; border-top: 1px solid #363c44; }
.footer-text { padding-top: 20px; }
.footer-links { margin-top: 10px; }
.footer-links a { color: #c6d4df; text-decoration: none; margin: 0 5px; }
.footer-links a:hover { color: #fff; }
</style>