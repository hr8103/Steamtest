<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const developerName = ref('')
const gameList = ref([])

// 获取该开发商的所有游戏
const fetchDeveloperGames = async () => {
  developerName.value = route.params.name
  // 注意：真实开发中应该后端写个 select * from game where developer = ?
  // 这里为了演示方便，我们在前端过滤（因为数据量不大）
  const allGames = await request.get('/api/game/list')
  gameList.value = allGames.filter(g => g.developer === developerName.value)
}

const goToDetail = (id) => {
  router.push(`/game/${id}`)
}

onMounted(() => {
  fetchDeveloperGames()
})

// 监听路由变化（防止同一个页面切换参数不刷新）
watch(() => route.params.name, () => {
  fetchDeveloperGames()
})
</script>

<template>
  <div class="dev-container">
    <div class="content-width">
      <div class="dev-header">
        <h1>{{ developerName }}</h1>
        <div class="dev-subtitle">开发商 / 发行商主页</div>
      </div>

      <div class="list-section">
        <h3>旗下作品 ({{ gameList.length }})</h3>

        <div v-if="gameList.length === 0" class="empty">
          该厂商暂无其他收录作品
        </div>

        <div class="game-grid">
          <div class="game-card" v-for="game in gameList" :key="game.id" @click="goToDetail(game.id)">
            <div class="card-img-wrapper">
              <img :src="game.coverUrl" class="card-img" />
            </div>
            <div class="card-info">
              <div class="game-title">{{ game.title }}</div>
              <div class="price-row">
                <span class="final-price">$ {{ game.price }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dev-container {
  min-height: 100vh;
  background: #1b2838;
  color: #fff;
  padding-bottom: 50px;
}
.content-width { width: 940px; margin: 0 auto; }

.dev-header {
  background: linear-gradient(to right, #2a475e, #1b2838);
  padding: 40px;
  margin-bottom: 30px;
  border-bottom: 1px solid #66c0f4;
}
.dev-header h1 { margin: 0; font-size: 36px; letter-spacing: 2px; }
.dev-subtitle { color: #66c0f4; margin-top: 10px; text-transform: uppercase; }

.list-section h3 { border-bottom: 1px solid #3c4450; padding-bottom: 10px; margin-bottom: 20px; }

.game-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
.game-card {
  background: rgba(0,0,0,0.2);
  cursor: pointer;
  transition: transform 0.2s;
}
.game-card:hover { transform: scale(1.02); background: rgba(0,0,0,0.4); }
.card-img-wrapper { height: 160px; overflow: hidden; }
.card-img { width: 100%; height: 100%; object-fit: cover; }
.card-info { padding: 15px; }
.game-title { font-size: 16px; margin-bottom: 10px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.final-price { color: #acdbf5; font-size: 16px; }
.empty { color: #888; padding: 50px; text-align: center; }
</style>