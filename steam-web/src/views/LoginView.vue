<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request' // 必须确保 request.js 存在且配置正确

const router = useRouter()
const form = reactive({
  username: '',
  password: ''
})

const handleLogin = () => {
  if(!form.username || !form.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  // === 发送真实请求 ===
  request.post('/api/user/login', form).then(user => {
    // 进入这里说明登录成功 (code===200)
    // 后端返回的 user 对象里包含了 role 字段

    // 1. 存入浏览器缓存
    localStorage.setItem('user', JSON.stringify(user))

    // 2. 提示信息
    if (user.role === 1) {
      ElMessage.success('管理员登录成功，正在跳转后台...')
      router.push('/admin') // 管理员直接去后台
    } else {
      ElMessage.success(`欢迎回来，${user.nickname || user.username}`)
      router.push('/')      // 普通玩家去首页
    }

    // 3. 强制刷新页面，让 App.vue 里的导航栏更新（显示/隐藏后台按钮）
    setTimeout(() => {
      location.reload()
    }, 500)
  }).catch(err => {
    // 登录失败会在 request.js 被拦截并弹窗提示(如“密码错误”)
    console.error(err)
  })
}
</script>

<template>
  <div class="login-container">
    <div class="login-box">
      <h2>登录 Steam</h2>
      <div class="input-group">
        <label>用账户名称登录</label>
        <input v-model="form.username" type="text" placeholder="请输入账户名称" />
      </div>
      <div class="input-group">
        <label>密码</label>
        <input v-model="form.password" type="password" placeholder="输入密码" />
      </div>
      <button class="login-btn" @click="handleLogin">登录</button>
      <div class="login-help">
        <span @click="$router.push('/')">返回商店首页</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-container {
  height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #171a21; /* 确保背景色一致 */
}

.login-box {
  background: #181a21;
  padding: 50px;
  width: 400px;
  border-radius: 4px;
  color: #fff;
  box-shadow: 0 0 20px rgba(0,0,0,0.5);
}

h2 {
  color: #fff;
  font-weight: 200;
  letter-spacing: 2px;
  margin-bottom: 30px;
  text-transform: uppercase;
}

.input-group {
  margin-bottom: 20px;
}

label {
  display: block;
  color: #1999ff;
  font-size: 12px;
  margin-bottom: 5px;
  text-transform: uppercase;
  font-weight: bold;
}

input {
  width: 100%;
  background: #32353c;
  border: 1px solid #32353c;
  border-radius: 2px;
  color: #fff;
  padding: 10px;
  outline: none;
  transition: 0.2s;
}

input:focus {
  background: #393c44;
  border-color: #1999ff;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(to right, #47bfff 0%, #1a44c2 100%);
  border: none;
  color: #fff;
  font-size: 16px;
  border-radius: 2px;
  cursor: pointer;
  margin-top: 20px;
  font-weight: bold;
}

.login-btn:hover {
  filter: brightness(1.2);
}

.login-help {
  margin-top: 20px;
  text-align: center;
  font-size: 12px;
  color: #b8b6b4;
  cursor: pointer;
}
.login-help span:hover {
  color: #fff;
  text-decoration: underline;
}
</style>