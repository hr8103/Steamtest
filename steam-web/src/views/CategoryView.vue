<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()
const categoryName = ref('')
const gameList = ref([])

const fetchGames = async () => {
  categoryName.value = route.params.name
  // 模拟搜索：获取所有游戏并在前端筛选包含该标签的
  const all = await request.get('/api/game/list')
  gameList.value = all.filter(g => g.tags && g.tags.includes(categoryName.value))
}

watch(() => route.params.name, fetchGames)
onMounted(fetchGames)
</script>

<template>
  <div class="cat-container">
    <div class="content-width">
      <div class="cat-header">
        <h2>正在浏览 {{ categoryName }} 标签下的游戏</h2>
      </div>
      <div class="game-grid">
        <div class="game-card" v-for="game in gameList" :key="game.id" @click="router.push(`/game/${game.id}`)">
          <div class="img-box"><img :src="game.coverUrl"></div>
          <div class="info">
            <div class="title">{{ game.title }}</div>
            <div class="price">$ {{ game.price }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.cat-container { background: #1b2838; min-height: 100vh; color: #fff; padding: 30px 0; }
.content-width { width: 940px; margin: 0 auto; }
.cat-header { border-bottom: 1px solid #3c4450; margin-bottom: 20px; padding-bottom: 10px; }
.game-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px; }
.game-card { background: rgba(0,0,0,0.2); cursor: pointer; transition: 0.2s; }
.game-card:hover { transform: scale(1.02); background: #2a3f5a; }
.img-box { height: 160px; overflow: hidden; }
.img-box img { width: 100%; height: 100%; object-fit: cover; }
.info { padding: 10px; }
.price { color: #66c0f4; }
</style>