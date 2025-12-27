import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// 引入 Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
// 引入深色模式样式（Steam 风格必须）
import 'element-plus/theme-chalk/dark/css-vars.css'

const app = createApp(App)

app.use(router)
app.use(ElementPlus) // 使用 UI 库

app.mount('#app')