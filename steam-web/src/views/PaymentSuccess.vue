<template>
  <div class="payment-result-container">
    <div class="content-box">
      <div v-if="loading">
        <h2>正在确认支付结果...</h2>
        <div class="loading-spinner"></div>
      </div>

      <div v-else-if="success">
        <div class="icon-success">✔</div>
        <h1 style="color: #66c0f4; margin: 20px 0;">感谢您对Steam的支持</h1>
        <p>您的交易已完成，游戏已添加至库中。</p>
        <button class="btn-steam-blue-large" @click="$router.push('/')">返回商店</button>
      </div>

      <div v-else>
        <h1 style="color: #ff4c4c;">支付确认失败</h1>
        <p>请联系客服或前往 PayPal 查看交易详情。</p>
        <button class="btn-steam-blue-large" @click="$router.push('/')">返回首页</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import request from '@/utils/request'
import { clearCart } from '@/utils/cart'

const route = useRoute()
const loading = ref(true)
const success = ref(false)

onMounted(async () => {
  const token = route.query.token
  if (token) {
    try {
      await request.post(`/api/order/capture?token=${token}`)
      success.value = true
      clearCart() // 支付成功，清空购物车
    } catch (e) {
      console.error('支付回调异常:', e)
      success.value = false
    } finally {
      loading.value = false
    }
  } else {
    loading.value = false
    success.value = false
  }
})
</script>

<style scoped>
.payment-result-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 80vh;
  background: #1b2838;
  color: #fff;
  font-family: Arial, sans-serif;
}
.content-box {
  background: #101822;
  padding: 50px;
  border-radius: 4px;
  text-align: center;
  box-shadow: 0 0 20px rgba(0,0,0,0.5);
  min-width: 450px;
}
.icon-success {
  font-size: 60px;
  color: #66c0f4; /* 改为 Steam 蓝 */
  margin-bottom: 20px;
}
/* 新版大号蓝色按钮 */
.btn-steam-blue-large {
  margin-top: 30px;
  padding: 15px 40px;
  background: linear-gradient(to right, #47bfff 0%, #1a44c2 100%);
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
  border-radius: 2px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.4);
}
.btn-steam-blue-large:hover {
  background: linear-gradient(to right, #66c0f4 0%, #1a44c2 100%);
}

.loading-spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #66c0f4;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 20px auto;
}
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>