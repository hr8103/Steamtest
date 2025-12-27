import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import AdminGameView from '../views/AdminGameView.vue'
import GameDetailView from '../views/GameDetailView.vue'
import DeveloperView from '../views/DeveloperView.vue'
import CategoryView from '../views/CategoryView.vue'
import SearchResultsView from '../views/SearchResultsView.vue'
import CartView from '../views/CartView.vue'

// ★★★ 核心修复 1：引入支付成功回调页面 ★★★
import PaymentSuccess from '../views/PaymentSuccess.vue'

// ★★★ 核心修复 2：引入之前做好的商店浏览页面 (防止首页按钮点不动) ★★★
import StoreBrowseView from '../views/StoreBrowseView.vue'
import StoreCategoryView from '../views/StoreCategoryView.vue'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView
        },
        {
            path: '/login',
            name: 'login',
            component: LoginView
        },
        {
            path: '/admin',
            name: 'admin',
            component: AdminGameView,
            // 简单的路由守卫，防止非管理员直接输入网址访问
            beforeEnter: (to, from, next) => {
                const user = JSON.parse(localStorage.getItem('user') || '{}')
                if (user && user.role === 1) next()
                else next('/')
            }
        },
        {
            path: '/game/:id',
            name: 'game-detail',
            component: GameDetailView
        },
        {
            path: '/developer/:name',
            name: 'developer',
            component: DeveloperView
        },
        {
            path: '/category/:name', // 兼容旧版单个分类
            name: 'category',
            component: CategoryView
        },
        {
            path: '/search',
            name: 'search',
            component: SearchResultsView
        },
        {
            path: '/cart',
            name: 'cart',
            component: CartView
        },

        // ====================================================
        // ★★★ 修复重点：注册支付回调路由 ★★★
        // ====================================================
        {
            path: '/payment/success',
            name: 'payment-success',
            component: PaymentSuccess
        },

        // ====================================================
        // ★★★ 补充：注册新版商店浏览路由 (首页胶囊按钮需要) ★★★
        // ====================================================
        {
            path: '/store/featured',
            name: 'store-featured',
            component: StoreBrowseView,
            props: { mode: 'random', title: '精选和推荐' }
        },
        {
            path: '/store/special',
            name: 'store-special',
            component: StoreBrowseView,
            props: { mode: 'price_asc', title: '特别优惠' }
        },
        {
            path: '/store/categories',
            name: 'store-categories',
            component: StoreCategoryView
        }
    ],
    // 每次切换页面滚动条回到顶部
    scrollBehavior(to, from, savedPosition) {
        return { top: 0 }
    }
})

export default router